package com.owenlejeune.tvtime.ui.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.owenlejeune.tvtime.api.LoadingState
import com.owenlejeune.tvtime.api.tmdb.api.createPagingFlow
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.PeopleService
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AccountStates
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CastMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CrewMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ExternalIds
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Keyword
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatingBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Review
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SearchResultMedia
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Video
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchProviders
import com.owenlejeune.tvtime.extensions.anyOf
import com.owenlejeune.tvtime.ui.screens.tabs.MediaTabNavItem
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.utils.types.TimeWindow
import com.owenlejeune.tvtime.utils.types.ViewableMediaTypeException
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel: ViewModel(), KoinComponent {

    private val movieService: MoviesService by inject()
    private val tvService: TvService by inject()
    private val peopleService: PeopleService by inject()

    val detailMovies = movieService.detailMovies
    val movieImages = movieService.images
    val movieCast = movieService.cast
    val movieCrew = movieService.crew
    val movieVideos = movieService.videos
    val movieReviews = movieService.reviews
    val movieKeywords = movieService.keywords
    val movieWatchProviders = movieService.watchProviders
    val movieExternalIds = movieService.externalIds
    val movieReleaseDates = movieService.releaseDates
    val similarMovies = movieService.similar
    val movieAccountStates = movieService.accountStates
    val movieKeywordResults = movieService.keywordResults

    val movieDetailsLoadingState = movieService.detailsLoadingState
    val movieImagesLoadingState = movieService.imagesLoadingState
    val movieCastCrewLoadingState = movieService.castCrewLoadingState
    val movieVideosLoadingState = movieService.videosLoadingState
    val movieReviewsLoadingState = movieService.reviewsLoadingState
    val movieKeywordsLoadingState = movieService.keywordsLoadingState
    val movieWatchProvidersLoadingState = movieService.watchProvidersLoadingState
    val movieExternalIdsLoadingState = movieService.externalIdsLoadingState
    val movieReleaseDatesLoadingState = movieService.releaseDatesLoadingState
    val movieAccountStatesLoadingState = movieService.accountStatesLoadingState

    val popularMovies by lazy {
        createPagingFlow(
            fetcher = { p -> movieService.getPopular(p) },
            processor = { it.results }
        )
    }
    val topRatedMovies by lazy {
        createPagingFlow(
            fetcher = { p -> movieService.getTopRated(p) },
            processor = { it.results }
        )
    }
    val nowPlayingMovies by lazy {
        createPagingFlow(
            fetcher = { p -> movieService.getNowPlaying(p) },
            processor = { it.results }
        )
    }
    val upcomingMovies by lazy {
        createPagingFlow(
            fetcher = { p -> movieService.getUpcoming(p) },
            processor = { it.results }
        )
    }

    val detailedTv = tvService.detailTv
    val tvImages = tvService.images
    val tvCast = tvService.cast
    val tvCrew = tvService.crew
    val tvVideos = tvService.videos
    val tvReviews = tvService.reviews
    val tvKeywords = tvService.keywords
    val tvWatchProviders = tvService.watchProviders
    val tvExternalIds = tvService.externalIds
    val tvContentRatings = tvService.contentRatings
    val tvSeasons = tvService.seasons
    val similarTv = tvService.similar
    val tvAccountStates = tvService.accountStates
    val tvKeywordResults = tvService.keywordResults
    val tvSeasonAccountStates = tvService.seasonAccountStates
    val tvSeasonCast = tvService.seasonCast
    val tvSeasonCrew = tvService.seasonCrew
    val tvSeasonImages = tvService.seasonImages
    val tvSeasonVideos = tvService.seasonVideos
    val tvSeasonWatchProviders = tvService.seasonWatchProviders

    val tvDetailsLoadingState = tvService.detailsLoadingState
    val tvImagesLoadingState = tvService.imagesLoadingState
    val tvCastCrewLoadingState = tvService.castCrewLoadingState
    val tvVideosLoadingState = tvService.videosLoadingState
    val tvReviewsLoadingState = tvService.reviewsLoadingState
    val tvKeywordsLoadingState = tvService.keywordsLoadingState
    val tvWatchProvidersLoadingState = tvService.watchProvidersLoadingState
    val tvExternalIdsLoadingState = tvService.externalIdsLoadingState
    val tvSeasonsLoadingState = tvService.seasonsLoadingState
    val tvContentRatingsLoadingState = tvService.contentRatingsLoadingState
    val tvAccountStatesLoadingState = tvService.accountStatesLoadingState
    val tvSeasonAccountStatesLoadingState = tvService.seasonAccountStatesLoadingState
    val tvSeasonCreditsLoadingState = tvService.seasonCreditsLoadingState
    val tvSeasonImagesLoadingState = tvService.seasonImagesLoadingState
    val tvSeasonVideosLoadingState = tvService.seasonVideosLoadingState
    val tvSeasonWatchProvidersLoadingState = tvService.seasonWatchProvidersLoadingState

    val popularTv by lazy {
        createPagingFlow(
            fetcher = { p -> tvService.getPopular(p) },
            processor = { it.results }
        )
    }
    val topRatedTv by lazy{
        createPagingFlow(
            fetcher = { p -> tvService.getTopRated(p) },
            processor = { it.results }
        )
    }
    val airingTodayTv by lazy {
        createPagingFlow(
            fetcher = { p -> tvService.getNowPlaying(p) },
            processor = { it.results }
        )
    }
    val onTheAirTv by lazy {
        createPagingFlow(
            fetcher = { p -> tvService.getUpcoming(p) },
            processor = { it.results }
        )
    }

    val peopleMap = peopleService.peopleMap
    val peopleCastMap = peopleService.castMap
    val peopleCrewMap = peopleService.crewMap
    val peopleImagesMap = peopleService.imagesMap
    val peopleExternalIdsMap = peopleService.externalIdsMap

    val peopleDetailsLoadingState = peopleService.detailsLoadingState
    val peopleCastCrewLoadingState = peopleService.castCrewLoadingState
    val peopleImagesLoadingState = peopleService.imagesLoadingState
    val peopleExternalIdsLoadingState = peopleService.externalIdsLoadingState

    val popularPeople by lazy {
        createPagingFlow(
            fetcher = { p -> peopleService.getPopular(p) },
            processor = { it.results }
        )
    }

    private fun <T> providesForType(type: MediaViewType, movies: () -> T, tv: () -> T): T {
        return when (type) {
            MediaViewType.MOVIE -> movies()
            MediaViewType.TV -> tv()
            else -> throw ViewableMediaTypeException(type) // shouldn't happen
        }
    }

    fun produceDetailsFor(type: MediaViewType): Map<Int, DetailedItem> {
        return providesForType(type, { detailMovies }, { detailedTv })
    }

    fun produceImagesFor(type: MediaViewType): Map<Int, ImageCollection> {
        return providesForType(type, { movieImages }, { tvImages })
    }

    fun produceExternalIdsFor(type: MediaViewType): Map<Int, ExternalIds> {
        return when (type) {
            MediaViewType.MOVIE -> movieExternalIds
            MediaViewType.TV -> tvExternalIds
            MediaViewType.PERSON -> peopleExternalIdsMap
            else -> throw ViewableMediaTypeException(type)
        }
    }

    fun produceKeywordsFor(type: MediaViewType): Map<Int, List<Keyword>> {
        return providesForType(type, { movieKeywords }, { tvKeywords })
    }

    fun produceCastFor(type: MediaViewType): Map<Int, List<CastMember>> {
        return providesForType(type, { movieCast }, { tvCast })
    }

    fun produceCrewFor(type: MediaViewType): Map<Int, List<CrewMember>> {
        return providesForType(type, { movieCrew }, { tvCrew })
    }

    fun produceVideosFor(type: MediaViewType): Map<Int, List<Video>> {
        return providesForType(type, { movieVideos }, { tvVideos })
    }

    fun produceWatchProvidersFor(type: MediaViewType): Map<Int, WatchProviders> {
        return providesForType(type, { movieWatchProviders }, { tvWatchProviders })
    }

    fun produceReviewsFor(type: MediaViewType): Map<Int, List<Review>> {
        return providesForType(type, { movieReviews }, { tvReviews })
    }

    fun produceSimilarContentFor(type: MediaViewType): Map<Int, Flow<PagingData<TmdbItem>>> {
        return providesForType(type, { similarMovies }, { similarTv })
    }

    fun produceAccountStatesFor(type: MediaViewType): Map<Int, AccountStates> {
        return providesForType(type, { movieAccountStates }, { tvAccountStates} )
    }

    fun produceMediaTabFlowFor(mediaType: MediaViewType, contentType: MediaTabNavItem.Type): Flow<PagingData<TmdbItem>> {
        return providesForType(
            mediaType,
            {
                when (contentType) {
                    MediaTabNavItem.Type.UPCOMING -> upcomingMovies
                    MediaTabNavItem.Type.TOP_RATED -> topRatedMovies
                    MediaTabNavItem.Type.NOW_PLAYING -> nowPlayingMovies
                    MediaTabNavItem.Type.POPULAR -> popularMovies
                    else -> throw Exception("Can't produce media flow for $mediaType")
                }
            },
            {
                when (contentType) {
                    MediaTabNavItem.Type.UPCOMING -> onTheAirTv
                    MediaTabNavItem.Type.TOP_RATED -> topRatedTv
                    MediaTabNavItem.Type.NOW_PLAYING -> airingTodayTv
                    MediaTabNavItem.Type.POPULAR -> popularTv
                    else -> throw Exception("Can't produce media flow for $mediaType")
                }
            }
        )
    }

    fun produceKeywordsResultsFor(mediaType: MediaViewType): Map<Int, Flow<PagingData<SearchResultMedia>>> {
        return providesForType(mediaType, { movieKeywordResults }, { tvKeywordResults })
    }

    fun produceTrendingFor(mediaType: MediaViewType, timeWindow: TimeWindow): Flow<PagingData<TmdbItem>> {
        return when (mediaType) {
            MediaViewType.MOVIE -> {
                createPagingFlow(
                    fetcher = { p -> movieService.getTrending(timeWindow, p) },
                    processor = { it.results }
                )
            }
            MediaViewType.TV -> {
                createPagingFlow(
                    fetcher = { p -> tvService.getTrending(timeWindow, p) },
                    processor = { it.results }
                )
            }
            MediaViewType.PERSON -> {
                createPagingFlow(
                    fetcher = { p -> peopleService.getTrending(timeWindow, p) },
                    processor = { it.results }
                )
            }
            else -> throw ViewableMediaTypeException(mediaType)
        }
    }

    fun produceDetailsLoadingStateFor(mediaType: MediaViewType): MutableState<LoadingState> {
        return providesForType(mediaType, { movieDetailsLoadingState }, { tvDetailsLoadingState})
    }

    fun produceImagesLoadingStateFor(mediaType: MediaViewType): MutableState<LoadingState> {
        return providesForType(mediaType, { movieImagesLoadingState }, { tvImagesLoadingState })
    }

    fun produceCastCrewLoadingStateFor(mediaType: MediaViewType): MutableState<LoadingState> {
        return providesForType(mediaType, { movieCastCrewLoadingState }, { tvCastCrewLoadingState })
    }

    fun produceVideosLoadingStateFor(mediaType: MediaViewType): MutableState<LoadingState> {
        return providesForType(mediaType, { movieVideosLoadingState }, { tvVideosLoadingState })
    }

    fun produceReviewsLoadingStateFor(mediaType: MediaViewType): MutableState<LoadingState> {
        return providesForType(mediaType, { movieReviewsLoadingState }, { tvReviewsLoadingState })
    }

    fun produceKeywordsLoadingStateFor(mediaType: MediaViewType): MutableState<LoadingState> {
        return providesForType(mediaType, { movieKeywordsLoadingState }, { tvKeywordsLoadingState })
    }

    fun produceWatchProvidersLoadingStateFor(mediaType: MediaViewType): MutableState<LoadingState> {
        return providesForType(mediaType, { movieWatchProvidersLoadingState }, { tvWatchProvidersLoadingState })
    }

    fun produceExternalIdsLoadingStateFor(mediaType: MediaViewType): MutableState<LoadingState> {
        return when (mediaType) {
            MediaViewType.MOVIE -> movieExternalIdsLoadingState
            MediaViewType.TV -> tvExternalIdsLoadingState
            MediaViewType.PERSON -> peopleExternalIdsLoadingState
            else -> throw ViewableMediaTypeException(mediaType)
        }
    }

    fun produceAccountStatesLoadingStateFor(mediaType: MediaViewType): MutableState<LoadingState> {
        return providesForType(mediaType, { movieAccountStatesLoadingState }, { tvAccountStatesLoadingState })
    }

    suspend fun getById(id: Int, type: MediaViewType, force: Boolean = false) {
        when (type) {
            MediaViewType.MOVIE -> if (detailMovies[id] == null || force) movieService.getById(id, force)
            MediaViewType.TV -> if (detailedTv[id] == null || force) tvService.getById(id, force)
            MediaViewType.PERSON -> if (peopleMap[id] == null || force) peopleService.getPerson(id, force)
            else -> {}
        }
    }

    suspend fun getImages(id: Int, type: MediaViewType, force: Boolean = false) {
        when (type) {
            MediaViewType.MOVIE -> if (movieImages[id] == null || force) movieService.getImages(id, force)
            MediaViewType.TV -> if (tvImages[id] == null || force) tvService.getImages(id, force)
            MediaViewType.PERSON -> if (peopleImagesMap[id] == null || force) peopleService.getImages(id, force)
            else -> {}
        }
    }

    suspend fun getCastAndCrew(id: Int, type: MediaViewType, force: Boolean = false) {
        when (type) {
            MediaViewType.MOVIE -> if (movieCast[id] == null || movieCrew[id] == null || force) movieService.getCastAndCrew(id, force)
            MediaViewType.TV -> if (tvCast[id] == null || tvCrew[id] == null || force) tvService.getCastAndCrew(id, force)
            MediaViewType.PERSON -> if (peopleCastMap[id] == null || peopleCrewMap[id] == null || force) peopleService.getCredits(id, force)
            else -> {}
        }
    }

    suspend fun getVideos(id: Int, type: MediaViewType, force: Boolean = false) {
        when (type) {
            MediaViewType.MOVIE -> if (movieVideos[id] == null || force) movieService.getVideos(id, force)
            MediaViewType.TV -> if (tvVideos[id] == null || force) tvService.getVideos(id, force)
            else -> {}
        }
    }

    suspend fun getReviews(id: Int, type: MediaViewType, force: Boolean = false) {
        when (type) {
            MediaViewType.MOVIE -> if (movieReviews[id] == null || force) movieService.getReviews(id, force)
            MediaViewType.TV -> if (tvReviews[id] == null || force) tvService.getReviews(id, force)
            else -> {}
        }
    }

    suspend fun getKeywords(id: Int, type: MediaViewType, force: Boolean = false) {
        when (type) {
            MediaViewType.MOVIE -> if (movieKeywords[id] == null || force) movieService.getKeywords(id, force)
            MediaViewType.TV -> if (tvKeywords[id] == null || force) tvService.getKeywords(id, force)
            else -> {}
        }
    }

    suspend fun getWatchProviders(id: Int, type: MediaViewType, force: Boolean = false) {
        when (type) {
            MediaViewType.MOVIE -> if (movieWatchProviders[id] == null || force) movieService.getWatchProviders(id, force)
            MediaViewType.TV -> if (tvWatchProviders[id] == null || force) tvService.getWatchProviders(id, force)
            else -> {}
        }
    }

    suspend fun getExternalIds(id: Int, type: MediaViewType, force: Boolean = false) {
        when (type) {
            MediaViewType.MOVIE -> if (movieExternalIds[id] == null || force) movieService.getExternalIds(id, force)
            MediaViewType.TV -> if (tvExternalIds[id] == null || force) tvService.getExternalIds(id, force)
            MediaViewType.PERSON -> if (peopleExternalIdsMap[id] == null || force) peopleService.getExternalIds(id, force)
            else -> {}
        }
    }

    suspend fun getAccountStates(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.getAccountStates(id)
            MediaViewType.TV -> tvService.getAccountStates(id)
            else -> {}
        }
    }

    suspend fun postRating(id: Int, rating: Float, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.postRating(id, RatingBody(rating))
            MediaViewType.TV -> tvService.postRating(id, RatingBody(rating))
            else -> {}
        }
    }

    suspend fun deleteRating(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE ->  movieService.deleteRating(id)
            MediaViewType.TV -> tvService.deleteRating(id)
            else -> {}
        }
    }

    fun getSimilar(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> {
                similarMovies[id] = createPagingFlow(
                    fetcher = { p -> movieService.getSimilar(id, p) },
                    processor = { it.results }
                )
            }
            MediaViewType.TV -> {
                similarTv[id] = createPagingFlow(
                    fetcher = { p -> tvService.getSimilar(id, p) },
                    processor = { it.results }
                )
            }
            else -> {}
        }
    }

    fun getKeywordResults(id: Int, keyword: String, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> {
                movieKeywordResults[id] = createPagingFlow(
                    fetcher = { p -> movieService.discover(keyword, p) },
                    processor = { it.results }
                )
            }
            MediaViewType.TV -> {
                tvKeywordResults[id] = createPagingFlow(
                    fetcher = { p -> tvService.discover(keyword, p) },
                    processor = { it.results }
                )
            }
            else -> {}
        }
    }

    suspend fun getReleaseDates(id: Int, force: Boolean = false) {
        if (movieReleaseDates[id] == null || force) {
            movieService.getReleaseDates(id, force)
        }
    }

    suspend fun getContentRatings(id: Int, force: Boolean = false) {
        if (tvContentRatings[id] == null || force) {
            tvService.getContentRatings(id, force)
        }
    }

    suspend fun getSeason(seriesId: Int, seasonId: Int, force: Boolean = false) {
        if (tvSeasons[seriesId] == null || force) {
            tvService.getSeason(seriesId, seasonId, force)
        }
    }

    suspend fun getSeasonAccountStates(seriesId: Int, seasonId: Int, force: Boolean = false) {
        if (tvSeasonAccountStates[seriesId]?.get(seasonId) == null || force) {
            tvService.getSeasonAccountStates(seriesId, seasonId, force)
        }
    }

    suspend fun getSeasonImages(seriesId: Int, seasonId: Int, force: Boolean = false) {
        if (tvSeasonImages[seriesId]?.get(seasonId) == null || force) {
            tvService.getSeasonImages(seriesId, seasonId, force)
        }
    }

    suspend fun getSeasonVideos(seriesId: Int, seasonId: Int, force: Boolean = false) {
        if (tvSeasonVideos[seriesId]?.get(seasonId) == null || force) {
            tvService.getSeasonVideos(seriesId, seasonId, force)
        }
    }

    suspend fun getSeasonCredits(seriesId: Int, seasonId: Int, force: Boolean = false) {
        if (tvSeasonCast[seriesId]?.get(seasonId) == null ||
            tvSeasonCrew[seriesId]?.get(seasonId) == null ||
            force
        ) {
            tvService.getSeasonCredits(seriesId, seasonId, force)
        }
    }

    suspend fun getSeasonWatchProviders(seriesId: Int, seasonId: Int, force: Boolean = false) {
        if (tvSeasonWatchProviders[seriesId]?.get(seasonId) == null || force) {
            tvService.getSeasonWatchProviders(seriesId, seasonId, force)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun monitorDetailsLoadingRefreshing(refreshing: MutableState<Boolean>) {
        val movieDetails = remember { movieDetailsLoadingState }
        val movieImages = remember { movieImagesLoadingState }
        val movieCastCrew = remember { movieCastCrewLoadingState }
        val movieVideos = remember { movieVideosLoadingState }
        val movieReviews = remember { movieReviewsLoadingState }
        val movieKeywords = remember { movieKeywordsLoadingState }
        val movieWatchProviders = remember { movieWatchProvidersLoadingState }
        val movieExternalIds = remember { movieExternalIdsLoadingState }
        val movieReleaseDates = remember { movieReleaseDatesLoadingState }
        val movieAccountStates = remember { movieAccountStatesLoadingState }
        val tvDetails = remember { tvDetailsLoadingState }
        val tvImages = remember { tvImagesLoadingState }
        val tvCastCrew = remember { tvCastCrewLoadingState }
        val tvVideos = remember { tvVideosLoadingState }
        val tvReviews = remember { tvReviewsLoadingState }
        val tvKeywords = remember { tvKeywordsLoadingState }
        val tvWatchProviders = remember { tvWatchProvidersLoadingState }
        val tvExternalIds = remember { tvExternalIdsLoadingState }
        val tvSeasons = remember { tvSeasonsLoadingState }
        val tvContentRatings = remember { tvContentRatingsLoadingState }
        val tvAccountStates = remember { tvAccountStatesLoadingState }
        val peopleDetails = remember { peopleDetailsLoadingState }
        val peopleCastCrew = remember { peopleCastCrewLoadingState }
        val peopleImages = remember { peopleImagesLoadingState }
        val peopleExternalIds = remember { peopleExternalIdsLoadingState }

        refreshing.value = movieDetails.value == LoadingState.REFRESHING || movieImages.value == LoadingState.REFRESHING ||
                movieCastCrew.value == LoadingState.REFRESHING ||  movieVideos.value == LoadingState.REFRESHING ||
                movieReviews.value == LoadingState.REFRESHING || movieKeywords.value == LoadingState.REFRESHING ||
                movieWatchProviders.value == LoadingState.REFRESHING || movieExternalIds.value == LoadingState.REFRESHING ||
                movieReleaseDates.value == LoadingState.REFRESHING || movieAccountStates.value == LoadingState.REFRESHING ||
                tvDetails.value == LoadingState.REFRESHING || tvImages.value == LoadingState.REFRESHING ||
                tvCastCrew.value == LoadingState.REFRESHING || tvVideos.value == LoadingState.REFRESHING ||
                tvReviews.value == LoadingState.REFRESHING || tvKeywords.value == LoadingState.REFRESHING ||
                tvWatchProviders.value == LoadingState.REFRESHING || tvExternalIds.value == LoadingState.REFRESHING ||
                tvSeasons.value == LoadingState.REFRESHING || tvContentRatings.value == LoadingState.REFRESHING ||
                tvAccountStates.value == LoadingState.REFRESHING || peopleDetails.value == LoadingState.REFRESHING ||
                peopleCastCrew.value == LoadingState.REFRESHING || peopleImages.value == LoadingState.REFRESHING ||
                peopleExternalIds.value == LoadingState.REFRESHING
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun monitorDetailsLoading(loading: MutableState<Boolean>) {
        val movieDetails = remember { movieDetailsLoadingState }
        val movieImages = remember { movieImagesLoadingState }
        val movieCastCrew = remember { movieCastCrewLoadingState }
        val movieVideos = remember { movieVideosLoadingState }
        val movieReviews = remember { movieReviewsLoadingState }
        val movieKeywords = remember { movieKeywordsLoadingState }
        val movieWatchProviders = remember { movieWatchProvidersLoadingState }
        val movieExternalIds = remember { movieExternalIdsLoadingState }
        val movieReleaseDates = remember { movieReleaseDatesLoadingState }
        val movieAccountStates = remember { movieAccountStatesLoadingState }
        val tvDetails = remember { tvDetailsLoadingState }
        val tvImages = remember { tvImagesLoadingState }
        val tvCastCrew = remember { tvCastCrewLoadingState }
        val tvVideos = remember { tvVideosLoadingState }
        val tvReviews = remember { tvReviewsLoadingState }
        val tvKeywords = remember { tvKeywordsLoadingState }
        val tvWatchProviders = remember { tvWatchProvidersLoadingState }
        val tvExternalIds = remember { tvExternalIdsLoadingState }
        val tvSeasons = remember { tvSeasonsLoadingState }
        val tvContentRatings = remember { tvContentRatingsLoadingState }
        val tvAccountStates = remember { tvAccountStatesLoadingState }
        val peopleDetails = remember { peopleDetailsLoadingState }
        val peopleCastCrew = remember { peopleCastCrewLoadingState }
        val peopleImages = remember { peopleImagesLoadingState }
        val peopleExternalIds = remember { peopleExternalIdsLoadingState }

        loading.value = listOf(
            movieDetails.value, movieImages.value, movieCastCrew.value, movieVideos.value, movieReviews.value,
            movieKeywords.value, movieWatchProviders.value, movieExternalIds.value, movieReleaseDates.value,
            movieAccountStates.value, tvDetails.value, tvImages.value, tvCastCrew.value, tvVideos.value,
            tvReviews.value, tvKeywords.value, tvWatchProviders.value, tvExternalIds.value, tvSeasons.value,
            tvContentRatings.value, tvAccountStates.value, peopleDetails.value, peopleCastCrew.value,
            peopleImages.value, peopleExternalIds.value
        ).any { it == LoadingState.LOADING || it == LoadingState.REFRESHING }
    }

}