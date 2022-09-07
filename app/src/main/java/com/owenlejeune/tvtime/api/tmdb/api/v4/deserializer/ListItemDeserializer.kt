package com.owenlejeune.tvtime.api.tmdb.api.v4.deserializer

import com.google.gson.*
import com.owenlejeune.tvtime.api.tmdb.api.BaseDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListItem
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListTv
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType

class ListItemDeserializer: BaseDeserializer<ListItem>() {

    override fun processJson(obj: JsonObject): ListItem {
        if (obj.has(MediaViewType.JSON_KEY)) {
            val typeStr = obj.get(MediaViewType.JSON_KEY).asString
            return when (gson.fromJson(typeStr, MediaViewType::class.java)) {
                MediaViewType.MOVIE -> gson.fromJson(obj.toString(), ListMovie::class.java)
                MediaViewType.TV -> gson.fromJson(obj.toString(), ListTv::class.java)
                else -> throw JsonParseException("Not a valid MediaViewType: $typeStr")
            }
        }
        throw JsonParseException("JSON object has no property ${MediaViewType.JSON_KEY}")
    }

}