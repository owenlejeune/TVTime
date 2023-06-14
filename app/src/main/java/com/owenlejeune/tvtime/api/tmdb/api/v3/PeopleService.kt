package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailPerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ExternalIds
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePeopleResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PersonCreditsResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PersonImageCollection
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

    suspend fun getExternalIds(id: Int): Response<ExternalIds> {
        return service.getExternalIds(id)
    }

}