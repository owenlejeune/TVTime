package com.owenlejeune.tvtime.api

import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory

class GsonConverter: Converter {
    override fun get(): retrofit2.Converter.Factory {
        val gson = GsonBuilder()
            .create()

        return GsonConverterFactory.create(gson)
    }
}