package com.tenko.myst.data.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.tenko.myst.data.api.ApiClient
import com.tenko.myst.data.api.TokenManager
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReportViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var reportError by mutableStateOf<String?>(null)

    // Lista para mostrar en la LazyColumn
    val savedReports = mutableStateListOf<ReportItem>()

    data class ReportItem(val name: String, val url: String, val date: Long)

    /**
     * Descarga el reporte del API y lo sube a Firebase
     */
    fun generateAndSaveReport(tokenManager: TokenManager, cycleId: Int? = null) {
        viewModelScope.launch {
            isLoading = true
            reportError = null

            try {
                // LÃ³gica de reintentos para el Token (3 intentos)
                var token: String? = null
                repeat(3) { attempt ->
                    token = tokenManager.getToken.toString()
                    delay(1000) // Esperar 1 segundo entre intentos
                    return@repeat
                }

                if (token == null) {
                    reportError = "No se pudo recuperar el token de acceso."
                    return@launch
                }

                // Determinar endpoint
                val url = if (cycleId == null)
                    "https://api-myst.onrender.com/reports/full-report"
                else
                    "https://api-myst.onrender.com/reports/cycle-report/$cycleId"

                // 1. Obtener el PDF como ByteArray desde FastAPI
                val response = ApiClient.client.get(url)

                if (response.status == HttpStatusCode.OK) {
                    val pdfBytes: ByteArray = response.body()
                    val fileName = if (cycleId == null) "historial_clinico_${System.currentTimeMillis()}.pdf"
                    else "ciclo_${cycleId}_${System.currentTimeMillis()}.pdf"

                    // 2. Subir a Firebase Storage
                    uploadPdfToFirebase(pdfBytes, fileName)
                    fetchSavedReports() // Actualizar lista
                } else {
                    reportError = "Error del servidor: ${response.status.description}"
                }

            } catch (e: Exception) {
                reportError = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun uploadPdfToFirebase(bytes: ByteArray, fileName: String) {
        val storageRef = Firebase.storage.reference.child("reports/$fileName")
        storageRef.putBytes(bytes).await()
    }

    /**
     * Lista todos los reportes del usuario en Firebase
     */
    fun fetchSavedReports() {
        viewModelScope.launch {
            try {
                val storageRef = Firebase.storage.reference.child("reports")
                val listResult = storageRef.listAll().await()

                savedReports.clear()
                listResult.items.forEach { item ->
                    val url = item.downloadUrl.await().toString()
                    val metadata = item.metadata.await()
                    savedReports.add(ReportItem(item.name, url, metadata.creationTimeMillis))
                }
                // Ordenar por fecha reciente
                savedReports.sortByDescending { it.date }
            } catch (e: Exception) {
                reportError = "Error al listar reportes: ${e.localizedMessage}"
            }
        }
    }
}