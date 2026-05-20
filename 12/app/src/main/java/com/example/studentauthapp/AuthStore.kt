package com.example.studentauthapp

import android.content.Context

object AuthStore {
    private const val PREFS_NAME = "student_auth_store"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_FULL_NAME = "full_name"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(context: Context): Boolean = prefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun hasRegisteredAccount(context: Context): Boolean {
        val sharedPreferences = prefs(context)
        return sharedPreferences.contains(KEY_EMAIL) && sharedPreferences.contains(KEY_PASSWORD)
    }

    fun saveAccount(context: Context, fullName: String, email: String, password: String) {
        prefs(context).edit()
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun attemptLogin(context: Context, email: String, password: String): Boolean {
        val sharedPreferences = prefs(context)
        val savedEmail = sharedPreferences.getString(KEY_EMAIL, null)
        val savedPassword = sharedPreferences.getString(KEY_PASSWORD, null)
        val matches = email == savedEmail && password == savedPassword

        if (matches) {
            sharedPreferences.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .apply()
        }

        return matches
    }

    fun logout(context: Context) {
        prefs(context).edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .apply()
    }

    fun clearAll(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun getFullName(context: Context): String? = prefs(context).getString(KEY_FULL_NAME, null)
}