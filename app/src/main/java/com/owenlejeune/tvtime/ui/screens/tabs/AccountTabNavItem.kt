package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteTvSeries
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistTvSeries
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AccountList
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RatedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RatedTv
import com.owenlejeune.tvtime.ui.screens.AccountTabContent
import com.owenlejeune.tvtime.utils.ResourceUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.utils.types.TabNavItem
import org.koin.core.component.inject
import kotlin.reflect.KClass

sealed class AccountTabNavItem(
    stringRes: Int,
    route: String,
    noContentStringRes: Int,
    val mediaType: MediaViewType,
    val screen: AccountNavComposableFun,
    val type: AccountTabType,
    val contentType: KClass<*>,
    val ordinal: Int
): TabNavItem(route) {
    private val resourceUtils: ResourceUtils by inject()

    override val name = resourceUtils.getString(stringRes)
    val noContentText = resourceUtils.getString(noContentStringRes)

    enum class AccountTabType {
        RATED,
        FAVORITE,
        WATCHLIST,
        LIST,
        RECOMMENDED
    }

    companion object {
        val AuthorizedItems
            get() = listOf(
                RatedMovies, RatedTvShows, /*RatedTvEpisodes, */FavoriteMovies, FavoriteTvShows,
                MovieWatchlist, TvWatchlist, UserLists, RecommendedMovies, RecommendedTv
            ).filter { it.ordinal > -1 }.sortedBy { it.ordinal }
    }

    object RatedMovies: AccountTabNavItem(
        R.string.nav_rated_movies_title,
        "rated_movies_route",
        R.string.no_rated_movies,
        MediaViewType.MOVIE,
        screenContent,
        AccountTabType.RATED,
        RatedMovie::class,
        0
    )
    object RatedTvShows: AccountTabNavItem(
        R.string.nav_rated_shows_title,
        "rated_shows_route",
        R.string.no_rated_tv,
        MediaViewType.TV,
        screenContent,
        AccountTabType.RATED,
        RatedTv::class,
        1
    )
//    object RatedTvEpisodes: AccountTabNavItem(
//        R.string.nav_rated_episodes_title,
//        "rated_episodes_route",
//        R.string.no_rated_episodes,
//        MediaViewType.EPISODE,
//        screenContent,
//        AccountTabType.RATED,
//        RatedEpisode::class,
//        -1 //2
//    )
    object FavoriteMovies: AccountTabNavItem(
        R.string.nav_favorite_movies_title,
        "favorite_movies_route",
        R.string.no_favorite_movies,
        MediaViewType.MOVIE,
        screenContent,
    AccountTabType.FAVORITE,
        FavoriteMovie::class,
        3
    )
    object FavoriteTvShows: AccountTabNavItem(
        R.string.nav_favorite_tv_show_title,
        "favorite_shows_route",
        R.string.no_favorite_tv,
        MediaViewType.TV,
        screenContent,
        AccountTabType.FAVORITE,
        FavoriteTvSeries::class,
        4
    )
    object MovieWatchlist: AccountTabNavItem(
        R.string.nav_movie_watchlist_title,
        "movie_watchlist_route",
        R.string.no_watchlist_movies,
        MediaViewType.MOVIE,
        screenContent,
        AccountTabType.WATCHLIST,
        WatchlistMovie::class,
        5
    )
    object TvWatchlist: AccountTabNavItem(
        R.string.nav_tv_watchlist_title,
        "tv_watchlist_route",
        R.string.no_watchlist_tv,
        MediaViewType.TV,
        screenContent,
        AccountTabType.WATCHLIST,
        WatchlistTvSeries::class,
        6
    )

    object UserLists: AccountTabNavItem(
        R.string.nav_user_lists_title,
        "user_lists_route",
        R.string.no_lists,
        MediaViewType.LIST,
        screenContent,
        AccountTabType.LIST,
        AccountList::class,
        7
    )

    object RecommendedMovies: AccountTabNavItem(
        R.string.recommended_movies_title,
        "recommended_movies_route",
        R.string.no_recommended_movies,
        MediaViewType.MOVIE,
        screenContent,
        AccountTabType.RECOMMENDED,
        AccountList::class,
        8
    )

    object RecommendedTv: AccountTabNavItem(
        R.string.recommended_tv_title,
        "recommended_tv_route",
        R.string.no_recommended_tv,
        MediaViewType.TV,
        screenContent,
        AccountTabType.RECOMMENDED,
        AccountList::class,
        9
    )
}

private val screenContent: AccountNavComposableFun = { noContentText, appNavController, mediaViewType, atType, clazz ->
    AccountTabContent(
        noContentText = noContentText,
        appNavController = appNavController,
        mediaViewType = mediaViewType,
        accountTabType = atType,
        clazz = clazz
    )
}

typealias AccountNavComposableFun = @Composable (String, NavHostController, MediaViewType, AccountTabNavItem.AccountTabType, KClass<*>) -> Unit