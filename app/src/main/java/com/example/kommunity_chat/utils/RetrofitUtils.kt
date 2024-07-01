package com.example.kommunity_chat.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor


object LoggingInterceptor : Interceptor {
    private val logger = HttpLoggingInterceptor.Logger { message ->
        Log.d("fitLvl.dev", message)
    }

    private val loggingInterceptor = HttpLoggingInterceptor(logger).apply {
        level = HttpLoggingInterceptor.Level.BODY // You can change the log level as needed
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return loggingInterceptor.intercept(chain)
    }
}

object AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val modifiedRequest =
            originalRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json; charset=utf-8")
                .build()

        return chain.proceed(modifiedRequest)
    }
}