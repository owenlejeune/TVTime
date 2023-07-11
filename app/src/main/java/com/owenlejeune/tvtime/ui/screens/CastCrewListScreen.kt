package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CrewMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MovieCastMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvCastMember
import com.owenlejeune.tvtime.ui.components.MediaResultCard
import com.owenlejeune.tvtime.ui.components.PillSegmentedControl
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastCrewListScreen(
    appNavController: NavController,
    type: MediaViewType,
    id: Int
) {
    val mainViewModel = viewModel<MainViewModel>()

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)

    val details = mainViewModel.produceDetailsFor(type)[id]

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
                title = { Text(text = details?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = { appNavController.popBackStack() }) {
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

            val castMap = remember { mainViewModel.produceCastFor(type) }
            val crewMap = remember { mainViewModel.produceCrewFor(type) }

            val cast = castMap[id]
            val crew = crewMap[id]

            var castSelected by remember { mutableStateOf(true) }
            val items = if (castSelected) cast else crew

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    val labels = listOf(stringResource(id = R.string.actor_label), stringResource(id = R.string.production_label))
                    PillSegmentedControl(
                        items = labels,
                        itemLabel = { _, t -> t },
                        onItemSelected = { i, _ -> castSelected = i == 0 },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                items(items!!) { item ->
                    val additionalDetails = emptyList<String>().toMutableList()
                    when (item) {
                        is MovieCastMember -> additionalDetails.add(stringResource(id = R.string.cast_character_template, item.character))
                        is TvCastMember -> {
                            item.roles.forEach { role ->
                                additionalDetails.add(stringResource(id = R.string.cast_tv_character_template, role.role, role.episodeCount))
                            }
                        }
                        is CrewMember -> additionalDetails.add(stringResource(id = R.string.crew_template, item.department))
                    }

                    MediaResultCard(
                        appNavController = appNavController,
                        mediaViewType = type,
                        id = item.id,
                        backdropPath = null,
                        posterPath = TmdbUtils.getFullPosterPath(item.profilePath),
                        title = item.name,
                        additionalDetails = additionalDetails,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }


}