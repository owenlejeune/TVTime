package com.owenlejeune.tvtime.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Episode
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Season
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SeasonAccountStates
import com.owenlejeune.tvtime.extensions.toCompositeParts
import com.owenlejeune.tvtime.ui.components.BackButton
import com.owenlejeune.tvtime.ui.components.CastCrewCard
import com.owenlejeune.tvtime.ui.components.ContentCard
import com.owenlejeune.tvtime.ui.components.DetailHeader
import com.owenlejeune.tvtime.ui.components.EpisodeItem
import com.owenlejeune.tvtime.ui.components.ImagesCard
import com.owenlejeune.tvtime.ui.components.TVTTopAppBar
import com.owenlejeune.tvtime.ui.components.VideosCard
import com.owenlejeune.tvtime.ui.components.WatchProvidersCard
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
    seasonNumber: Int,
    seriesId: Int,
    force: Boolean = false
) {
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch { mainViewModel.getSeason(seriesId, seasonNumber, force) }
    if (SessionManager.isLoggedIn) {
        scope.launch { mainViewModel.getSeasonAccountStates(seriesId, seasonNumber, force) }
    }
    scope.launch { mainViewModel.getSeasonImages(seriesId, seasonNumber, force) }
    scope.launch { mainViewModel.getSeasonVideos(seriesId, seasonNumber, force) }
    scope.launch { mainViewModel.getSeasonCredits(seriesId, seasonNumber, force) }
    scope.launch { mainViewModel.getSeasonWatchProviders(seriesId, seasonNumber, force) }
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
            season?.let {
                SeasonContent(
                    seriesId = seriesId,
                    appNavController = appNavController,
                    mainViewModel = mainViewModel,
                    season = season
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SeasonContent(
    seriesId: Int,
    appNavController: NavController,
    mainViewModel: MainViewModel,
    season: Season
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailHeader(
            posterUrl = TmdbUtils.getFullPosterPath(season.posterPath),
            elevation = 0.dp,
            expandedPosterAsBackdrop = true
        )

        var isExpanded by remember { mutableStateOf(true) }
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row {
                Text(
                    text = season.name,
                    color = MaterialTheme.colorScheme.secondary,
                    style = Typography.headlineLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                var currentRotation by remember { mutableFloatStateOf(180f) }
                val rotation = remember { Animatable(currentRotation) }
                LaunchedEffect(isExpanded) {
                    rotation.animateTo(
                        targetValue = if (isExpanded) 180f else 0f,
                        animationSpec = tween(200, easing = LinearEasing)
                    ) {
                        currentRotation = value
                    }
                }
                Icon(
                    imageVector = Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(currentRotation)
                        .clickable {
                            isExpanded = !isExpanded
                        },
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            val accountStatesMap = remember { mainViewModel.tvSeasonAccountStates }
            val accountStates = accountStatesMap[seriesId]?.get(season.seasonNumber)

            AnimatedVisibility(
                visible = isExpanded
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    season.episodes.forEach { episode ->
                        DrawEpisodeCard(
                            episode = episode,
                            accountStates = accountStates,
                            appNavController = appNavController
                        )
                    }
                }
            }

            val imagesMap = remember { mainViewModel.tvSeasonImages }
            val images = imagesMap[seriesId]?.get(season.seasonNumber)
            images?.let {
                ImagesCard(images = images.posters)
            }

            val videosMap = remember { mainViewModel.tvSeasonVideos }
            val videos = videosMap[seriesId]?.get(season.seasonNumber)
            if (videos?.any { it.isOfficial } == true) {
                VideosCard(videos = videos, modifier = Modifier.fillMaxWidth())
            }

            val watchProvidersMap = remember { mainViewModel.tvSeasonWatchProviders }
            val watchProviders = watchProvidersMap[seriesId]?.get(season.seasonNumber)
            watchProviders?.let { providers ->
                if (providers.buy?.isNotEmpty() == true || providers.rent?.isNotEmpty() == true || providers.flaterate?.isNotEmpty() == true) {
                    WatchProvidersCard(providers = providers)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrawEpisodeCard(
    episode: Episode,
    accountStates: SeasonAccountStates?,
    appNavController: NavController
) {
    val rating = accountStates?.results?.find { it.id == episode.id }?.takeUnless { !it.isRated }?.rating
    SeasonEpisodeItem(appNavController = appNavController, episode = episode, rating = rating)
}

@Composable
private fun SeasonEpisodeItem(
    appNavController: NavController,
    episode: Episode,
    rating: Int?
) {
    ContentCard {
        EpisodeItem(
            episode = episode,
            elevation = 0.dp,
            maxDescriptionLines = 5,
            rating = rating
        )

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