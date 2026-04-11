package com.tenko.myst.data.view

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tenko.myst.data.api.ApiClient
import com.tenko.myst.data.serializable.Token
import com.tenko.myst.navigation.AppScreens
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var tokenData by mutableStateOf<Token?>(null)

    fun login(email: String, password: String, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val response = ApiClient.client.submitForm(
                    url = "https://api-myst.onrender.com/auth/login",
                    formParameters = parameters {
                        append("username", email)
                        append("password", password)
                    }
                )

                if (response.status == HttpStatusCode.OK) {
                    tokenData = response.body<Token>()
                    navController.navigate(AppScreens.MainScreen.route)
                    // Aquí guardarías el token (ej. en SharedPreferences o DataStore)
                } else {
                    loginError = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                loginError = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}