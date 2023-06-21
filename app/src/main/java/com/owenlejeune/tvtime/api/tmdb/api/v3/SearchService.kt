package com.owenlejeune.tvtime.api.tmdb.api.v3

import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Collection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Keyword
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ProductionCompany
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResult
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultPerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultTv
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Searchable
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SortableSearchResult
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

class SearchService: KoinComponent {

    private val service: SearchService by inject()

    val movieResults = mutableStateOf<Flow<PagingData<SearchResultMovie>>?>(null)
    val tvResults = mutableStateOf<Flow<PagingData<SearchResultTv>>?>(null)
    val peopleResults = mutableStateOf<Flow<PagingData<SearchResultPerson>>?>(null)
    val multiResults = mutableStateOf<Flow<PagingData<SortableSearchResult>>?>(null)

    fun searchCompanies(query: String, page: Int = 1): Response<SearchResult<ProductionCompany>> {
        return service.searchCompanies(query, page)
    }

    fun searchCollections(query: String, page: Int = 1): Response<SearchResult<Collection>> {
        return service.searchCollections(query, page)
    }

    fun searchKeywords(query: String, page: Int = 1): Response<SearchResult<Keyword>> {
        return service.searchKeywords(query, page)
    }

    fun searchMovies(query: String, page: Int): Response<SearchResult<SearchResultMovie>> {
        return service.searchMovies(query, page)
    }

    fun searchTv(query: String, page: Int): Response<SearchResult<SearchResultTv>> {
        return service.searchTv(query, page)
    }

    fun searchPeople(query: String, page: Int): Response<SearchResult<SearchResultPerson>> {
        return service.searchPeople(query, page)
    }

    fun searchMulti(query: String, page: Int): Response<SearchResult<SortableSearchResult>> {
        return service.searchMulti(query, page)
    }

}

typealias SearchResultProvider<T> = suspend (Int) -> Response<SearchResult<T>>
