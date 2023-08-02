package com.owenlejeune.tvtime.api.common

import com.google.gson.Gson
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory

class GsonConverter: ConverterFactoryFactory, KoinComponent {

    private val gson: Gson by inject()

    override fun get(): Converter.Factory = GsonConverterFactory.create(gson)

}