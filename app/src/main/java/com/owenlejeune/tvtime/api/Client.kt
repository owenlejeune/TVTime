package com.owenlejeune.tvtime.api

import okhttp3.Interceptor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Retrofit

class Client(baseUrl: String): KoinComponent {

    private val converter: Converter by inject()
    private val client: HttpClient by inject()

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client.httpClient)
        .addConverterFactory(converter.get())
        .build()

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    fun addInterceptor(interceptor: Interceptor) {
        val iClient = client.httpClient.newBuilder()
            .addInterceptor(interceptor)
            .build()
        retrofit = retrofit.newBuilder()
            .client(iClient)
            .build()
    }

}