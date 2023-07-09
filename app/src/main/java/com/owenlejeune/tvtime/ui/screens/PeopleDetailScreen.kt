package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailPerson
import com.owenlejeune.tvtime.ui.components.ContentCard
import com.owenlejeune.tvtime.ui.components.DetailHeader
import com.owenlejeune.tvtime.ui.components.ExpandableContentCard
import com.owenlejeune.tvtime.ui.components.ExternalIdsArea
import com.owenlejeune.tvtime.ui.components.PosterItem
import com.owenlejeune.tvtime.ui.components.TwoLineImageTextCard
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.lang.Integer.min

private const val TAG = "PeopleDetailScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun PersonDetailScreen(
    appNavController: NavController,
    personId: Int
) {
    val mainViewModel = viewModel<MainViewModel>()
    LaunchedEffect(Unit) {
        mainViewModel.getById(personId, MediaViewType.PERSON)
        mainViewModel.getExternalIds(personId, MediaViewType.PERSON)
        mainViewModel.getCastAndCrew(personId, MediaViewType.PERSON)
        mainViewModel.getImages(personId, MediaViewType.PERSON)
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    val peopleMap = remember { mainViewModel.peopleMap }
    val person = peopleMap[personId]

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
                title = { Text(text = person?.name ?: "") },
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
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .verticalScroll(state = rememberScrollState())
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailHeader(
                    posterUrl = TmdbUtils.getFullPersonImagePath(person?.profilePath),
                    posterContentDescription = person?.profilePath,
                    elevation = 0.dp
                )

                val externalIdsMap = remember { mainViewModel.peopleExternalIdsMap }
                val externalIds = externalIdsMap[personId]
                externalIds?.let {
                    ExternalIdsArea(
                        externalIds = it,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                BiographyCard(person = person)

                CreditsCard(personId = personId, appNavController = appNavController)
                
                ImagesCard(id = personId, appNavController = appNavController)
            }
        }
    }
}

@Composable
private fun BiographyCard(person: DetailPerson?) {
    if (person != null && person.biography.isNotEmpty()) {
        ExpandableContentCard(
            expandOnTouchAnywhere = true
        ) { isExpanded ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                text = person.biography,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CreditsCard(
    personId: Int,
    appNavController: NavController
) {
    val mainViewModel = viewModel<MainViewModel>()
    
    val creditsMap = remember { mainViewModel.peopleCastMap }
    val credits = creditsMap[personId] ?: emptyList()

    ContentCard(
        title = stringResource(R.string.known_for_label)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
            items(min(credits.size, 15)) { i ->
                val content = credits[i]

                TwoLineImageTextCard(
                    title = content.title,
                    titleTextColor = MaterialTheme.colorScheme.primary,
                    subtitle = content.character,
                    modifier = Modifier
                        .width(124.dp)
                        .wrapContentHeight(),
                    imageUrl = TmdbUtils.getFullPosterPath(content.posterPath),
                    onItemClicked = {
                        appNavController.navigate(
                            AppNavItem.DetailView.withArgs(content.mediaType, content.id)
                        )
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Text(
            text = stringResource(id = R.string.expand_see_all),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp)
                .clickable {
                    appNavController.navigate(AppNavItem.KnownForView.withArgs(personId))
                }
        )
    }
}

@Composable
private fun ImagesCard(
    id: Int,
    appNavController: NavController
) {
    val mainViewModel = viewModel<MainViewModel>()
    val imagesMap = remember { mainViewModel.peopleImagesMap }
    val images = imagesMap[id] ?: emptyList()

    ContentCard(
        title = stringResource(R.string.images_title)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
            items(images) { image ->
                PosterItem(
                    width = 120.dp,
                    url = TmdbUtils.getFullPersonImagePath(image.filePath),
                    placeholder = Icons.Filled.Person,
                    title = ""
                )
            }
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Text(
            text = stringResource(id = R.string.expand_see_all),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp)
                .clickable {
                    appNavController.navigate(AppNavItem.GalleryView.withArgs(MediaViewType.PERSON, id))
                }
        )
    }
}