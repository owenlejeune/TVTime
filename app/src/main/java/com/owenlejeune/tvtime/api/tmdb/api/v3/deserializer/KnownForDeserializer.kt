package com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer

import com.google.gson.*
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.KnownFor
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import java.lang.reflect.Type

class KnownForDeserializer: JsonDeserializer<KnownFor> {

    companion object {
        const val MEDIA_TYPE = "media_type"
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): KnownFor {
        if (json?.isJsonObject == true) {
            val obj = json.asJsonObject
            if (obj.has(MEDIA_TYPE)) {
                val typeStr = obj.get(MEDIA_TYPE).asString
                return when (Gson().fromJson(typeStr, MediaViewType::class.java)) {
                    MediaViewType.MOVIE -> Gson().fromJson(obj.toString(), KnownFor::class.java)
                    MediaViewType.TV -> Gson().fromJson(obj.toString(), KnownFor::class.java)
                    else -> throw JsonParseException("Not a valid MediaViewType: $typeStr")
                }
            }
            throw JsonParseException("JSON object has no property $MEDIA_TYPE")
        }
        throw JsonParseException("Not a JSON object")
    }

}