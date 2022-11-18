package com.owenlejeune.tvtime.api

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import okhttp3.OkHttpClient

class DebugHttpClient: HttpClient {
    override val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(OkHttpProfilerInterceptor())
        .addNetworkInterceptor(StethoInterceptor())
        .build()
}