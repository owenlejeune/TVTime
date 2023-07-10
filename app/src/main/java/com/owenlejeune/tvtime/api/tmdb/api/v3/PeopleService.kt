package com.owenlejeune.tvtime.api.tmdb.api.v3

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.owenlejeune.tvtime.api.LoadingState
import com.owenlejeune.tvtime.api.loadRemoteData
import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailCast
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailCrew
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailPerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ExternalIds
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePeopleResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PersonImage
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResult
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultPerson
import com.owenlejeune.tvtime.utils.types.TimeWindow
import org.koin.core.component.KoinComponent
import retrofit2.Response
import java.util.Collections

class PeopleService: KoinComponent {

    private val TAG = "PeopleService"

    private val service by lazy { TmdbClient().createPeopleService() }

    val peopleMap = Collections.synchronizedMap(mutableStateMapOf<Int, DetailPerson>())
    val castMap = Collections.synchronizedMap(mutableStateMapOf<Int, List<DetailCast>>())
    val crewMap = Collections.synchronizedMap(mutableStateMapOf<Int, List<DetailCrew>>())
    val imagesMap = Collections.synchronizedMap(mutableStateMapOf<Int, List<PersonImage>>())
    val externalIdsMap = Collections.synchronizedMap(mutableStateMapOf<Int, ExternalIds>())

    val detailsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val castCrewLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val imagesLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val externalIdsLoadingState = mutableStateOf(LoadingState.INACTIVE)

    suspend fun getPerson(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getPerson(id) },
            { peopleMap[id] = it },
            detailsLoadingState,
            refreshing
        )
    }

    suspend fun getCredits(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getCredits(id) },
            {
                castMap[id] = it.cast
                crewMap[id] = it.crew
            },
            castCrewLoadingState,
            refreshing
        )
    }

    suspend fun getImages(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getImages(id) },
            { imagesMap[id] = it.images },
            imagesLoadingState,
            refreshing
        )
    }

    suspend fun getExternalIds(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getExternalIds(id) },
            { externalIdsMap[id] = it },
            externalIdsLoadingState,
            refreshing
        )
    }

    suspend fun getPopular(page: Int): Response<HomePagePeopleResponse> {
        return service.getPopular(page)
    }

    suspend fun getTrending(timeWindow: TimeWindow, page: Int): Response<SearchResult<SearchResultPerson>> {
        return service.getTrending(timeWindow.name.lowercase(), page)
    }

}