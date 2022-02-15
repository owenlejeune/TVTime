package com.owenlejeune.tvtime.api.tmdb.model

abstract class DetailedItem(
    id: Int,
    title: String,
    posterPath: String?,
    @Transient open val backdropPath: String?,
    @Transient open val genres: List<Genre>,
    @Transient open val overview: String?,
    @Transient open val productionCompanies: List<ProductionCompany>,
    @Transient open val status: String,
    @Transient open val tagline: String?
): TmdbItem(id, title, posterPath)