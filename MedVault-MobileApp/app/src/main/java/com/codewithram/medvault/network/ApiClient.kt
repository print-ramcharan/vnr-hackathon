package com.codewithram.medvault.network

import android.content.Context
import com.codewithram.medvault.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val BASE_URL: String = try {
        BuildConfig.BACKEND_BASE
    } catch (e: Exception) {
        "http://10.100.27.122:8080/api/"
    }

    lateinit var apiService: ApiService
        private set

    private var initialized = false

    @Synchronized
    fun init(appContext: Context) {
        if (initialized) return

        SessionManager.init(appContext)

        // ✅ Capture Set-Cookie header from login response
        val cookieInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())

            response.headers("Set-Cookie").forEach { header ->
                if (header.contains("JSESSIONID")) {
                    SessionManager.saveSessionCookie(header)
                }
            }

            response
        }

        // ✅ Attach JSESSIONID cookie to all requests
        val authInterceptor = Interceptor { chain ->
            val builder: Request.Builder = chain.request().newBuilder()
            SessionManager.getSessionCookie()?.let {
                builder.addHeader("Cookie", it)
            }
            chain.proceed(builder.build())
        }

        // ✅ Logging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // ✅ Correct interceptor order
        val client = OkHttpClient.Builder()
            .addInterceptor(cookieInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        initialized = true
    }
}
