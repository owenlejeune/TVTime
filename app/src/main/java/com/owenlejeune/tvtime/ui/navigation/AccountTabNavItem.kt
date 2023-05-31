package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.V4AccountList
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import com.owenlejeune.tvtime.ui.screens.main.AccountTabContent
import com.owenlejeune.tvtime.ui.screens.main.RecommendedAccountTabContent
import com.owenlejeune.tvtime.utils.ResourceUtils
import com.owenlejeune.tvtime.utils.SessionManager
import org.koin.core.component.inject
import kotlin.reflect.KClass

sealed class AccountTabNavItem(
    stringRes: Int,
    route: String,
    noContentStringRes: Int,
    val mediaType: MediaViewType,
    val screen: AccountNavComposableFun,
    val listFetchFun: ListFetchFun,
    val listType: KClass<*>,
    val ordinal: Int
): TabNavItem(route) {
    private val resourceUtils: ResourceUtils by inject()

    override val name = resourceUtils.getString(stringRes)
    val noContentText = resourceUtils.getString(noContentStringRes)

    companion object {
        val GuestItems
            get() = listOf(RatedMovies, RatedTvShows, RatedTvEpisodes)

        val AuthorizedItems
            get() = listOf(
                RatedMovies, RatedTvShows, RatedTvEpisodes, FavoriteMovies, FavoriteTvShows,
                MovieWatchlist, TvWatchlist, UserLists, RecommendedMovies, RecommendedTv
            ).filter { it.ordinal > -1 }.sortedBy { it.ordinal }
    }

    object RatedMovies: AccountTabNavItem(
        R.string.nav_rated_movies_title,
        "rated_movies_route",
        R.string.no_rated_movies,
        MediaViewType.MOVIE,
        screenContent,
        { SessionManager.currentSession?.ratedMovies ?: emptyList() },
        RatedMovie::class,
        0
    )
    object RatedTvShows: AccountTabNavItem(
        R.string.nav_rated_shows_title,
        "rated_shows_route",
        R.string.no_rated_tv,
        MediaViewType.TV,
        screenContent,
        { SessionManager.currentSession?.ratedTvShows ?: emptyList() },
        RatedTv::class,
        1
    )
    object RatedTvEpisodes: AccountTabNavItem(
        R.string.nav_rated_episodes_title,
        "rated_episodes_route",
        R.string.no_rated_episodes,
        MediaViewType.EPISODE,
        screenContent,
        { SessionManager.currentSession?.ratedTvEpisodes ?: emptyList() },
        RatedEpisode::class,
        -1 //2
    )
    object FavoriteMovies: AccountTabNavItem(
        R.string.nav_favorite_movies_title,
        "favorite_movies_route",
        R.string.no_favorite_movies,
        MediaViewType.MOVIE,
        screenContent,
        { SessionManager.currentSession?.favoriteMovies ?: emptyList() },
        FavoriteMovie::class,
        3
    )
    object FavoriteTvShows: AccountTabNavItem(
        R.string.nav_favorite_tv_show_title,
        "favorite_shows_route",
        R.string.no_favorite_tv,
        MediaViewType.TV,
        screenContent,
        { SessionManager.currentSession?.favoriteTvShows ?: emptyList() },
        FavoriteTvSeries::class,
        4
    )
    object MovieWatchlist: AccountTabNavItem(
        R.string.nav_movie_watchlist_title,
        "movie_watchlist_route",
        R.string.no_watchlist_movies,
        MediaViewType.MOVIE,
        screenContent,
        { SessionManager.currentSession?.movieWatchlist ?: emptyList() },
        WatchlistMovie::class,
        5
    )
    object TvWatchlist: AccountTabNavItem(
        R.string.nav_tv_watchlist_title,
        "tv_watchlist_route",
        R.string.no_watchlist_tv,
        MediaViewType.TV,
        screenContent,
        { SessionManager.currentSession?.tvWatchlist ?: emptyList() },
        WatchlistTvSeries::class,
        6
    )

    object UserLists: AccountTabNavItem(
        R.string.nav_user_lists_title,
        "user_lists_route",
        R.string.no_lists,
        MediaViewType.LIST,
        screenContent,
        { SessionManager.currentSession?.accountLists ?: emptyList() },
        V4AccountList::class,
        7
    )

    object RecommendedMovies: AccountTabNavItem(
        R.string.recommended_movies_title,
        "recommended_movies_route",
        R.string.no_recommended_movies,
        MediaViewType.MOVIE,
        recommendedScreenContent,
        { emptyList() },
        V4AccountList::class,
        8
    )

    object RecommendedTv: AccountTabNavItem(
        R.string.recommended_tv_title,
        "recommended_tv_route",
        R.string.no_recommended_tv,
        MediaViewType.TV,
        recommendedScreenContent,
        { emptyList() },
        V4AccountList::class,
        9
    )
}

private val screenContent: AccountNavComposableFun = { noContentText, appNavController, mediaViewType, listFetchFun, clazz ->
    AccountTabContent(
        noContentText = noContentText,
        appNavController = appNavController,
        mediaViewType = mediaViewType,
        listFetchFun = listFetchFun,
        clazz = clazz
    )
}

private val recommendedScreenContent: AccountNavComposableFun = { noContentText, appNavController, mediaViewType, _, _ ->
    RecommendedAccountTabContent(
        noContentText = noContentText,
        appNavController = appNavController,
        mediaViewType = mediaViewType,
    )
}

typealias ListFetchFun = () -> List<Any>

typealias AccountNavComposableFun = @Composable (String, NavHostController, MediaViewType, ListFetchFun, KClass<*>) -> Unit