package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.HomePageService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import com.owenlejeune.tvtime.ui.screens.tabs.bottom.MediaTabContent
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.core.component.inject
import retrofit2.Response

sealed class MediaTabNavItem(stringRes: Int, route: String, val screen: MediaNavComposableFun, val mediaFetchFun: MediaFetchFun): TabNavItem(route) {
    private val resourceUtils: ResourceUtils by inject()

    override val name = resourceUtils.getString(stringRes)

    companion object  {
        val MovieItems = listOf(Popular, TopRated, NowPlaying, Upcoming)
        val TvItems = listOf(Popular, TopRated, AiringToday, OnTheAir)

        private val Items = listOf(NowPlaying, Popular, TopRated, Upcoming, AiringToday, OnTheAir)

        fun getByRoute(route: String?): MediaTabNavItem? {
            return Items.firstOrNull { it.route == route }
        }
    }

    object Popular: MediaTabNavItem(R.string.nav_popular_title, "popular_route", screenContent, { s, p -> s.getPopular(p) } )
    object TopRated: MediaTabNavItem(R.string.nav_top_rated_title, "top_rated_route", screenContent, { s, p -> s.getTopRated(p) } )
    object NowPlaying: MediaTabNavItem(R.string.nav_now_playing_title, "now_playing_route", screenContent, { s, p -> s.getNowPlaying(p) } )
    object Upcoming: MediaTabNavItem(R.string.nav_upcoming_title, "upcoming_route", screenContent, { s, p -> s.getUpcoming(p) } )
    object AiringToday: MediaTabNavItem(R.string.nav_tv_airing_today_title, "airing_today_route", screenContent, { s, p -> s.getNowPlaying(p) } )
    object OnTheAir: MediaTabNavItem(R.string.nav_tv_on_the_air, "on_the_air_route", screenContent, { s, p -> s.getUpcoming(p) } )
}

private val screenContent: MediaNavComposableFun = { appNavController, mediaViewType, mediaFetchFun ->
    MediaTabContent(appNavController = appNavController, mediaType = mediaViewType, mediaFetchFun = mediaFetchFun)
}

typealias MediaNavComposableFun = @Composable (NavHostController, MediaViewType, MediaFetchFun) -> Unit

typealias MediaFetchFun = suspend (service: HomePageService, page: Int) -> Response<out HomePageResponse>