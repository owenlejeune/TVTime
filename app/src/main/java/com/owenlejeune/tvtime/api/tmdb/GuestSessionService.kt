package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.RatedMedia
import com.owenlejeune.tvtime.api.tmdb.model.RatedMediaResponse
import retrofit2.Response

class GuestSessionService {

    private val service by lazy { TmdbClient().createGuestSessionService() }

    suspend fun getRatedMovies(sessionId: String): Response<RatedMediaResponse> {
        return service.getRatedMovies(sessionId = sessionId).apply {
            body()?.results?.forEach { it.type = RatedMedia.Type.MOVIE }
        }
    }

    suspend fun getRatedTvShows(sessionId: String): Response<RatedMediaResponse> {
        return service.getRatedTvShows(sessionId = sessionId).apply {
            body()?.results?.forEach { it.type = RatedMedia.Type.SERIES }
        }
    }

    suspend fun getRatedTvEpisodes(sessionId: String): Response<RatedMediaResponse> {
        return service.getRatedTvEpisodes(sessionId = sessionId).apply {
            body()?.results?.forEach { it.type = RatedMedia.Type.EPISODE }
        }
    }

}