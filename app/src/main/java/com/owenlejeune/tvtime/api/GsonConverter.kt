package com.owenlejeune.tvtime.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KClass

class GsonConverter: Converter, KoinComponent {

    private val deserializers: Map<Class<*>, JsonDeserializer<*>> by inject()

    override fun get(): retrofit2.Converter.Factory {
//        val gson = GsonBuilder()
//            .registerTypeAdapter()
//            .create()
        val builder = GsonBuilder()

        deserializers.forEach { deserializer ->
            builder.registerTypeAdapter(deserializer.key, deserializer.value)
        }

        val gson = builder.create()

        return GsonConverterFactory.create(gson)
    }
}