package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedEpisode
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedMediaResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedTv
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GuestSessionApi {

    @GET("guest_session/{session_id}/rated/movies")
    suspend fun getRatedMovies(@Path("session_id") sessionId: String): Response<RatedMediaResponse<RatedMovie>>

    @GET("guest_session/{session_id}/rated/tv")
    suspend fun getRatedTvShows(@Path("session_id") sessionId: String): Response<RatedMediaResponse<RatedTv>>

    @GET("guest_session/{session_id}/rated/tv/episodes")
    suspend fun getRatedTvEpisodes(@Path("session_id") sessionId: String): Response<RatedMediaResponse<RatedEpisode>>

}