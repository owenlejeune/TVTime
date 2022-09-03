package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.HomePageService
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePagingSource
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import com.owenlejeune.tvtime.ui.screens.main.MediaTabContent
import com.owenlejeune.tvtime.utils.ResourceUtils
import kotlinx.coroutines.flow.Flow
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
        val MovieItems = listOf(Popular, NowPlaying, Upcoming, TopRated)
        val TvItems = listOf(Popular, AiringToday, OnTheAir, TopRated)

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

sealed class MediaTabViewModel(service: HomePageService, mediaFetchFun: MediaFetchFun): ViewModel() {
    val mediaItems: Flow<PagingData<TmdbItem>> = Pager(PagingConfig(pageSize = Int.MAX_VALUE)) {
        HomePagePagingSource(service = service, mediaFetch = mediaFetchFun)
    }.flow.cachedIn(viewModelScope)

    object PopularMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getPopular(p) })
    object TopRatedMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getTopRated(p) })
    object NowPlayingMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getNowPlaying(p) })
    object UpcomingMoviesVM: MediaTabViewModel(MoviesService(), { s, p -> s.getUpcoming(p) })
    object PopularTvVM: MediaTabViewModel(TvService(), { s, p -> s.getPopular(p) })
    object TopRatedTvVM: MediaTabViewModel(TvService(), { s, p -> s.getTopRated(p) })
    object AiringTodayTvVM: MediaTabViewModel(TvService(), { s, p -> s.getNowPlaying(p) })
    object OnTheAirTvVM: MediaTabViewModel(TvService(), { s, p -> s.getUpcoming(p) })

}