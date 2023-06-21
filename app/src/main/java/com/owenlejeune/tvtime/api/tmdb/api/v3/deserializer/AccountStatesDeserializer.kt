package com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer

import com.google.gson.JsonObject
import com.owenlejeune.tvtime.api.tmdb.api.BaseDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AccountStates

class AccountStatesDeserializer: BaseDeserializer<AccountStates>() {

    override fun processJson(obj: JsonObject): AccountStates {
        val id = obj.get("id").asInt
        val isFavorite = obj.get("favorite").asBoolean
        val isWatchlist = obj.get("watchlist").asBoolean
        return try {
            val isRated = obj.get("rated").asBoolean
            AccountStates(id, isFavorite, isWatchlist, isRated, -1)
        } catch (e: Exception) {
            val rating = obj.get("rated").asJsonObject.get("value").asInt
            AccountStates(id, isFavorite, isWatchlist, true, rating)
        }
    }

}