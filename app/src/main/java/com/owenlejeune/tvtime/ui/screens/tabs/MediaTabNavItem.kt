package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.HomePageService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import com.owenlejeune.tvtime.ui.screens.tabs.MediaTabContent
import com.owenlejeune.tvtime.ui.screens.tabs.MediaTabTrendingContent
import com.owenlejeune.tvtime.utils.ResourceUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.utils.types.TabNavItem
import com.owenlejeune.tvtime.utils.types.ViewableMediaTypeException
import org.koin.core.component.inject
import retrofit2.Response

sealed class MediaTabNavItem(
    stringRes: Int,
    route: String,
    val screen: MediaNavComposableFun,
    val type: Type
): TabNavItem(route) {
    private val resourceUtils: ResourceUtils by inject()

    override val name = resourceUtils.getString(stringRes)

    enum class Type {
        POPULAR,
        NOW_PLAYING,
        UPCOMING,
        TOP_RATED,
        TRENDING
    }

    companion object  {
        private val MovieItems = listOf(Trending, NowPlaying, Popular, Upcoming, TopRated)
        private val TvItems = listOf(Trending, OnTheAir, Popular, AiringToday, TopRated)

        fun itemsForType(type: MediaViewType): List<MediaTabNavItem> {
            return when (type) {
                MediaViewType.MOVIE -> MovieItems
                MediaViewType.TV -> TvItems
                else -> throw ViewableMediaTypeException(type)
            }
        }
    }

    object Popular: MediaTabNavItem(
        stringRes = R.string.nav_popular_title,
        route = "popular_route",
        screen = screenContent,
        type = Type.POPULAR
    )
    object TopRated: MediaTabNavItem(
        stringRes = R.string.nav_top_rated_title,
        route = "top_rated_route",
        screen = screenContent,
        type = Type.TOP_RATED
    )
    object NowPlaying: MediaTabNavItem(
        stringRes = R.string.nav_now_playing_title,
        route = "now_playing_route",
        screen = screenContent,
        type = Type.NOW_PLAYING
    )
    object Upcoming: MediaTabNavItem(
        stringRes = R.string.nav_upcoming_title,
        route = "upcoming_route",
        screen = screenContent,
        type = Type.UPCOMING
    )
    object AiringToday: MediaTabNavItem(
        stringRes = R.string.nav_tv_airing_today_title,
        route = "airing_today_route",
        screen = screenContent,
        type = Type.NOW_PLAYING
    )
    object OnTheAir: MediaTabNavItem(
        stringRes = R.string.nav_tv_on_the_air,
        route = "on_the_air_route",
        screen = screenContent,
        type = Type.UPCOMING
    )

    object Trending: MediaTabNavItem(
        stringRes = R.string.nav_trending_title,
        route = "trending_route",
        screen = trendingScreenContent,
        type = Type.TRENDING
    )
}

private val screenContent: MediaNavComposableFun = { appNavController, mediaViewType, mediaTabItem ->
    MediaTabContent(appNavController = appNavController, mediaType = mediaViewType, mediaTabItem = mediaTabItem)
}

private val trendingScreenContent: MediaNavComposableFun = { appNavController, mediaViewType, mediaTabItem ->
    MediaTabTrendingContent(appNavController = appNavController, mediaType = mediaViewType, mediaTabItem = mediaTabItem)
}

typealias MediaNavComposableFun = @Composable (NavHostController, MediaViewType, MediaTabNavItem) -> Unit

typealias MediaFetchFun = suspend (service: HomePageService, page: Int) -> Response<out HomePageResponse>