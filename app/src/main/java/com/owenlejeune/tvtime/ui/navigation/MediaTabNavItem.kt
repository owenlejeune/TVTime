package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.HomePageService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import com.owenlejeune.tvtime.ui.screens.main.MediaTabContent
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import com.owenlejeune.tvtime.ui.viewmodel.MediaTabViewModel
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.core.component.inject
import retrofit2.Response

sealed class MediaTabNavItem(
    stringRes: Int,
    route: String,
    val screen: MediaNavComposableFun,
    val movieViewModel: MediaTabViewModel?,
    val tvViewModel: MediaTabViewModel?
): TabNavItem(route) {
    private val resourceUtils: ResourceUtils by inject()

    override val name = resourceUtils.getString(stringRes)

    companion object  {
        val MovieItems = listOf(NowPlaying, Popular, Upcoming, TopRated)
        val TvItems = listOf(OnTheAir, Popular, AiringToday, TopRated)

        private val Items = listOf(NowPlaying, Popular, TopRated, Upcoming, AiringToday, OnTheAir)

        fun getByRoute(route: String?): MediaTabNavItem? {
            return Items.firstOrNull { it.route == route }
        }
    }

    object Popular: MediaTabNavItem(
        stringRes = R.string.nav_popular_title,
        route = "popular_route",
        screen = screenContent,
        movieViewModel = MediaTabViewModel.PopularMoviesVM,
        tvViewModel = MediaTabViewModel.PopularTvVM
    )
    object TopRated: MediaTabNavItem(
        stringRes = R.string.nav_top_rated_title,
        route = "top_rated_route",
        screen = screenContent,
        movieViewModel = MediaTabViewModel.TopRatedMoviesVM,
        tvViewModel = MediaTabViewModel.TopRatedTvVM
    )
    object NowPlaying: MediaTabNavItem(
        stringRes = R.string.nav_now_playing_title,
        route = "now_playing_route",
        screen = screenContent,
        movieViewModel = MediaTabViewModel.NowPlayingMoviesVM,
        tvViewModel = null
    )
    object Upcoming: MediaTabNavItem(
        stringRes = R.string.nav_upcoming_title,
        route = "upcoming_route",
        screen = screenContent,
        movieViewModel = MediaTabViewModel.UpcomingMoviesVM,
        tvViewModel = null
    )
    object AiringToday: MediaTabNavItem(
        stringRes = R.string.nav_tv_airing_today_title,
        route = "airing_today_route",
        screen = screenContent,
        movieViewModel = null,
        tvViewModel = MediaTabViewModel.AiringTodayTvVM
    )
    object OnTheAir: MediaTabNavItem(
        stringRes = R.string.nav_tv_on_the_air,
        route = "on_the_air_route",
        screen = screenContent,
        movieViewModel = null,
        tvViewModel = MediaTabViewModel.OnTheAirTvVM
    )
}

private val screenContent: MediaNavComposableFun = { appNavController, mediaViewType, mediaTabItem ->
    MediaTabContent(appNavController = appNavController, mediaType = mediaViewType, mediaTabItem = mediaTabItem)
}

typealias MediaNavComposableFun = @Composable (NavHostController, MediaViewType, MediaTabNavItem) -> Unit

typealias MediaFetchFun = suspend (service: HomePageService, page: Int) -> Response<out HomePageResponse>