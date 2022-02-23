package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.DetailPerson
import com.owenlejeune.tvtime.api.tmdb.model.PersonCreditsResponse
import com.owenlejeune.tvtime.api.tmdb.model.PersonImageCollection
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PeopleApi {

    @GET("person/{id}")
    suspend fun getPerson(@Path("id") id: Int): Response<DetailPerson>

//    @GET("person/{id}/movie_credits")
//    suspend fun getMovieCredits(@Path("id") id: Int)
//
//    @GET("person/{id}/tv_credits")
//    suspend fun getTvCredits(@Path("id") id: Int)

    @GET("person/{id}/combined_credits")
    suspend fun getCredits(@Path("id") id: Int): Response<PersonCreditsResponse>

    @GET("person/{id}/images")
    suspend fun getImages(@Path("id") id: Int): Response<PersonImageCollection>

//    @GET("persons/{id}/tagged_images")
//    suspend fun getTaggedImages(@Path("id") id: Int, @Query("page") page: Int = 1): Response<PersonImageCollection>

}