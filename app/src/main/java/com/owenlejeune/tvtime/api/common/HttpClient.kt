package com.owenlejeune.tvtime.api.common

import okhttp3.OkHttpClient

interface HttpClient {
    val httpClient: OkHttpClient
}