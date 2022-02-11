package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
import org.koin.core.component.KoinComponent
import retrofit2.Response

class TvService: KoinComponent, DetailService {

    private val service by lazy { TmdbClient().createTvService() }

    suspend fun getPopularTv(page: Int = 1) = service.getPoplarTv(page)

    override suspend fun getById(id: Int): Response<DetailedItem> {
        return service.getTvShowById(id) as Response<DetailedItem>
    }
}