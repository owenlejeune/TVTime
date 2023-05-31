package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RecommendedMediaPagingSource
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RecommendedMedia
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

sealed class RecommendedMediaViewModel(mediaType: MediaViewType): ViewModel(), KoinComponent {

    val mediaItems: Flow<PagingData<TmdbItem>> = Pager(PagingConfig(pageSize = Int.MAX_VALUE)) {
        RecommendedMediaPagingSource(mediaType)
    }.flow.cachedIn(viewModelScope)

    object RecommendedMoviesVM: RecommendedMediaViewModel(MediaViewType.MOVIE)
    object RecommendedTvVM: RecommendedMediaViewModel(MediaViewType.TV)

}