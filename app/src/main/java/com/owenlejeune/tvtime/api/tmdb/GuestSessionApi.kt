package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.RatedMediaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GuestSessionApi {

    @GET("guest_session/{session_id}/rated/movies")
    suspend fun getRatedMovies(@Path("session_id") sessionId: String): Response<RatedMediaResponse>

    @GET("guest_session/{session_id}/rated/tv")
    suspend fun getRatedTvShows(@Path("session_id") sessionId: String): Response<RatedMediaResponse>

    @GET("guest_session/{session_id}/rated/tv/episodes")
    suspend fun getRatedTvEpisodes(@Path("session_id") sessionId: String): Response<RatedMediaResponse>

}