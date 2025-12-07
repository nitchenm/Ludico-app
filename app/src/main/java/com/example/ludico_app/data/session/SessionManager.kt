package com.example.ludico_app.data.session

import android.content.Context
import android.content.SharedPreferences
import com.example.ludico_app.R

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID_KEY = "user_id"
    }

    /**
     * Guarda el token de autenticación del usuario.
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Obtiene el token de autenticación del usuario.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveUserId(userId: String) {
        val editor = prefs.edit()
        editor.putString(USER_ID_KEY, userId)
        editor.apply()
    }

    fun fetchUserId(): String? {
        return prefs.getString(USER_ID_KEY, null)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.remove(USER_ID_KEY)
        editor.apply()
    }
}
