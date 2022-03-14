package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedEpisode
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedMediaResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedTv
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