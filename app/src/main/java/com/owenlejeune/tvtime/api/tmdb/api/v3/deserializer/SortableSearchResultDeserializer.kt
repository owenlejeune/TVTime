package com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.owenlejeune.tvtime.api.tmdb.api.BaseDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultPerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultTv
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SortableSearchResult
import com.owenlejeune.tvtime.utils.types.MediaViewType

class SortableSearchResultDeserializer: BaseDeserializer<SortableSearchResult>() {

    override fun processJson(obj: JsonObject): SortableSearchResult {
        if (obj.has(MediaViewType.JSON_KEY)) {
            val typeStr = obj.get(MediaViewType.JSON_KEY).asString
            return when (gson.fromJson(typeStr, MediaViewType::class.java)) {
                MediaViewType.PERSON -> gson.fromJson(obj.toString(), SearchResultPerson::class.java)
                MediaViewType.MOVIE -> gson.fromJson(obj.toString(), SearchResultMovie::class.java)
                MediaViewType.TV -> gson.fromJson(obj.toString(), SearchResultTv::class.java)
                else -> throw JsonParseException("Not a valid MediaViewType: $typeStr")
            }
        }
        throw JsonParseException("JSON object has no property ${MediaViewType.JSON_KEY}")
    }

}