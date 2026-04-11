package com.tenko.myst.data.view

import android.service.autofill.UserData
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tenko.myst.data.api.ApiClient
import com.tenko.myst.data.serializable.Token
import com.tenko.myst.data.serializable.ForgotPasswordRequest
import com.tenko.myst.data.serializable.UserCreate
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

    fun forgotPassword(email: String, navController: NavController){
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.post("https://api-myst.onrender.com/auth/forgot-password"){
                    contentType(ContentType.Application.Json)
                    setBody(ForgotPasswordRequest(email))
                }
                if (response.status == HttpStatusCode.OK) {
                    loginError = "Correo de recuperación enviado"
                    navController.navigate(AppScreens.ForgotPasswordScreen.route)
                } else {
                    loginError = "Usuario no encontrado"
                }
            } catch (e: Exception) {
                loginError = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun verifyEmail(token: String, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.get("https://api-myst.onrender.com/auth/verify-email") {
                    parameter("token", token)
                }
                if (response.status == HttpStatusCode.OK) {
                    // Éxito: El backend devuelve un HTMLResponse
                    println("Email verificado con éxito")
                } else {
                    loginError = "Token inválido o expirado"
                }
            } catch (e: Exception) {
                loginError = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createUser(userData: UserCreate, navController: NavController){
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val response = ApiClient.client.post("https://api-myst.onrender.com/users/") {
                    contentType(ContentType.Application.Json)
                    setBody(userData)
                }

                if (response.status == HttpStatusCode.Created) {
                    // Registro exitoso. El usuario ahora debe revisar su correo.
                    // Podrías navegar a una pantalla de "Revisa tu correo"
                    navController.navigate(AppScreens.ValidateEmailScreen.route)
                } else {
                    // Manejar errores como "Email already registered"
                    loginError = "El correo ya está registrado o los datos son inválidos"
                }
            } catch (e: Exception) {
                loginError = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}