package com.owenlejeune.tvtime.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.owenlejeune.tvtime.api.tmdb.api.v3.SearchPagingSource
import com.owenlejeune.tvtime.api.tmdb.api.v3.SearchResultProvider
import com.owenlejeune.tvtime.api.tmdb.api.v3.SearchService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultPerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultTv
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Searchable
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SortableSearchResult
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchViewModel: ViewModel(), KoinComponent {

    private val service: SearchService by inject()

    val movieResults = service.movieResults
    val tvResults = service.tvResults
    val peopleResults = service.peopleResults
    val multiResults = service.multiResults

    fun resetResults() {
        movieResults.value = null
        tvResults.value = null
        peopleResults.value = null
        multiResults.value = null
    }

    fun searchFor(query: String, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> searchForMovies(query)
            MediaViewType.TV -> searchForTv(query)
            MediaViewType.PERSON -> searchForPeople(query)
            MediaViewType.MIXED -> searchMulti(query)
            else -> {}
        }
    }

    fun searchForMovies(query: String) {
        movieResults.value = createPagingSource(viewModelScope) { service.searchMovies(query, it) }
    }

    fun searchForTv(query: String) {
        tvResults.value = createPagingSource(viewModelScope) { service.searchTv(query, it) }
    }

    fun searchForPeople(query: String) {
        peopleResults.value = createPagingSource(viewModelScope) { service.searchPeople(query, it) }
    }

    fun searchMulti(query: String) {
        multiResults.value = createPagingSource(viewModelScope) { service.searchMulti(query, it) }
    }

    private fun <T: Searchable> createPagingSource(viewModelScope: CoroutineScope, provideResults: SearchResultProvider<T>): Flow<PagingData<T>> {
        return Pager(PagingConfig(pageSize = 1)) { SearchPagingSource(provideResults) }.flow.cachedIn(viewModelScope)
    }

}