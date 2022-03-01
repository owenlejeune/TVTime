package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
import retrofit2.Response

interface DetailService {

    suspend fun getById(id: Int): Response<out DetailedItem>

    suspend fun getImages(id: Int): Response<ImageCollection>

    suspend fun getCastAndCrew(id: Int): Response<CastAndCrew>

    suspend fun getSimilar(id: Int, page: Int): Response<out HomePageResponse>

    suspend fun getVideos(id: Int): Response<VideoResponse>

    suspend fun getReviews(id: Int): Response<ReviewResponse>

    suspend fun postRating(id: Int, ratingBody: RatingBody): Response<RatingResponse>

    suspend fun deleteRating(id: Int): Response<RatingResponse>

}