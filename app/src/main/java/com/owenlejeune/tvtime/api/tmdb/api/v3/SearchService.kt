package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Collection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import java.util.*

class SearchService: KoinComponent {

    private val service: SearchApi by inject()

    suspend fun searchCompanies(query: String, page: Int = 1): Response<SearchResult<ProductionCompany>> {
        return service.searchCompanies(query, page)
    }

    suspend fun searchCollections(query: String, page: Int = 1): Response<SearchResult<Collection>> {
        return service.searchCollections(query, page)
    }

    suspend fun searchKeywords(query: String, page: Int = 1): Response<SearchResult<Keyword>> {
        return service.searchKeywords(query, page)
    }

    suspend fun searchMovies(query: String, page: Int = 1): Response<SearchResult<SearchResultMovie>> {
        return service.searchMovies(query, page)
    }

    suspend fun searchTv(query: String, page: Int = 1): Response<SearchResult<SearchResultTv>> {
        return service.searchTv(query, page)
    }

    suspend fun searchPeople(query: String, page: Int = 1): Response<SearchResult<SearchResultPerson>> {
        return service.searchPeople(query, page)
    }

    suspend fun searchMulti(query: String, page: Int = 1): Response<SearchResult<SortableSearchResult>> {
        return service.searchMulti(query, page)
    }

}