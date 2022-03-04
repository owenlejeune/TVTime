package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.model.RatedMedia
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import com.owenlejeune.tvtime.ui.screens.tabs.bottom.AccountTabContent
import com.owenlejeune.tvtime.utils.ResourceUtils
import com.owenlejeune.tvtime.utils.SessionManager
import org.koin.core.component.inject

sealed class AccountTabNavItem(stringRes: Int, route: String, val mediaType: MediaViewType, val screen: AccountNavComposableFun, val listFetchFun: ListFetchFun): TabNavItem(route) {
    private val resourceUtils: ResourceUtils by inject()

    override val name = resourceUtils.getString(stringRes)

    companion object {
        val GuestItems = listOf(RatedMovies, RatedTvShows)//, RatedTvEpisodes)
    }

    object RatedMovies: AccountTabNavItem(R.string.nav_rated_movies_title, "rated_movies_route", MediaViewType.MOVIE, screenContent, { SessionManager.currentSession?.ratedMovies ?: emptyList() } )
    object RatedTvShows: AccountTabNavItem(R.string.nav_rated_shows_title, "rated_shows_route", MediaViewType.TV, screenContent, { SessionManager.currentSession?.ratedTvShows ?: emptyList() } )
    object RatedTvEpisodes: AccountTabNavItem(R.string.nav_rated_episodes_title, "rated_episodes_route", MediaViewType.EPISODE, screenContent, { SessionManager.currentSession?.ratedTvEpisodes ?: emptyList() } )
}

private val screenContent: AccountNavComposableFun = { appNavController, mediaViewType, listFetchFun ->
    AccountTabContent(appNavController = appNavController, mediaViewType = mediaViewType, listFetchFun = listFetchFun)
}

typealias ListFetchFun = () -> List<RatedMedia>

typealias AccountNavComposableFun = @Composable (NavHostController, MediaViewType, ListFetchFun) -> Unit