//package com.owenlejeune.tvtime.api.tmdb.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import androidx.paging.cachedIn
//import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PopularMovie
//import com.owenlejeune.tvtime.api.tmdb.paging.PopularMovieSource
//import kotlinx.coroutines.flow.Flow
//
//class PopularMovieViewModel: ViewModel() {
//    val moviePage: Flow<PagingData<PopularMovie>> = Pager(PagingConfig(pageSize = PopularMovieSource.MAX_PAGE)) {
//        PopularMovieSource()
//    }.flow.cachedIn(viewModelScope)
//}