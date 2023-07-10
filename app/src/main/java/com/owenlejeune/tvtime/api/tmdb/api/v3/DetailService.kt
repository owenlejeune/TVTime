package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import retrofit2.Response

interface DetailService {

    suspend fun getById(id: Int, refreshing: Boolean)

    suspend fun getImages(id: Int, refreshing: Boolean)

    suspend fun getCastAndCrew(id: Int, refreshing: Boolean)

    suspend fun getSimilar(id: Int, page: Int): Response<out HomePageResponse>

    suspend fun getVideos(id: Int, refreshing: Boolean)

    suspend fun getReviews(id: Int, refreshing: Boolean)

    suspend fun postRating(id: Int, ratingBody: RatingBody)

    suspend fun deleteRating(id: Int)

    suspend fun getKeywords(id: Int, refreshing: Boolean)

    suspend fun getWatchProviders(id: Int, refreshing: Boolean)

    suspend fun getExternalIds(id: Int, refreshing: Boolean)

    suspend fun getAccountStates(id: Int)

    suspend fun discover(keywords: String? = null, page: Int): Response<out SearchResult<out SearchResultMedia>>

}