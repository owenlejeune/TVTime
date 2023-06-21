package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.owenlejeune.tvtime.api.tmdb.api.createPagingFlow
import com.owenlejeune.tvtime.api.tmdb.api.v3.SearchResultProvider
import com.owenlejeune.tvtime.api.tmdb.api.v3.SearchService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Searchable
import com.owenlejeune.tvtime.utils.types.MediaViewType
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
        movieResults.value = createPagingSource { service.searchMovies(query, it) }
    }

    fun searchForTv(query: String) {
        tvResults.value = createPagingSource { service.searchTv(query, it) }
    }

    fun searchForPeople(query: String) {
        peopleResults.value = createPagingSource { service.searchPeople(query, it) }
    }

    fun searchMulti(query: String) {
        multiResults.value = createPagingSource { service.searchMulti(query, it) }
    }

    private fun <T: Searchable> createPagingSource(provideResults: SearchResultProvider<T>): Flow<PagingData<T>> {
        return createPagingFlow(
            fetcher = { provideResults(it) },
            processor = { it.results }
        )
    }

}