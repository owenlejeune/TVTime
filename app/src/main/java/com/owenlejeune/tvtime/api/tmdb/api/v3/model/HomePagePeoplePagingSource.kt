package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.owenlejeune.tvtime.api.tmdb.api.v3.PeopleApi
import com.owenlejeune.tvtime.api.tmdb.api.v3.PeopleService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomePagePeoplePagingSource: PagingSource<Int, HomePagePerson>(), KoinComponent {

    private val service: PeopleApi by inject()
    private val context: Context by inject()

    override fun getRefreshKey(state: PagingState<Int, HomePagePerson>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomePagePerson> {
        return try {
            val nextPage = params.key ?: 1
            val peopleResponse = service.getPopular(page = nextPage)
            if (peopleResponse.isSuccessful) {
                val responseBody = peopleResponse.body()
                val results = responseBody?.results ?: emptyList()
                LoadResult.Page(
                    data = results,
                    prevKey = if (nextPage == 1) null else nextPage - 1,
                    nextKey = if (results.isEmpty() || responseBody == null) null else responseBody.page + 1
                )
            } else {
                Toast.makeText(context, "No more results found", Toast.LENGTH_SHORT).show()
                LoadResult.Invalid()
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}