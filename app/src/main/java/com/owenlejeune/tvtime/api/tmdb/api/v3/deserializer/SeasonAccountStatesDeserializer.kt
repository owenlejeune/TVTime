package com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer

import com.google.gson.JsonObject
import com.owenlejeune.tvtime.api.tmdb.api.BaseDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SeasonAccountStatesResult

class SeasonAccountStatesResultDeserializer: BaseDeserializer<SeasonAccountStatesResult>() {

    override fun processJson(obj: JsonObject): SeasonAccountStatesResult {
        val id = obj.get("id").asInt
        val episodeNumber = obj.get("episode_number").asInt
        return try {
            val isRated = obj.get("rated").asBoolean
            SeasonAccountStatesResult(id, episodeNumber, isRated, -1)
        } catch (e: Exception) {
            val rating = obj.get("rated").asJsonObject.get("value").asInt
            SeasonAccountStatesResult(id, episodeNumber, true, rating)
        }
    }

}