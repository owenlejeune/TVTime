package com.owenlejeune.tvtime.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Season
import com.owenlejeune.tvtime.extensions.combineWith
import com.owenlejeune.tvtime.ui.components.BackButton
import com.owenlejeune.tvtime.ui.components.EpisodeItem
import com.owenlejeune.tvtime.ui.components.TVTTopAppBar
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.types.MediaViewType

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
        val numSeasons = mainViewModel.detailedTv[id]?.numberOfSeasons ?: 0
        for (i in 0..numSeasons) {
            mainViewModel.getSeason(id, i, true)
        }
    }

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TVTTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { },
                appNavController = appNavController,
                navigationIcon = {
                    BackButton(navController = appNavController)
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
                val seasons = seasonsMap[id] ?: emptySet()

                seasons.sortedBy { it.seasonNumber }.forEachIndexed { index, season ->
                    SeasonSection(
                        appNavController = appNavController,
                        seriesId = id,
                        season = season,
                        expandedByDefault = index == 1
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun SeasonSection(
    appNavController: NavController,
    seriesId: Int,
    season: Season,
    expandedByDefault: Boolean
) {
    var isExpanded by remember { mutableStateOf(expandedByDefault) }

    Row(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    val combinedId = seriesId.combineWith(season.seasonNumber)
                    appNavController.navigate(AppNavItem.DetailView.withArgs(type = MediaViewType.SEASON, id = combinedId))
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