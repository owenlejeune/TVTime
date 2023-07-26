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
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedTv
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ExternalIds
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Keyword
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Review
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResult
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultMedia
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Season
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SeasonAccountStates
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvCastMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvContentRatings
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvCrewMember
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

    private val _seasonAccountStates = Collections.synchronizedMap(mutableStateMapOf<Int, MutableMap<Int, SeasonAccountStates>>())
    val seasonAccountStates: MutableMap<Int, out Map<Int, SeasonAccountStates>>
        get() = _seasonAccountStates

    private val _seasonCast = Collections.synchronizedMap(mutableStateMapOf<Int, MutableMap<Int, List<TvCastMember>>>())
    val seasonCast: MutableMap<Int, out Map<Int, List<TvCastMember>>>
        get() = _seasonCast

    private val _seasonCrew = Collections.synchronizedMap(mutableStateMapOf<Int, MutableMap<Int, List<TvCrewMember>>>())
    val seasonCrew: MutableMap<Int, out Map<Int, List<TvCrewMember>>>
        get() = _seasonCrew

    private val _seasonImages = Collections.synchronizedMap(mutableStateMapOf<Int, MutableMap<Int, ImageCollection>>())
    val seasonImages: Map<Int, out Map<Int, ImageCollection>>
        get() = _seasonImages

    private val _seasonVideos = Collections.synchronizedMap(mutableStateMapOf<Int, MutableMap<Int, List<Video>>>())
    val seasonVideos: MutableMap<Int, out Map<Int, List<Video>>>
        get() = _seasonVideos

    private val _seasonWatchProviders = Collections.synchronizedMap(mutableStateMapOf<Int, MutableMap<Int, WatchProviders>>())
    val seasonWatchProviders: MutableMap<Int, out Map<Int, WatchProviders>>
        get() = _seasonWatchProviders

    val detailsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val imagesLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val castCrewLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val contentRatingsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val videosLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val reviewsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val keywordsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val watchProvidersLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val externalIdsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val accountStatesLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val seasonsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val seasonAccountStatesLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val seasonCreditsLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val seasonImagesLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val seasonVideosLoadingState = mutableStateOf(LoadingState.INACTIVE)
    val seasonWatchProvidersLoadingState = mutableStateOf(LoadingState.INACTIVE)

    override suspend fun getById(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getTvShowById(id) },
            { detailTv[id] = it },
            detailsLoadingState,
            refreshing
        )
    }

    override suspend fun getImages(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getTvImages(id) },
            { images[id] = it },
            imagesLoadingState,
            refreshing
        )
    }

    override suspend fun getCastAndCrew(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getCastAndCrew(id) },
            {
                cast[id] = it.cast
                crew[id] = it.crew
            },
            castCrewLoadingState,
            refreshing
        )
    }

    suspend fun getContentRatings(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getContentRatings(id) },
            { contentRatings[id] = it.results },
            contentRatingsLoadingState,
            refreshing
        )
    }

    override suspend fun getVideos(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getVideos(id) },
            { videos[id] = it.results },
            videosLoadingState,
            refreshing
        )
    }

    override suspend fun getReviews(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getReviews(id) },
            { reviews[id] = it.results },
            reviewsLoadingState,
            refreshing
        )
    }

    override suspend fun getKeywords(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getKeywords(id) },
            { keywords[id] = it.keywords ?: emptyList() },
            keywordsLoadingState,
            refreshing
        )
    }

    override suspend fun getWatchProviders(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getWatchProviders(id) },
            {
                it.results[Locale.getDefault().country]?.let { wp ->
                    watchProviders[id] = wp
                }
            },
            watchProvidersLoadingState,
            refreshing
        )
    }

    override suspend fun getAccountStates(id: Int) {
        loadRemoteData(
            { service.getAccountStates(id) },
            { accountStates[id] = it },
            accountStatesLoadingState,
            false
        )
    }

    override suspend fun getExternalIds(id: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getExternalIds(id) },
            { externalIds[id] = it },
            externalIdsLoadingState,
            refreshing
        )
    }

    suspend fun getSeason(seriesId: Int, seasonId: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getSeason(seriesId, seasonId) },
            { season ->
                _seasons[seriesId]?.let { seasonSet ->
                    if (seasonSet.find { it.seasonNumber == seasonId } == null) {
                        seasonSet.add(season)
                    }
                } ?: run {
                    _seasons[seriesId] = mutableSetOf(season)
                }
            },
            seasonsLoadingState,
            refreshing
        )
    }

    suspend fun getSeasonAccountStates(seriesId: Int, seasonId: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getSeasonAccountStates(seriesId, seasonId) },
            { sas ->
                _seasonAccountStates
                    .getOrPut(seriesId) {
                        emptyMap<Int, SeasonAccountStates>().toMutableMap()
                    }[seasonId] = sas
            },
            seasonAccountStatesLoadingState,
            refreshing
        )
    }

    suspend fun getSeasonCredits(seriesId: Int, seasonId: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getSeasonCredits(seriesId, seasonId) },
            { sc ->
                _seasonCast
                    .getOrPut(seriesId) {
                        emptyMap<Int, List<TvCastMember>>().toMutableMap()
                    }[seasonId] = sc.cast
                _seasonCrew
                    .getOrPut(seriesId) {
                        emptyMap<Int, List<TvCrewMember>>().toMutableMap()
                    }[seasonId] = sc.crew
            },
            seasonCreditsLoadingState,
            refreshing
        )
    }

    suspend fun getSeasonImages(seriesId: Int, seasonId: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getSeasonImages(seriesId, seasonId) },
            { si ->
                _seasonImages
                    .getOrPut(seriesId) {
                        emptyMap<Int, ImageCollection>().toMutableMap()
                    }[seasonId] = si
            },
            seasonImagesLoadingState,
            refreshing
        )
    }

    suspend fun getSeasonVideos(seriesId: Int, seasonId: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getSeasonVideos(seriesId, seasonId) },
            { sv ->
                _seasonVideos
                    .getOrPut(seriesId) {
                        emptyMap<Int, List<Video>>().toMutableMap()
                    }[seasonId] = sv.results
            },
            seasonVideosLoadingState,
            refreshing
        )
    }

    suspend fun getSeasonWatchProviders(seriesId: Int, seasonId: Int, refreshing: Boolean) {
        loadRemoteData(
            { service.getSeasonWatchProviders(seriesId, seasonId) },
            { si ->
                si.results[Locale.getDefault().country]?.let { wp ->
                    _seasonWatchProviders
                        .getOrPut(seriesId) {
                            emptyMap<Int, WatchProviders>().toMutableMap()
                        }[seasonId] = wp
                }
            },
            seasonWatchProvidersLoadingState,
            refreshing
        )
    }

    override suspend fun postRating(id: Int, rating: Float) {
        val session = SessionManager.currentSession.value ?: throw Exception("Session must not be null")
        val response = service.postTvRatingAsUser(id, session.sessionId, rating)
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

    suspend fun getTrending(timeWindow: TimeWindow, page: Int): Response<out SearchResult<out SearchResultMedia>> {
        return service.trending(timeWindow.name.lowercase(), page)
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
