package com.owenlejeune.tvtime.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Episode
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Season
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonListScreen(
    id: Int,
    appNavController: NavController
) {
    val mainViewModel = viewModel<MainViewModel>()

    val applicationViewModel = viewModel<ApplicationViewModel>()
    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    LaunchedEffect(Unit) {
        val numSeasons = mainViewModel.detailedTv[id]!!.numberOfSeasons
        for (i in 0..numSeasons) {
            mainViewModel.getSeason(id, i)
        }
    }

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults
                    .topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = { appNavController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.content_description_back_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(12.dp)
                    .verticalScroll(state = rememberScrollState())
            ) {
                val seasonsMap = remember { mainViewModel.tvSeasons }
                val seasons = seasonsMap[id] ?: emptyList()

                seasons.sortedBy { it.seasonNumber }.forEach { season ->
                    SeasonSection(season = season)
                }
            }
        }
    }
}

@Composable
private fun SeasonSection(season: Season) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {

                }
        ) {
            Text(
                text = season.name,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(vertical = 12.dp, horizontal = 4.dp)
            )
        }

        var currentRotation by remember { mutableFloatStateOf(0f) }
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
                .size(32.dp)
                .rotate(currentRotation)
                .clickable {
                    isExpanded = !isExpanded
                },
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            season.episodes.forEach { episode ->
                EpisodeItem(episode = episode)
            }
        }
    }
}

@Composable
private fun EpisodeItem(episode: Episode) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            }
    ) {
        Box(
            modifier = Modifier.height(112.dp)
        ) {
            episode.stillPath?.let {
//                Cloudy(
//                    modifier = Modifier.background(Color.Black.copy(alpha = 0.4f))
//                ) {
                    val url = TmdbUtils.getFullEpisodeStillPath(it)
                    val model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .diskCacheKey(url ?: "")
                        .networkCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build()
                    AsyncImage(
                        model = model,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .blur(radius = 10.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .blur(radius = 5.dp)
                    )
//                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                val textColor = episode.stillPath?.let { Color.White } ?: if (isSystemInDarkTheme()) Color.White else Color.Black
                Text(
                    text = "S${episode.seasonNumber}E${episode.episodeNumber} • ${episode.name}",
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TmdbUtils.convertEpisodeDate(episode.airDate)?.let {
                    Text(
                        text = it,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = textColor
                    )
                }
                Text(
                    text = episode.overview,
                    overflow = TextOverflow.Ellipsis,
                    color = textColor
                )
            }
        }
    }
}