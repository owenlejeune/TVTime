package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Episode
import com.owenlejeune.tvtime.extensions.createEpisodeKey
import com.owenlejeune.tvtime.extensions.toCompositeParts
import com.owenlejeune.tvtime.ui.components.BackButton
import com.owenlejeune.tvtime.ui.components.CastCard
import com.owenlejeune.tvtime.ui.components.DetailHeader
import com.owenlejeune.tvtime.ui.components.TVTTopAppBar
import com.owenlejeune.tvtime.ui.theme.Typography
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private fun fetchData(
    mainViewModel: MainViewModel,
    seriesId: Int,
    seasonNumber: Int,
    episodeNumber: Int,
    force: Boolean = false
) {
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch { mainViewModel.getEpisode(seriesId, seasonNumber, episodeNumber, force) }
    scope.launch { mainViewModel.getEpisodeCredits(seriesId, seasonNumber, episodeNumber, force) }
    scope.launch { mainViewModel.getEpisodeImages(seriesId, seasonNumber, episodeNumber, force) }
    if (SessionManager.isLoggedIn) {
        scope.launch { mainViewModel.getEpisodeAccountStates(seriesId, seasonNumber, episodeNumber, force) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailsScreen(
    appNavController: NavController,
    codedId: Int
) {
    val mainViewModel = viewModel<MainViewModel>()
    val applicationViewModel = viewModel<ApplicationViewModel>()

    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    val (a, b) = codedId.toCompositeParts()
    val episodeNumber = minOf(a, b)
    val (c, d) = maxOf(a, b).toCompositeParts()
    val seasonNumber = minOf(c, d)
    val seriesId = maxOf(c, d)
    LaunchedEffect(Unit) {
        fetchData(mainViewModel, seriesId, seasonNumber, episodeNumber)
    }

    val episodeKey = createEpisodeKey(seriesId, seasonNumber, episodeNumber)

    val episodesMap = remember { mainViewModel.tvEpisodes }
    val episode = episodesMap[episodeKey]

    Scaffold(
        topBar = {
            TVTTopAppBar(
                title = { },
                appNavController = appNavController,
                navigationIcon = { BackButton(navController = appNavController) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            episode?.let {
                EpisodeContent(
                    seriesId = seriesId,
                    episodeKey = episodeKey,
                    episode = episode,
                    appNavController = appNavController,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun EpisodeContent(
    seriesId: Int,
    episodeKey: String,
    episode: Episode,
    appNavController: NavController,
    mainViewModel: MainViewModel
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailHeader(
            backdropUrl = TmdbUtils.getFullEpisodeStillPath(episode.stillPath)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = episode.name,
                color = MaterialTheme.colorScheme.secondary,
                style = Typography.headlineLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            TmdbUtils.convertEpisodeDate(episode.airDate)?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            val castMap = remember { mainViewModel.tvEpisodeCast }
            val cast = castMap[episodeKey]
            cast?.let {
                CastCard(
                    title = stringResource(R.string.cast_label),
                    isLoading = false,
                    cast = cast,
                    appNavController = appNavController
                )
            }

            val guestStarsMap = remember { mainViewModel.tvEpisodeGuestStars }
            val guestStars = guestStarsMap[episodeKey]
            guestStars?.let {
                CastCard(
                    title = stringResource(id = R.string.guest_stars_label),
                    isLoading = false,
                    cast = guestStars,
                    appNavController = appNavController
                )
            }

            val crewMap = remember { mainViewModel.tvEpisodeCrew }
            val crew = crewMap[episodeKey]
            crew?.let {
                CastCard(
                    title = stringResource(id = R.string.crew_label),
                    isLoading = false,
                    cast = crew,
                    appNavController = appNavController
                )
            }
        }
    }
}