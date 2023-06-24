package com.owenlejeune.tvtime.api.tmdb.api.v3

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.owenlejeune.tvtime.api.storedIn
import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AccountStates
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CastAndCrew
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CastMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CrewMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedTv
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ExternalIds
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Keyword
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.KeywordsResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatingBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Review
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ReviewResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResult
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultMedia
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Season
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.StatusResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvContentRatings
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Video
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.VideoResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchProviderResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchProviders
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import java.util.Collections
import java.util.Locale

class TvService: KoinComponent, DetailService, HomePageService {

    companion object {
        private const val TAG = "TvService"
    }

    private val service: TvApi by inject()

    val detailTv = Collections.synchronizedMap(mutableStateMapOf<Int, DetailedTv>())
    val images = Collections.synchronizedMap(mutableStateMapOf<Int, ImageCollection>())
    val cast = Collections.synchronizedMap(mutableStateMapOf<Int, List<CastMember>>())
    val crew = Collections.synchronizedMap(mutableStateMapOf<Int, List<CrewMember>>())
    val videos = Collections.synchronizedMap(mutableStateMapOf<Int, List<Video>>())
    val reviews = Collections.synchronizedMap(mutableStateMapOf<Int, List<Review>>())
    val keywords = Collections.synchronizedMap(mutableStateMapOf<Int, List<Keyword>>())
    val watchProviders = Collections.synchronizedMap(mutableStateMapOf<Int, WatchProviders>())
    val externalIds = Collections.synchronizedMap(mutableStateMapOf<Int, ExternalIds>())
    val contentRatings = Collections.synchronizedMap(mutableStateMapOf<Int, List<TvContentRatings.TvContentRating>>())
    val similar = Collections.synchronizedMap(mutableStateMapOf<Int, Flow<PagingData<TmdbItem>>>())
    val accountStates = Collections.synchronizedMap(mutableStateMapOf<Int, AccountStates>())
    val keywordResults = Collections.synchronizedMap(mutableStateMapOf<Int, Flow<PagingData<SearchResultMedia>>>())

    private val _seasons = Collections.synchronizedMap(mutableStateMapOf<Int, MutableSet<Season>>())
    val seasons: MutableMap<Int, out Set<Season>>
        get() = _seasons

    override suspend fun getById(id: Int) {
        service.getTvShowById(id) storedIn { detailTv[id] = it }
    }

    override suspend fun getImages(id: Int) {
        service.getTvImages(id) storedIn { images[id] = it }
    }

    override suspend fun getCastAndCrew(id: Int) {
        service.getCastAndCrew(id) storedIn {
            cast[id] = it.cast
            crew[id] = it.crew
        }
    }

    suspend fun getContentRatings(id: Int) {
        service.getContentRatings(id) storedIn { contentRatings[id] = it.results }
    }

    override suspend fun getVideos(id: Int) {
        service.getVideos(id) storedIn { videos[id] = it.results }
    }

    override suspend fun getReviews(id: Int) {
        service.getReviews(id) storedIn { reviews[id] = it.results }
    }

    override suspend fun getKeywords(id: Int) {
        service.getKeywords(id) storedIn { keywords[id] = it.keywords ?: emptyList() }
    }

    override suspend fun getWatchProviders(id: Int) {
        service.getWatchProviders(id) storedIn {
            it.results[Locale.getDefault().country]?.let { wp ->
                watchProviders[id] = wp
            }
        }
    }

    override suspend fun getAccountStates(id: Int) {
        service.getAccountStates(id) storedIn { accountStates[id] = it }
    }

    suspend fun getSeason(seriesId: Int, seasonId: Int) {
        service.getSeason(seriesId, seasonId) storedIn {
            _seasons[seriesId]?.add(it) ?: run {
                _seasons[seriesId] = mutableSetOf(it)
            }
        }
    }

    override suspend fun getExternalIds(id: Int) {
        service.getExternalIds(id) storedIn { externalIds[id] = it }
    }

    override suspend fun postRating(id: Int, ratingBody: RatingBody) {
        val session = SessionManager.currentSession.value ?: throw Exception("Session must not be null")
        val response = service.postTvRatingAsUser(id, session.sessionId, ratingBody)
        if (response.isSuccessful) {
            Log.d(TAG, "Successfully posted rating")
        } else {
            Log.w(TAG, "Issue posting rating")
        }
    }

    override suspend fun deleteRating(id: Int) {
        val session = SessionManager.currentSession.value ?: throw Exception("Session must not be null")
        val response = service.deleteTvReviewAsUser(id, session.sessionId)
        if (response.isSuccessful) {
            Log.d(TAG, "Successfully deleted rating")
        } else {
            Log.w(TAG, "Issue deleting rating")
        }
    }

    override suspend fun discover(keywords: String?, page: Int): Response<out SearchResult<out SearchResultMedia>> {
        return service.discover(page, keywords)
    }

    override suspend fun getSimilar(id: Int, page: Int): Response<out HomePageResponse> {
        return service.getSimilarTvShows(id, page)
    }

    override suspend fun getPopular(page: Int): Response<out HomePageResponse> {
        return service.getPoplarTv(page)
    }

    override suspend fun getNowPlaying(page: Int): Response<out HomePageResponse> {
        return service.getTvAiringToday(page)
    }

    override suspend fun getTopRated(page: Int): Response<out HomePageResponse> {
        return service.getTopRatedTv(page)
    }

    override suspend fun getUpcoming(page: Int): Response<out HomePageResponse> {
        return service.getTvOnTheAir(page)
    }

}
