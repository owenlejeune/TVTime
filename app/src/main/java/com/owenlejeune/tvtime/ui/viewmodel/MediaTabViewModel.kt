package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.owenlejeune.tvtime.api.tmdb.api.v3.HomePageService
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePagingSource
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.ui.navigation.MediaFetchFun
import kotlinx.coroutines.flow.Flow

sealed class MediaTabViewModel(service: HomePageService, mediaFetchFun: MediaFetchFun): ViewModel() {
    val mediaItems: Flow<PagingData<TmdbItem>> = Pager(PagingConfig(pageSize = Int.MAX_VALUE)) {
        HomePagePagingSource(service = service, mediaFetch = mediaFetchFun)
    }.flow.cachedIn(viewModelScope)

    object PopularMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getPopular(p) })
    object TopRatedMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getTopRated(p) })
    object NowPlayingMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getNowPlaying(p) })
    object UpcomingMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getUpcoming(p) })
    object PopularTvVM: MediaTabViewModel(TvService(), { s, p -> s.getPopular(p) })
    object TopRatedTvVM: MediaTabViewModel(TvService(), { s, p -> s.getTopRated(p) })
    object AiringTodayTvVM: MediaTabViewModel(TvService(), { s, p -> s.getNowPlaying(p) })
    object OnTheAirTvVM: MediaTabViewModel(TvService(), { s, p -> s.getUpcoming(p) })

}