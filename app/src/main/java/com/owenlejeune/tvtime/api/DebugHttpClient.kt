package com.owenlejeune.tvtime.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class DebugHttpClient: HttpClient {
    override val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(getLoggingInterceptor())
        .build()

    private fun getLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}