package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.CastAndCrew
import com.owenlejeune.tvtime.api.tmdb.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.model.TvContentRatings
import org.koin.core.component.KoinComponent
import retrofit2.Response

class TvService: KoinComponent, DetailService {

    private val service by lazy { TmdbClient().createTvService() }

    suspend fun getPopularTv(page: Int = 1) = service.getPoplarTv(page)

    override suspend fun getById(id: Int): Response<out DetailedItem> {
        return service.getTvShowById(id)
    }

    override suspend fun getImages(id: Int): Response<ImageCollection> {
        return service.getTvImages(id)
    }

    override suspend fun getCastAndCrew(id: Int): Response<CastAndCrew> {
        return service.getCastAndCrew(id)
    }

    suspend fun getContentRatings(id: Int): Response<TvContentRatings> {
        return service.getContentRatings(id)
    }
}