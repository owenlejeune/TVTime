package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Collection
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search/company")
    suspend fun searchCompanies(@Query("query") query: String, @Query("page") page: Int): Response<SearchResult<ProductionCompany>>

    @GET("search/collection")
    suspend fun searchCollections(@Query("query") query: String, @Query("page") page: Int): Response<SearchResult<Collection>>

    @GET("search/keyword")
    suspend fun searchKeywords(@Query("query") query: String, @Query("page") page: Int): Response<SearchResult<Keyword>>

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String, @Query("page") page: Int): Response<SearchResult<SearchResultMovie>>

    @GET("search/tv")
    suspend fun searchTv(@Query("query") query: String, @Query("page") page: Int): Response<SearchResult<SearchResultTv>>

    @GET("search/person")
    suspend fun searchPeople(@Query("query") query: String, @Query("page") page: Int): Response<SearchResult<SearchResultPerson>>

    @GET("search/multi")
    suspend fun searchMulti(@Query("query") query: String, @Query("page") page: Int): Response<SearchResult<SortableSearchResult>>

}