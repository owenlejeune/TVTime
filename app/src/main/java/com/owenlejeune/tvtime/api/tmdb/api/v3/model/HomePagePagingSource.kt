package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.owenlejeune.tvtime.api.tmdb.api.v3.HomePageService
import com.owenlejeune.tvtime.ui.navigation.MediaFetchFun
import retrofit2.Response

class HomePagePagingSource(private val service: HomePageService, private val mediaFetch: MediaFetchFun): PagingSource<Int, TmdbItem>() {

    override fun getRefreshKey(state: PagingState<Int, TmdbItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TmdbItem> {
        return try {
            val nextPage = params.key ?: 1
            val mediaResponse = mediaFetch.invoke(service, nextPage)
            if (mediaResponse.isSuccessful) {
                val responseBody = mediaResponse.body()
                val results = responseBody?.results ?: emptyList()
                LoadResult.Page(
                    data = results,
                    prevKey = if (nextPage == 1) null else nextPage - 1,
                    nextKey = if (results.isEmpty() || responseBody == null) null else responseBody.page + 1
                )
            } else {
                LoadResult.Invalid()
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}