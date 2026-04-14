package com.tenko.myst.data.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenManager(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    // Guardar el token
    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    // Eliminar el token
    suspend fun deleteToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }

    // Leer el token (como un Flow para que la UI reaccione a cambios)
    val getToken: Flow<String?> = context.dataStore.data
        .map { it[TOKEN_KEY] }
}
