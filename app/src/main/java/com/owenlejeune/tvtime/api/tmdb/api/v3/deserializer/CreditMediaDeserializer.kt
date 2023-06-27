package com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.owenlejeune.tvtime.api.tmdb.api.BaseDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CreditMedia
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CreditMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CreditTv
import com.owenlejeune.tvtime.utils.types.MediaViewType

class CreditMediaDeserializer: BaseDeserializer<CreditMedia>() {

    override fun processJson(obj: JsonObject): CreditMedia {
        if (obj.has(MediaViewType.JSON_KEY)) {
            val typeStr = obj.get(MediaViewType.JSON_KEY).asString
            return when (gson.fromJson(typeStr, MediaViewType::class.java)) {
                MediaViewType.MOVIE -> gson.fromJson(obj.toString(), CreditMovie::class.java)
                MediaViewType.TV -> gson.fromJson(obj.toString(), CreditTv::class.java)
                else -> throw JsonParseException("Not a valid MediaViewType: $typeStr")
            }
        }
        throw JsonParseException("JSON object has no property ${MediaViewType.JSON_KEY}")
    }

}