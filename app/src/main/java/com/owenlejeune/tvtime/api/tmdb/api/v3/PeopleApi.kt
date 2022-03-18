package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailPerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePeopleResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PersonCreditsResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PersonImageCollection
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("person/popular")
    suspend fun getPopular(@Query("page") page: Int = 1): Response<HomePagePeopleResponse>

//    @GET("persons/{id}/tagged_images")
//    suspend fun getTaggedImages(@Path("id") id: Int, @Query("page") page: Int = 1): Response<PersonImageCollection>

}