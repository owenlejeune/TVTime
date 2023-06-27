package com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.owenlejeune.tvtime.api.tmdb.api.BaseDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailCrew
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MovieCrew
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvCrew
import com.owenlejeune.tvtime.utils.types.MediaViewType

class DetailCrewDeserializer: BaseDeserializer<DetailCrew>() {

    override fun processJson(obj: JsonObject): DetailCrew {
        if (obj.has(MediaViewType.JSON_KEY)) {
            val typeStr = obj.get(MediaViewType.JSON_KEY).asString
            return when (gson.fromJson(typeStr, MediaViewType::class.java)) {
                MediaViewType.MOVIE -> gson.fromJson(obj.toString(), MovieCrew::class.java)
                MediaViewType.TV -> gson.fromJson(obj.toString(), TvCrew::class.java)
                else -> throw JsonParseException("Not a valid MediaViewType: $typeStr")
            }
        }
        throw JsonParseException("JSON object has no property ${MediaViewType.JSON_KEY}")
    }

}