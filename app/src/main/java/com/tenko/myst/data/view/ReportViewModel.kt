package com.tenko.myst.data.view

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.tenko.myst.data.api.ApiClient
import com.tenko.myst.data.model.ReportItem
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri

class ReportViewModel : ViewModel() {
    // --- ESTADOS DE LA UI ---
    var savedReports = mutableStateListOf<ReportItem>()
    var isLoading by mutableStateOf(false)
    var reportError by mutableStateOf<String?>(null)

    /**
     * Genera el PDF desde el API y lo sube a la carpeta del usuario.
     * Solo necesitamos el userId (del AuthViewModel) y opcionalmente el cycleId.
     */
    fun generateAndSaveReport(userId: String, cycleId: Int? = null) {
        viewModelScope.launch {
            isLoading = true
            reportError = null

            executeWithRetry {
                val url = if (cycleId == null)
                    "https://api-myst.onrender.com/reports/full-report"
                else
                    "https://api-myst.onrender.com/reports/cycle-report/$cycleId"

                val response = ApiClient.client.get(url)

                if (response.status == HttpStatusCode.OK) {
                    val pdfBytes: ByteArray = response.body()
                    val fileName = if (cycleId == null) "reporte_clinico_${System.currentTimeMillis()}.pdf"
                    else "ciclo_${cycleId}_${System.currentTimeMillis()}.pdf"

                    // Subida a Firebase en la ruta reports/userId/fileName
                    uploadPdfToFirebase(pdfBytes, fileName, userId)
                    fetchSavedReports(userId) // Refrescar lista local
                    true
                } else false
            }
            isLoading = false
        }
    }

    /**
     * Lista los archivos dentro de la carpeta específica del usuario.
     */
    fun fetchSavedReports(userId: String) {
        viewModelScope.launch {
            executeWithRetry {
                try {
                    val storageRef = Firebase.storage.reference.child("reports/$userId")
                    val listResult = storageRef.listAll().await()

                    val tempItems = listResult.items.map { item ->
                        val url = item.downloadUrl.await().toString()
                        val metadata = item.metadata.await()
                        ReportItem(item.name, url, metadata.creationTimeMillis)
                    }

                    savedReports.clear()
                    savedReports.addAll(tempItems.sortedByDescending { it.date })
                    true
                } catch (e: Exception) {
                    // Si falla porque la carpeta no existe aún, lo tratamos como éxito vacío
                    savedReports.clear()
                    true
                }
            }
        }
    }

    private suspend fun uploadPdfToFirebase(bytes: ByteArray, fileName: String, userId: String) {
        val storageRef = Firebase.storage.reference.child("reports/$userId/$fileName")
        storageRef.putBytes(bytes).await()
    }

    /**
     * Lógica para abrir el PDF externamente
     */
    fun openPdf(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(url.toUri(), "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(intent, "Abrir con..."))
    }

    // --- LÓGICA DE ROBUSTEZ REUTILIZADA ---

    private suspend fun executeWithRetry(action: suspend () -> Boolean) {
        var retryCount = 0
        val maxAttempts = 3
        var success = false

        while (retryCount < maxAttempts && !success) {
            try {
                if (action()) {
                    success = true
                    reportError = null
                } else {
                    retryCount++
                    delay(1000)
                }
            } catch (e: Exception) {
                retryCount++
                if (retryCount >= maxAttempts) {
                    reportError = "Error: ${e.localizedMessage}"
                }
                delay(1000)
            }
        }
    }
}