package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.DetailPerson
import com.owenlejeune.tvtime.api.tmdb.model.HomePagePeopleResponse
import com.owenlejeune.tvtime.api.tmdb.model.PersonCreditsResponse
import com.owenlejeune.tvtime.api.tmdb.model.PersonImageCollection
import org.koin.core.component.KoinComponent
import retrofit2.Response

class PeopleService: KoinComponent {

    private val service by lazy { TmdbClient().createPeopleService() }

    suspend fun getPerson(id: Int): Response<DetailPerson> {
        return service.getPerson(id)
    }

    suspend fun getCredits(id: Int): Response<PersonCreditsResponse> {
        return service.getCredits(id)
    }

    suspend fun getImages(id: Int): Response<PersonImageCollection> {
        return service.getImages(id)
    }

    suspend fun getPopular(page: Int = 1): Response<HomePagePeopleResponse> {
        return service.getPopular(page)
    }

}