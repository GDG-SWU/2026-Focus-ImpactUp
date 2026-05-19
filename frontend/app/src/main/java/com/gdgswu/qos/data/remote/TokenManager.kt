package com.gdgswu.qos.data.remote

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "qos_token"
    private const val KEY_TOKEN  = "bearer_token"
    private const val KEY_USER_ID = "user_id"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveToken(context: Context, token: String) {
        prefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? =
        prefs(context).getString(KEY_TOKEN, null)

    fun saveUserId(context: Context, userId: String) {
        prefs(context).edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): String? =
        prefs(context).getString(KEY_USER_ID, null)

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun isLoggedIn(context: Context): Boolean = getToken(context) != null
}
