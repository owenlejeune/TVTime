package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailCrew
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MovieCast
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvCast
import com.owenlejeune.tvtime.extensions.bringToFront
import com.owenlejeune.tvtime.extensions.getCalendarYear
import com.owenlejeune.tvtime.extensions.unlessEmpty
import com.owenlejeune.tvtime.ui.components.BackButton
import com.owenlejeune.tvtime.ui.components.MediaResultCard
import com.owenlejeune.tvtime.ui.components.PillSegmentedControl
import com.owenlejeune.tvtime.ui.components.TVTTopAppBar
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnownForScreen(
    appNavController: NavController,
    id: Int
) {
    val mainViewModel = viewModel<MainViewModel>()
    val applicationViewModel = viewModel<ApplicationViewModel>()

    LaunchedEffect(Unit) {
        mainViewModel.getCastAndCrew(id, MediaViewType.PERSON)
    }

    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    val peopleMap = remember { mainViewModel.peopleMap }
    val person = peopleMap[id]

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TVTTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(text = person?.name ?: "") },
                appNavController = appNavController,
                navigationIcon = {
                    BackButton(navController = appNavController)
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val castCreditsMap = remember { mainViewModel.peopleCastMap }
            val crewCreditsMap = remember { mainViewModel.peopleCrewMap }

            val castCredits = castCreditsMap[id]?.sortedByDescending { it.releaseDate }?.bringToFront { it.releaseDate == null } ?: emptyList()
            val crewCredits = crewCreditsMap[id]?.sortedByDescending { it.releaseDate }?.bringToFront { it.releaseDate == null } ?: emptyList()

            var actorSelected by remember { mutableStateOf(true) }
            val items = if (actorSelected) castCredits else crewCredits

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val labels = listOf(stringResource(id = R.string.actor_label), stringResource(id = R.string.production_label))
                        PillSegmentedControl(
                            items = labels,
                            itemLabel = { _, t -> t },
                            onItemSelected = { i, _ -> actorSelected = i == 0}
                        )
                    }
                }

                items(items) { item ->
                    val additionalDetails = emptyList<String>().toMutableList()
                    when (item) {
                        is MovieCast -> additionalDetails.add(stringResource(id = R.string.cast_character_template, item.character.unlessEmpty(stringResource(id = R.string.uncredited))))
                        is TvCast -> additionalDetails.add(stringResource(id = R.string.cast_tv_character_template, item.character.unlessEmpty(stringResource(id = R.string.uncredited)), item.episodeCount))
                        is DetailCrew -> additionalDetails.add(stringResource(id = R.string.crew_template, item.job))
                    }

                    val releaseYear = item.releaseDate?.getCalendarYear() ?: stringResource(id = R.string.tba)

                    MediaResultCard(
                        appNavController = appNavController,
                        mediaViewType = item.mediaType,
                        id = item.id,
                        backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath),
                        posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                        title = "${item.title} • $releaseYear",
                        additionalDetails = additionalDetails,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}