package com.owenlejeune.tvtime.api

import okhttp3.OkHttpClient

interface HttpClient {
    val httpClient: OkHttpClient
}