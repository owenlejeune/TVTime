package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.HomePageService
import com.owenlejeune.tvtime.api.tmdb.model.HomePageResponse
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import com.owenlejeune.tvtime.ui.screens.tabs.bottom.MediaTabContent
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

typealias NavComposableFun = @Composable (NavHostController, MediaViewType, MediaFetchFun) -> Unit

private val screenContent: NavComposableFun = { appNavController, mediaViewType, mediaFetchFun ->
    MediaTabContent(appNavController = appNavController, mediaType = mediaViewType, mediaFetchFun = mediaFetchFun)
}

typealias MediaFetchFun = suspend (service: HomePageService, page: Int) -> Response<out HomePageResponse>

abstract class TabNavItem(val route: String, val screen: NavComposableFun, val mediaFetchFun: MediaFetchFun): KoinComponent {
    abstract val name: String
}

sealed class MainTabNavItem(stringRes: Int, route: String, screen: NavComposableFun, mediaFetchFun: MediaFetchFun)
    : TabNavItem(route, screen, mediaFetchFun)
{
    private val resourceUtils: ResourceUtils by inject()

    override val name = resourceUtils.getString(stringRes)

    companion object  {
        val MovieItems = listOf(Popular, TopRated, NowPlaying, Upcoming)
        val TvItems = listOf(Popular, TopRated, AiringToday, OnTheAir)

        private val Items = listOf(NowPlaying, Popular, TopRated, Upcoming, AiringToday, OnTheAir)

        fun getByRoute(route: String?): MainTabNavItem? {
            return Items.firstOrNull { it.route == route }
        }
    }

    object Popular: MainTabNavItem(R.string.nav_popular_title, "popular_route", screenContent, { s, p -> s.getPopular(p) } )
    object TopRated: MainTabNavItem(R.string.nav_top_rated_title, "top_rated_route", screenContent, { s, p -> s.getTopRated(p) } )
    object NowPlaying: MainTabNavItem(R.string.nav_now_playing_title, "now_playing_route", screenContent, { s, p -> s.getNowPlaying(p) } )
    object Upcoming: MainTabNavItem(R.string.nav_upcoming_title, "upcoming_route", screenContent, { s, p -> s.getUpcoming(p) } )
    object AiringToday: MainTabNavItem(R.string.nav_tv_airing_today_title, "airing_today_route", screenContent, { s, p -> s.getNowPlaying(p) } )
    object OnTheAir: MainTabNavItem(R.string.nav_tv_on_the_air, "on_the_air_route", screenContent, { s, p -> s.getUpcoming(p) } )
}