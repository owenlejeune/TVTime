package com.owenlejeune.tvtime.api.tmdb.api.v3

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.owenlejeune.tvtime.api.storedIn
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AccountStates
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CastMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CrewMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ExternalIds
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Keyword
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MovieReleaseResults
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatingBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Review
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResult
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Searchable
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SortableSearchResult
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.StatusResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Video
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchProviders
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import java.util.Collections
import java.util.Locale

class MoviesService: KoinComponent, DetailService, HomePageService {

    companion object {
        private const val TAG = "MovieService"
    }

    private val movieService: MoviesApi by inject()

    val detailMovies = Collections.synchronizedMap(mutableStateMapOf<Int, DetailedMovie>())
    val images = Collections.synchronizedMap(mutableStateMapOf<Int, ImageCollection>())
    val cast = Collections.synchronizedMap(mutableStateMapOf<Int, List<CastMember>>())
    val crew = Collections.synchronizedMap(mutableStateMapOf<Int, List<CrewMember>>())
    val videos = Collections.synchronizedMap(mutableStateMapOf<Int, List<Video>>())
    val reviews = Collections.synchronizedMap(mutableStateMapOf<Int, List<Review>>())
    val keywords = Collections.synchronizedMap(mutableStateMapOf<Int, List<Keyword>>())
    val watchProviders = Collections.synchronizedMap(mutableStateMapOf<Int, WatchProviders>())
    val externalIds = Collections.synchronizedMap(mutableStateMapOf<Int, ExternalIds>())
    val releaseDates = Collections.synchronizedMap(mutableStateMapOf<Int, List<MovieReleaseResults.ReleaseDateResult>>())
    val similar = Collections.synchronizedMap(mutableStateMapOf<Int, Flow<PagingData<TmdbItem>>>())
    val accountStates = Collections.synchronizedMap(mutableStateMapOf<Int, AccountStates>())


    override suspend fun getById(id: Int) {
        movieService.getMovieById(id) storedIn { detailMovies[id] = it }
    }

    override suspend fun getImages(id: Int) {
        movieService.getMovieImages(id) storedIn { images[id] = it }
    }

    override suspend fun getCastAndCrew(id: Int) {
        movieService.getCastAndCrew(id) storedIn {
            cast[id] = it.cast
            crew[id] = it.crew
        }
    }
    override suspend fun getVideos(id: Int) {
        movieService.getVideos(id) storedIn { videos[id] = it.results }
    }

    override suspend fun getReviews(id: Int) {
        movieService.getReviews(id) storedIn { reviews[id] = it.results }
    }

    override suspend fun getKeywords(id: Int) {
        movieService.getKeywords(id) storedIn { keywords[id] = it.keywords ?: emptyList() }
    }

    override suspend fun getWatchProviders(id: Int) {
        movieService.getWatchProviders(id) storedIn {
            it.results[Locale.getDefault().country]?.let { wp ->
                watchProviders[id] = wp
            }
        }
    }

    override suspend fun getExternalIds(id: Int) {
        movieService.getExternalIds(id) storedIn { externalIds[id] = it }
    }

    override suspend fun getAccountStates(id: Int) {
        val sessionId = SessionManager.currentSession.value?.sessionId ?: throw Exception("Session must not be null")
        val response = movieService.getAccountStates(id, sessionId)
        if (response.isSuccessful) {
            response.body()?.let {
                Log.d(TAG, "Successfully got account states: $it")
                accountStates[id] = it
            } ?: run {
                Log.d(TAG, "Problem getting account states")
            }
        } else {
            Log.d(TAG, "Issue getting account states: $response")
        }
//        movieService.getAccountStates(id) storedIn { accountStates[id] = it }
    }

    suspend fun getReleaseDates(id: Int) {
        movieService.getReleaseDates(id) storedIn { releaseDates[id] = it.releaseDates }
    }

    override suspend fun postRating(id: Int, ratingBody: RatingBody) {
        val session = SessionManager.currentSession.value ?: throw Exception("Session must not be null")
        val response = movieService.postMovieRatingAsUser(id, session.sessionId, ratingBody)
        if (response.isSuccessful) {
            Log.d(TAG, "Successfully rated")
            SessionManager.currentSession.value?.refresh(SessionManager.Session.Changed.Rated)
            getAccountStates(id)
        } else {
            Log.w(TAG, "Issue posting rating")
        }
    }

    override suspend fun deleteRating(id: Int) {
        val session = SessionManager.currentSession.value ?: throw Exception("Session must not be null")
        val response = movieService.deleteMovieReviewAsUser(id, session.sessionId)
        if (response.isSuccessful) {
            Log.d(TAG, "Successfully deleted rated")
            SessionManager.currentSession.value?.refresh(SessionManager.Session.Changed.Rated)
            getAccountStates(id)
        } else {
            Log.w(TAG, "Issue deleting rating")
        }
    }


    override suspend fun getSimilar(id: Int, page: Int): Response<out HomePageResponse> {
        return movieService.getSimilarMovies(id, page)
    }

    override suspend fun getPopular(page: Int): Response<out HomePageResponse> {
        return movieService.getPopularMovies(page)
    }

    override suspend fun getNowPlaying(page: Int): Response<out HomePageResponse> {
        return movieService.getNowPlayingMovies(page)
    }

    override suspend fun getTopRated(page: Int): Response<out HomePageResponse> {
        return movieService.getTopRatedMovies(page)
    }

    override suspend fun getUpcoming(page: Int): Response<out HomePageResponse> {
        return movieService.getUpcomingMovies(page)
    }
}

class SimilarMoviesSource(private val movieId: Int): PagingSource<Int, TmdbItem>(), KoinComponent {

    private val service: MoviesService by inject()

    override fun getRefreshKey(state: PagingState<Int, TmdbItem>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TmdbItem> {
        return try {
            val nextPage = params.key ?: 1
            val response = service.getSimilar(movieId, nextPage)
            if (response.isSuccessful) {
                val responseBody = response.body()
                val result = responseBody?.results ?: emptyList()
                LoadResult.Page(
                    data = result,
                    prevKey = if (nextPage == 1) {
                        null
                    } else {
                        nextPage - 1
                    },
                    nextKey = if (result.isEmpty()) {
                        null
                    } else {
                        responseBody?.page?.plus(1) ?: (nextPage + 1)
                    }
                )
            } else {
                LoadResult.Invalid()
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}