package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.PeopleService
import com.owenlejeune.tvtime.api.tmdb.api.v3.SimilarMoviesSource
import com.owenlejeune.tvtime.api.tmdb.api.v3.SimilarTvSource
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
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Video
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchProviders
import com.owenlejeune.tvtime.utils.types.MediaViewType
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

    val peopleMap = peopleService.peopleMap
    val peopleCastMap = peopleService.castMap
    val peopleCrewMap = peopleService.crewMap
    val peopleImagesMap = peopleService.imagesMap
    val peopleExternalIdsMap = peopleService.externalIdsMap

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
        return providesForType(type, { movieExternalIds }, { tvExternalIds })
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

    suspend fun getById(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.getById(id)
            MediaViewType.TV -> tvService.getById(id)
            MediaViewType.PERSON -> peopleService.getPerson(id)
            else -> {}
        }
    }

    suspend fun getImages(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.getImages(id)
            MediaViewType.TV -> tvService.getImages(id)
            MediaViewType.PERSON -> peopleService.getImages(id)
            else -> {}
        }
    }

    suspend fun getCastAndCrew(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.getCastAndCrew(id)
            MediaViewType.TV -> tvService.getCastAndCrew(id)
            MediaViewType.PERSON -> tvService.getCastAndCrew(id)
            else -> {}
        }
    }

    suspend fun getVideos(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.getVideos(id)
            MediaViewType.TV -> tvService.getVideos(id)
            else -> {}
        }
    }

    suspend fun getReviews(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.getReviews(id)
            MediaViewType.TV -> tvService.getReviews(id)
            else -> {}
        }
    }

    suspend fun getKeywords(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.getKeywords(id)
            MediaViewType.TV -> tvService.getKeywords(id)
            else -> {}
        }
    }

    suspend fun getWatchProviders(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.getWatchProviders(id)
            MediaViewType.TV -> tvService.getWatchProviders(id)
            else -> {}
        }
    }

    suspend fun getExternalIds(id: Int, type: MediaViewType) {
        when (type) {
            MediaViewType.MOVIE -> movieService.getExternalIds(id)
            MediaViewType.TV -> tvService.getExternalIds(id)
            MediaViewType.PERSON -> peopleService.getExternalIds(id)
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
                similarMovies[id] = Pager(PagingConfig(pageSize = 1)) {
                    SimilarMoviesSource(id)
                }.flow.cachedIn(viewModelScope)
            }
            MediaViewType.TV -> {
                similarTv[id] = Pager(PagingConfig(pageSize = 1)) {
                    SimilarTvSource(id)
                }.flow.cachedIn(viewModelScope)
            }
            else -> {}
        }
    }

    suspend fun getReleaseDates(id: Int) {
        movieService.getReleaseDates(id)
    }

    suspend fun getContentRatings(id: Int) {
        tvService.getContentRatings(id)
    }

    suspend fun getSeason(seriesId: Int, seasonId: Int) {
        tvService.getSeason(seriesId, seasonId)
    }

}