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
import com.tenko.myst.data.serializable.UserDelete
import com.tenko.myst.data.serializable.UserResponse
import com.tenko.myst.data.serializable.UserUpdate
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

    fun resetPassword(token: String, newPassword: String, navController: NavController){
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                // Usamos submitForm porque tu API espera Form(...)
                val response = ApiClient.client.submitForm(
                    url = "https://api-myst.onrender.com/auth/reset-password",
                    formParameters = parameters {
                        append("token", token)
                        append("new_password", newPassword)
                    }
                )

                if (response.status == HttpStatusCode.OK) {
                    // Éxito: Navegamos al login o mostramos mensaje
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                } else {
                    // Aquí capturamos el "Token inválido" o "Token expirado"
                    loginError = "Error: ${response.status.description}"
                }
            } catch (e: Exception) {
                loginError = "Error de conexión: ${e.localizedMessage}"
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

    fun getUser(token: String){
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.get("https://api-myst.onrender.com/users/me") {
                    // Enviamos el token de autenticación
                    bearerAuth(token)
                }
                if (response.status == HttpStatusCode.OK) {
                    val userProfile = response.body<UserResponse>()
                    // Aquí puedes guardar el perfil en una variable de estado
                    println("Usuario obtenido: ${userProfile.name}")
                }
            } catch (e: Exception) {
                loginError = "Error al obtener perfil: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateUser(token: String, userId: Int, updateData: UserUpdate) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.patch("https://api-myst.onrender.com/users/$userId") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(updateData)
                }
                if (response.status == HttpStatusCode.OK) {
                    val updatedUser = response.body<UserResponse>()
                    println("Usuario actualizado: ${updatedUser.name}")
                }
            } catch (e: Exception) {
                loginError = "Error al actualizar: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteUser(token: String, passwordConfirm: String, navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.client.delete("https://api-myst.onrender.com/users/me") {
                    bearerAuth(token)
                    contentType(ContentType.Application.Json)
                    setBody(UserDelete(password = passwordConfirm))
                }

                if (response.status == HttpStatusCode.NoContent) {
                    // Borrado exitoso, regresamos al inicio
                    navController.navigate(AppScreens.LoginScreen.route) {
                        popUpTo(0) // Limpia el historial de navegación
                    }
                } else if (response.status == HttpStatusCode.BadRequest) {
                    loginError = "Contraseña incorrecta"
                }
            } catch (e: Exception) {
                loginError = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}