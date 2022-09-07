package com.owenlejeune.tvtime.api.tmdb.api

import com.google.gson.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.reflect.Type

abstract class BaseDeserializer<T>: JsonDeserializer<T>, KoinComponent {

    protected val gson: Gson by inject()

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): T {
        if (json?.isJsonObject == true) {
            val obj = json.asJsonObject
            return processJson(obj)
        }
        throw JsonParseException("Not a json object")
    }

    protected abstract fun processJson(obj: JsonObject): T

}