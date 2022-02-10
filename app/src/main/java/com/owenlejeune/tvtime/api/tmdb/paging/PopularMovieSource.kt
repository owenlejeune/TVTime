//package com.owenlejeune.tvtime.api.tmdb.paging
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.owenlejeune.tvtime.api.tmdb.TmdbClient
//import com.owenlejeune.tvtime.api.tmdb.model.PopularMovie
//import retrofit2.HttpException
//import java.io.IOException
//
//class PopularMovieSource: PagingSource<Int, PopularMovie>() {
//
//    companion object {
//        const val MIN_PAGE = 1
//        const val MAX_PAGE = 1000
//    }
//
//    private val movieService by lazy { TmdbClient().createMovieService() }
//
//    override fun getRefreshKey(state: PagingState<Int, PopularMovie>): Int? {
//        return state.anchorPosition
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PopularMovie> {
//        return try {
//            val nextPage = params.key ?: 1
//            val movieList = movieService.getPopularMovies(page = nextPage)
//            LoadResult.Page(
//                data = movieList.movies,
//                prevKey = if (nextPage == MIN_PAGE) null else nextPage - 1,
//                nextKey = if (movieList.count == 0 || nextPage > MAX_PAGE) null else movieList.page + 1
//            )
//        } catch (exception: IOException) {
//            return LoadResult.Error(exception)
//        } catch (exception: HttpException) {
//            return LoadResult.Error(exception)
//        }
//    }
//}