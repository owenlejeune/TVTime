package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Episode
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Season
import com.owenlejeune.tvtime.extensions.toCompositeParts
import com.owenlejeune.tvtime.ui.components.BackButton
import com.owenlejeune.tvtime.ui.components.CastCrewCard
import com.owenlejeune.tvtime.ui.components.ContentCard
import com.owenlejeune.tvtime.ui.components.DetailHeader
import com.owenlejeune.tvtime.ui.components.EpisodeItem
import com.owenlejeune.tvtime.ui.components.TVTTopAppBar
import com.owenlejeune.tvtime.ui.theme.Typography
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private fun fetchData(
    mainViewModel: MainViewModel,
    seasonNumber: Int,
    seriesId: Int,
    force: Boolean = false
) {
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch { mainViewModel.getSeason(seriesId, seasonNumber, force) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonDetailsScreen(
    appNavController: NavController,
    codedId: Int
) {
    val mainViewModel = viewModel<MainViewModel>()
    val applicationViewModel = viewModel<ApplicationViewModel>()

    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    val (a, b) = codedId.toCompositeParts()
    val seasonNumber = minOf(a, b)
    val seriesId = maxOf(a, b)
    LaunchedEffect(Unit) {
        fetchData(mainViewModel, seasonNumber, seriesId)
    }

    val seasonsMap = remember { mainViewModel.tvSeasons }
    val season = seasonsMap[seriesId]?.firstOrNull { it.seasonNumber == seasonNumber }
    
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
            SeasonContent(appNavController = appNavController, season = season)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SeasonContent(
    appNavController: NavController,
    season: Season?
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailHeader(
            posterUrl = TmdbUtils.getFullPosterPath(season?.posterPath),
            elevation = 0.dp,
            expandedPosterAsBackdrop = true
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = season?.name ?: "",
                color = MaterialTheme.colorScheme.secondary,
                style = Typography.headlineLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            season?.episodes?.forEach { episode ->
                SeasonEpisodeItem(appNavController = appNavController, episode = episode)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SeasonEpisodeItem(
    appNavController: NavController,
    episode: Episode
) {
    ContentCard {
        EpisodeItem(episode = episode, elevation = 0.dp, maxDescriptionLines = 5)

        episode.guestStars?.let { guestStars ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.guest_stars_label),
                style = Typography.headlineSmall
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                guestStars.forEach {
                    CastCrewCard(appNavController = appNavController, person = it)
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}