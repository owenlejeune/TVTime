package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
import retrofit2.Response

interface DetailService {

    suspend fun getById(id: Int): Response<DetailedItem>

}