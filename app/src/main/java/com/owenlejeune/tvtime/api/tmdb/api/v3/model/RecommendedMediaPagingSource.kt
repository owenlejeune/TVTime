package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.owenlejeune.tvtime.api.tmdb.api.v4.AccountV4Service
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.utils.types.MediaViewType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecommendedMediaPagingSource(
    private val mediaType: MediaViewType
): PagingSource<Int, TmdbItem>(), KoinComponent {

    private val preferences: AppPreferences by inject()
    private val service: AccountV4Service by inject()

    override fun getRefreshKey(state: PagingState<Int, TmdbItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TmdbItem> {
        return try {
            val nextPage = params.key ?: 1
            val mediaResponse = if (mediaType == MediaViewType.MOVIE) {
                service.getRecommendedMovies(preferences.authorizedSessionValues?.accountId ?: "", nextPage)
            } else {
                service.getRecommendedTvSeries(preferences.authorizedSessionValues?.accountId ?: "", nextPage)
            }
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