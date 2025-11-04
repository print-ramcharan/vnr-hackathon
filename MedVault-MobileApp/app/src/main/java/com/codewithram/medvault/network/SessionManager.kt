package com.codewithram.medvault.network

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS = "medvault_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_ROLE = "role"
    private const val KEY_SESSION_COOKIE = "session_cookie"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null)
            prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun saveUser(username: String?, role: String?) {
        prefs?.edit()?.apply {
            putString(KEY_USERNAME, username)
            putString(KEY_ROLE, role)
            apply()
        }
    }

    fun getUsername(): String? = prefs?.getString(KEY_USERNAME, null)
    fun getRole(): String? = prefs?.getString(KEY_ROLE, null)

    fun saveSessionCookie(cookieValue: String?) {
        prefs?.edit()?.putString(KEY_SESSION_COOKIE, cookieValue)?.apply()
    }

    fun getSessionCookie(): String? = prefs?.getString(KEY_SESSION_COOKIE, null)

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}
