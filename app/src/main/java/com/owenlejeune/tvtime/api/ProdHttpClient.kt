package com.owenlejeune.tvtime.api

import okhttp3.OkHttpClient

class ProdHttpClient: HttpClient {
    override val httpClient: OkHttpClient = OkHttpClient.Builder().build()
}