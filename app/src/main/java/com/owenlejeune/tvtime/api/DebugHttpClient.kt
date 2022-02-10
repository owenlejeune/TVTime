package com.owenlejeune.tvtime.api

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient

class DebugHttpClient: HttpClient {
    override val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .build()
}