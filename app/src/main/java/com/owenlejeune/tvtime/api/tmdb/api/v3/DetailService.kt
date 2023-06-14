package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import retrofit2.Response

interface DetailService {

    suspend fun getById(id: Int): Response<out DetailedItem>

    suspend fun getImages(id: Int): Response<ImageCollection>

    suspend fun getCastAndCrew(id: Int): Response<CastAndCrew>

    suspend fun getSimilar(id: Int, page: Int): Response<out HomePageResponse>

    suspend fun getVideos(id: Int): Response<VideoResponse>

    suspend fun getReviews(id: Int): Response<ReviewResponse>

    suspend fun postRating(id: Int, ratingBody: RatingBody): Response<StatusResponse>

    suspend fun deleteRating(id: Int): Response<StatusResponse>

    suspend fun getKeywords(id: Int): Response<KeywordsResponse>

    suspend fun getWatchProviders(id: Int): Response<WatchProviderResponse>

    suspend fun getExternalIds(id: Int): Response<ExternalIds>

}