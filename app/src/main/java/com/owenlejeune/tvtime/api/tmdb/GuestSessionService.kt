package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
import retrofit2.Response

class GuestSessionService {

    private val service by lazy { TmdbClient().createGuestSessionService() }

    suspend fun getRatedMovies(sessionId: String): Response<RatedMediaResponse<RatedMovie>> {
        return service.getRatedMovies(sessionId = sessionId)
    }

    suspend fun getRatedTvShows(sessionId: String): Response<RatedMediaResponse<RatedTv>> {
        return service.getRatedTvShows(sessionId = sessionId)
    }

    suspend fun getRatedTvEpisodes(sessionId: String): Response<RatedMediaResponse<RatedEpisode>> {
        return service.getRatedTvEpisodes(sessionId = sessionId)
    }

}