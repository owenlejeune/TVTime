package com.owenlejeune.tvtime.api.common

import okhttp3.OkHttpClient

class ProdHttpClient: HttpClient {
    override val httpClient: OkHttpClient = OkHttpClient.Builder().build()
}