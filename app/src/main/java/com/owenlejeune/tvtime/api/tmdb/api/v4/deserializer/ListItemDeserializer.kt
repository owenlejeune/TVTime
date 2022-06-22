package com.owenlejeune.tvtime.api.tmdb.api.v4.deserializer

import com.google.gson.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListItem
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListTv
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import java.lang.reflect.Type

class ListItemDeserializer: JsonDeserializer<ListItem> {

    companion object {
        const val MEDIA_TYPE = "media_type"
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ListItem? {
        if (json?.isJsonObject == true) {
            val obj = json.asJsonObject
            if (obj.has(MEDIA_TYPE)) {
                val typeStr = obj.get(MEDIA_TYPE).asString
                return when (Gson().fromJson(typeStr, MediaViewType::class.java)) {
                    MediaViewType.MOVIE -> Gson().fromJson(obj.toString(), ListMovie::class.java)
                    MediaViewType.TV -> Gson().fromJson(obj.toString(), ListTv::class.java)
                    else -> throw JsonParseException("Not a valid MediaViewType: $typeStr")
                }
            }
            throw JsonParseException("JSON object has no property $MEDIA_TYPE")
        }
        throw JsonParseException("Not a JSON object")
    }
}