package com.owenlejeune.tvtime.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailPerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedTv
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Image
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.extensions.DateFormat
import com.owenlejeune.tvtime.extensions.combinedOnVisibilityChange
import com.owenlejeune.tvtime.extensions.format
import com.owenlejeune.tvtime.extensions.yearsSince
import com.owenlejeune.tvtime.ui.components.AdditionalDetailItem
import com.owenlejeune.tvtime.ui.components.BackButton
import com.owenlejeune.tvtime.ui.components.ContentCard
import com.owenlejeune.tvtime.ui.components.DetailHeader
import com.owenlejeune.tvtime.ui.components.ExpandableContentCard
import com.owenlejeune.tvtime.ui.components.ExternalIdsArea
import com.owenlejeune.tvtime.ui.components.PosterItem
import com.owenlejeune.tvtime.ui.components.TVTTopAppBar
import com.owenlejeune.tvtime.ui.components.TwoLineImageTextCard
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.theme.Typography
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Integer.min

private const val TAG = "PeopleDetailScreen"

private fun fetchData(
    mainViewModel: MainViewModel,
    id: Int,
    force: Boolean = false
) {
    val scope = CoroutineScope(Dispatchers.IO)
    scope.launch { mainViewModel.getById(id, MediaViewType.PERSON, force) }
    scope.launch { mainViewModel.getExternalIds(id, MediaViewType.PERSON, force) }
    scope.launch { mainViewModel.getCastAndCrew(id, MediaViewType.PERSON, force) }
    scope.launch { mainViewModel.getImages(id, MediaViewType.PERSON, force) }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun PersonDetailScreen(
    appNavController: NavController,
    personId: Int
) {
    val mainViewModel = viewModel<MainViewModel>()
    LaunchedEffect(Unit) {
        fetchData(mainViewModel, personId)
    }

    val applicationViewModel = viewModel<ApplicationViewModel>()
    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    val peopleMap = remember { mainViewModel.peopleMap }
    val person = peopleMap[personId]

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)

    val isRefreshing = remember { mutableStateOf(false) }
    mainViewModel.monitorDetailsLoadingRefreshing(refreshing = isRefreshing)
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = {
            fetchData(mainViewModel, personId, true)
        }
    )

    val titleViewHidden = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TVTTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    AnimatedVisibility(
                        visible = titleViewHidden.value,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(text = person?.name ?: "")
                    }
                },
                appNavController = appNavController,
                navigationIcon = {
                    BackButton(navController = appNavController)
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .pullRefresh(state = pullRefreshState)
        ) {
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .verticalScroll(state = rememberScrollState())
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val creditsMap = remember { mainViewModel.peopleCastMap }
                val credits =
                    creditsMap[personId]?.map { Image(it.backdropPath ?: "", 0, 0) } ?: emptyList()
                val imageCollection = ImageCollection(backdrops = credits, posters = emptyList())
                DetailHeader(
                    posterUrl = TmdbUtils.getFullPersonImagePath(person?.profilePath),
                    posterContentDescription = person?.profilePath,
                    imageCollection = imageCollection,
                    elevation = 0.dp
                )

                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = person?.name ?: "",
                        color = MaterialTheme.colorScheme.secondary,
                        style = Typography.headlineLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedOnVisibilityChange(
                                onVisible = { titleViewHidden.value = false },
                                onNotVisible = { titleViewHidden.value = true }
                            )
                    )

                    ExternalIdsArea(
                        modifier = Modifier.padding(start = 4.dp),
                        type = MediaViewType.PERSON,
                        itemId = personId
                    )

                    BiographyCard(person = person)

                    CreditsCard(personId = personId, appNavController = appNavController)

                    AdditionalDetailsCard(id = personId, mainViewModel = mainViewModel)

                    ImagesCard(id = personId, appNavController = appNavController)
                }
            }
            
            PullRefreshIndicator(
                refreshing = isRefreshing.value,
                state = pullRefreshState,
                modifier = Modifier.align(alignment = Alignment.TopCenter)
            )
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
private fun AdditionalDetailsCard(
    id: Int,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val personMap = remember { mainViewModel.peopleMap }
    val person = personMap[id]
    
    ContentCard(
        modifier = modifier,
        title = stringResource(R.string.additional_details_title)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdditionalDetailItem(
                title = stringResource(R.string.also_known_as),
                subtext = person?.alsoKnownAs?.joinToString(separator = ", ") ?: ""
            )
            AdditionalDetailItem(
                title = stringResource(R.string.place_of_birth),
                subtext = person?.birthplace ?: ""
            )
            val birthday = person?.birthday
            AdditionalDetailItem(
                title = stringResource(R.string.birthday),
                subtext = "${birthday?.format(DateFormat.MMMM_dd_yyyy) ?: ""} (${birthday?.yearsSince(person.dateOfDeath)} years old)"
            )
            person?.dateOfDeath?.let {
                AdditionalDetailItem(
                    title = stringResource(R.string.date_of_death),
                    subtext = it.format(DateFormat.MMMM_dd_yyyy)
                )
            }
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
    val sortedCredits = credits.sortedByDescending { it.popularity }

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
            items(min(sortedCredits.size, 15)) { i ->
                val content = sortedCredits[i]

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
                    appNavController.navigate(
                        AppNavItem.GalleryView.withArgs(
                            MediaViewType.PERSON,
                            id
                        )
                    )
                }
        )
    }
}