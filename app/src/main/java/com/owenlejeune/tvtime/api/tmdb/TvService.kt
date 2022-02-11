package com.owenlejeune.tvtime.api.tmdb

import org.koin.core.component.KoinComponent

class TvService: KoinComponent {

    private val service by lazy { TmdbClient().createTvService() }

    suspend fun getPopularTv(page: Int = 1) = service.getPoplarTv(page)
}