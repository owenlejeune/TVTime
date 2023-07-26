package com.owenlejeune.tvtime.api.tmdb.api.v3

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import com.owenlejeune.tvtime.api.LoadingState
import com.owenlejeune.tvtime.api.loadRemoteData
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AccountStates
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CastMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CrewMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ExternalIds
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Keyword
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MovieReleaseResults
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Review
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResult
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultMedia
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Video
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchProviders
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.types.TimeWindow
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
    val keywordResults = Collections.synchronizedMap(mutableStateMapOf<Int, Flow<PagingData<SearchResultMedia>>>())

    val detailsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val imagesLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val castCrewLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val videosLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val reviewsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val keywordsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val watchProvidersLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val externalIdsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val releaseDatesLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val accountStatesLoadingState = mutableStateOf(LoadingState.INACTIVE)

    override suspend fun getById(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { movieService.getMovieById(id) },
            { detailMovies[id] = it },
            detailsLoadingState,
            refreshing
        )
    }

    override suspend fun getImages(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { movieService.getMovieImages(id) },
            { images[id] = it },
            imagesLoadingState,
            refreshing
        )
    }

    override suspend fun getCastAndCrew(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { movieService.getCastAndCrew(id) },
            {
                cast[id] = it.cast
                crew[id] = it.crew
            },
            castCrewLoadingState,
            refreshing
        )
    }
    override suspend fun getVideos(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { movieService.getVideos(id) },
            { videos[id] = it.results },
            videosLoadingState,
            refreshing
        )
    }

    override suspend fun getReviews(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { movieService.getReviews(id) },
            { reviews[id] = it.results },
            reviewsLoadingState,
            refreshing
        )
    }

    override suspend fun getKeywords(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { movieService.getKeywords(id) },
            { keywords[id] = it.keywords ?: emptyList() },
            keywordsLoadingState,
            refreshing
        )
    }

    override suspend fun getWatchProviders(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { movieService.getWatchProviders(id) },
            {
                it.results[Locale.getDefault().country]?.let { wp ->
                    watchProviders[id] = wp
                }
            },
            watchProvidersLoadingState,
            refreshing
        )
    }

    override suspend fun getExternalIds(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { movieService.getExternalIds(id) },
            { externalIds[id] = it },
            externalIdsLoadingState,
            refreshing
        )
    }

    override suspend fun getAccountStates(id: Int) {
        val sessionId = SessionManager.currentSession.value?.sessionId ?: throw Exception("Session must not be null")
        loadRemoteData(
            { movieService.getAccountStates(id, sessionId) },
            { accountStates[id] = it },
            accountStatesLoadingState,
            false
        )
    }

    suspend fun getReleaseDates(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { movieService.getReleaseDates(id) },
            { releaseDates[id] = it.releaseDates },
            releaseDatesLoadingState,
            refreshing
        )
    }

    override suspend fun postRating(id: Int, rating: Float) {
        val session = SessionManager.currentSession.value ?: throw Exception("Session must not be null")
        val response = movieService.postMovieRatingAsUser(id, session.sessionId, rating)
        if (response.isSuccessful) {
            Log.d(TAG, "Successfully rated")
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
            getAccountStates(id)
        } else {
            Log.w(TAG, "Issue deleting rating")
        }
    }

    suspend fun getTrending(timeWindow: TimeWindow, page: Int): Response<out SearchResult<out SearchResultMedia>> {
        return movieService.trending(timeWindow.name.lowercase(), page)
    }

    override suspend fun discover(keywords: String?, page: Int): Response<out SearchResult<out SearchResultMedia>> {
        return movieService.discover(keywords, page)
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
