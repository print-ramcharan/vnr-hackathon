package com.codewithram.medvault.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

/**
 * A minimal in-memory CookieJar that stores cookies per host for the running process.
 * It captures Set-Cookie responses and sends Cookie headers on subsequent requests.
 * Not persisted across app restarts (sufficient for a short-lived session during testing).
 */
class SimpleCookieJar : CookieJar {
    private val cookieStore: MutableMap<String, MutableList<Cookie>> = ConcurrentHashMap()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val key = cookieKey(url)
        val list = cookieStore.getOrPut(key) { mutableListOf() }
        // replace existing cookies with same name
        for (cookie in cookies) {
            val idx = list.indexOfFirst { it.name == cookie.name && it.domain == cookie.domain }
            if (idx >= 0) list[idx] = cookie else list.add(cookie)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val key = cookieKey(url)
        val list = cookieStore[key] ?: return emptyList()
        val now = System.currentTimeMillis()
        // filter expired
        val fresh = list.filter { it.expiresAt >= now }
        cookieStore[key] = fresh.toMutableList()
        return fresh
    }

    private fun cookieKey(url: HttpUrl): String = url.host
}
