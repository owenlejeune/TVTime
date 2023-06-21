package com.owenlejeune.tvtime.api.tmdb.api

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.viewmodel.ViewModelConstants
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

fun <T: Any, S> ViewModel.createPagingFlow(
    fetcher: suspend (Int) -> Response<S>,
    processor: (S) -> List<T>
): Flow<PagingData<T>> {
    return Pager(PagingConfig(pageSize = ViewModelConstants.PAGING_SIZE)) {
        BasePagingSource(
            fetcher = fetcher,
            processor = processor
        )
    }.flow.cachedIn(viewModelScope)
}

class BasePagingSource<T: Any, S>(
    private val fetcher: suspend (Int) -> Response<S>,
    private val processor: (S) -> List<T>
): PagingSource<Int, T>(), KoinComponent {

    private val context: Context by inject()

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 1
            val response = fetcher(page)
            if (response.isSuccessful) {
                val responseBody = response.body()
                val results = responseBody?.let(processor) ?: emptyList()
                LoadResult.Page(
                    data = results,
                    prevKey = if (page == 1) { null } else { page - 1},
                    nextKey = if (results.isEmpty()) { null } else { page + 1}
                )
            } else {
                Toast.makeText(context, context.getString(R.string.no_result_found), Toast.LENGTH_SHORT).show()
                LoadResult.Invalid()
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}