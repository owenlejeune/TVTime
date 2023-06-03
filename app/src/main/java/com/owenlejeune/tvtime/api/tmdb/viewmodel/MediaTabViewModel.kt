package com.owenlejeune.tvtime.api.tmdb.viewmodel

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

sealed class MediaTabViewModel(service: HomePageService, mediaFetchFun: MediaFetchFun, tag: String): ViewModel() {

    val mediaItems: Flow<PagingData<TmdbItem>> = Pager(PagingConfig(pageSize = ViewModelConstants.PAGING_SIZE)) {
        HomePagePagingSource(service = service, mediaFetch = mediaFetchFun, tag = tag)
    }.flow.cachedIn(viewModelScope)

    object PopularMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getPopular(p) }, PopularMoviesVM::class.java.simpleName)
    object TopRatedMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getTopRated(p) }, TopRatedMoviesVM::class.java.simpleName)
    object NowPlayingMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getNowPlaying(p) }, NowPlayingMoviesVM::class.java.simpleName)
    object UpcomingMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getUpcoming(p) }, UpcomingMoviesVM::class.java.simpleName)
    object PopularTvVM: MediaTabViewModel(TvService(), { s, p -> s.getPopular(p) }, PopularTvVM::class.java.simpleName)
    object TopRatedTvVM: MediaTabViewModel(TvService(), { s, p -> s.getTopRated(p) }, TopRatedTvVM::class.java.simpleName)
    object AiringTodayTvVM: MediaTabViewModel(TvService(), { s, p -> s.getNowPlaying(p) }, AiringTodayTvVM::class.java.simpleName)
    object OnTheAirTvVM: MediaTabViewModel(TvService(), { s, p -> s.getUpcoming(p) }, OnTheAirTvVM::class.java.simpleName)

}