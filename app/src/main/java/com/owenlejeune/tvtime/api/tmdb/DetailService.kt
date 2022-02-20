package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.CastAndCrew
import com.owenlejeune.tvtime.api.tmdb.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.model.HomePageResponse
import retrofit2.Response

interface DetailService {

    suspend fun getById(id: Int): Response<out DetailedItem>

    suspend fun getImages(id: Int): Response<ImageCollection>

    suspend fun getCastAndCrew(id: Int): Response<CastAndCrew>

    suspend fun getSimilar(id: Int, page: Int): Response<out HomePageResponse>

}