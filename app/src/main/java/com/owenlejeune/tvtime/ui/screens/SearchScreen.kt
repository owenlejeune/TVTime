@file:Suppress("UNCHECKED_CAST")

package com.owenlejeune.tvtime.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.extensions.getCalendarYear
import com.owenlejeune.tvtime.extensions.lazyPagingItems
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.BackButton
import com.owenlejeune.tvtime.ui.components.MediaResultCard
import com.owenlejeune.tvtime.ui.components.PillSegmentedControl
import com.owenlejeune.tvtime.ui.components.TVTTopAppBar
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.ui.viewmodel.SearchViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.flow.Flow
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    appNavController: NavHostController,
    title: String,
    mediaViewType: MediaViewType,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val searchViewModel = viewModel<SearchViewModel>()
    val applicationViewModel = viewModel<ApplicationViewModel>()

    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    val viewType = remember { mutableStateOf(mediaViewType) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        val searchValue = rememberSaveable { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(searchValue.value, viewType.value) {
            if (searchValue.value.isEmpty()) {
                searchViewModel.resetResults()
            } else {
                searchViewModel.searchFor(searchValue.value, viewType.value)
            }
        }

        TVTTopAppBar(
            appNavController = appNavController,
            title = {
                TextField(
                    value = searchValue.value,
                    onValueChange = { searchValue.value = it },
                    placeholder = { Text(text = stringResource(id = R.string.search_placeholder, title)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (searchValue.value.isNotEmpty()) {
                            IconButton(
                                onClick = { searchValue.value = "" }
                            ) {
                                Icon(imageVector = Icons.Filled.Clear, contentDescription = stringResource(R.string.clear_search_query)
                                )
                            }
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onSearch = { storeSearchValue(searchValue.value, preferences) }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    )
                )
            },
            navigationIcon = {
                BackButton(navController = appNavController)
            }
        )
        Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.surfaceVariant)

        val searchTypes = listOf(MediaViewType.MOVIE, MediaViewType.TV, MediaViewType.PERSON, MediaViewType.MIXED)
        val selected = remember {
            mutableStateOf(
                if (preferences.multiSearch) {
                    MediaViewType.MIXED
                } else {
                    mediaViewType
                }
            )
        }

        val context = LocalContext.current
        PillSegmentedControl(
            items = searchTypes,
            itemLabel = { _, i ->
                when (i) {
                    MediaViewType.MOVIE -> context.getString(R.string.nav_movies_title)
                    MediaViewType.TV -> context.getString(R.string.nav_tv_title)
                    MediaViewType.PERSON -> context.getString(R.string.nav_people_title)
                    MediaViewType.MIXED -> context.getString(R.string.search_multi_title)
                    else -> ""
                }
            },
            onItemSelected = { _, i ->
                selected.value = i
            },
            defaultSelectedItemIndex = searchTypes.indexOf(selected.value),
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val results = remember { searchViewModel.produceSearchResultsFor(viewType.value) }
        results.value?.let {
            val pagingItems = (results.value as Flow<PagingData<SortableSearchResult>>).collectAsLazyPagingItems()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                handleLoadState(context, pagingItems.loadState.refresh)
                item {
                    if (pagingItems.itemCount == 0) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = stringResource(R.string.no_search_results),
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                lazyPagingItems(
                    lazyPagingItems = pagingItems,
                    key = { i -> pagingItems[i]?.id ?: -1 }
                ) { item ->
                    when (item?.mediaType) {
                        MediaViewType.MOVIE -> {
                            MovieSearchResultView(
                                appNavController = appNavController,
                                result = item as SearchResultMovie,
                                additionalOnClick = { storeSearchValue(searchValue.value, preferences) }
                            )
                        }
                        MediaViewType.TV -> {
                            TvSearchResultView(
                                appNavController = appNavController,
                                result = item as SearchResultTv,
                                additionalOnClick = { storeSearchValue(searchValue.value, preferences) }
                            )
                        }
                        MediaViewType.PERSON -> {
                            PeopleSearchResultView(
                                appNavController = appNavController,
                                result = item as SearchResultPerson,
                                additionalOnClick = { storeSearchValue(searchValue.value, preferences) }
                            )
                        }
                        else -> {}
                    }
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
                handleLoadState(context, pagingItems.loadState.append)
            }
        } ?: run {
            preferences.recentSearches.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .clickable {
                            searchValue.value = it
                        },
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Restore,
                        contentDescription = null
                    )
                    Text(
                        text = it,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        LaunchedEffect(key1 = "") {
            focusRequester.requestFocus()
        }
    }
}

private fun LazyListScope.handleLoadState(context: Context, state: LoadState) {
    when (state) {
        is LoadState.Loading -> {
            item {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    trackColor = MaterialTheme.colorScheme.background
                )
            }
        }
        is LoadState.Error -> {
            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
        }
        else -> {}
    }
}

@Composable
private fun <T: SortableSearchResult> SearchResultItemView(
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    searchResult: T,
    posterModel: (T) -> Any?,
    backdropModel: (T) -> Any?,
    additionalDetails: (T) -> List<String> = { emptyList() },
    additionalOnClick: () -> Unit = {}
) {
    MediaResultCard(
        appNavController = appNavController,
        mediaViewType = mediaViewType,
        id = searchResult.id,
        backdropPath = backdropModel(searchResult),
        posterPath = posterModel(searchResult),
        title = searchResult.title,
        additionalDetails = additionalDetails(searchResult),
        additionalOnClick = additionalOnClick
    )
}

@Composable
private fun MovieSearchResultView(
    appNavController: NavHostController,
    result: SearchResultMovie,
    additionalOnClick: () -> Unit = {},
    service: MoviesService = get(MoviesService::class.java)
) {
    LaunchedEffect(Unit) {
        service.getCastAndCrew(result.id, false)
    }
    val mainViewModel = viewModel<MainViewModel>()
    val castMap = remember { mainViewModel.movieCast }
    val cast = castMap[result.id]

    SearchResultItemView(
        appNavController = appNavController,
        mediaViewType = MediaViewType.MOVIE,
        searchResult = result,
        posterModel = { TmdbUtils.getFullPosterPath(result.posterPath) },
        backdropModel = { TmdbUtils.getFullBackdropPath(result.backdropPath) },
        additionalDetails = {
            listOf(
                result.releaseDate?.getCalendarYear()?.toString() ?: "",
                cast?.joinToString(separator = ", ") { it.name } ?: ""
            )
        },
        additionalOnClick = additionalOnClick
    )
}

@Composable
private fun TvSearchResultView(
    appNavController: NavHostController,
    result: SearchResultTv,
    additionalOnClick: () -> Unit = {},
    service: TvService = get(TvService::class.java)
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        service.getCastAndCrew(result.id, false)
    }
    val mainViewModel = viewModel<MainViewModel>()
    val castMap = remember { mainViewModel.tvCast }
    val cast = castMap[result.id]

    SearchResultItemView(
        appNavController = appNavController,
        mediaViewType = MediaViewType.TV,
        searchResult = result,
        posterModel = { TmdbUtils.getFullPosterPath(result.posterPath) },
        backdropModel = { TmdbUtils.getFullBackdropPath(result.backdropPath) },
        additionalDetails = {
            listOf(
                "${result.releaseDate?.getCalendarYear() ?: ""}  ${context.getString(R.string.search_result_tv_series)}",
                cast?.joinToString(separator = ", ") { it.name } ?: ""
            )
        },
        additionalOnClick = additionalOnClick
    )
}

@Composable
private fun PeopleSearchResultView(
    appNavController: NavHostController,
    result: SearchResultPerson,
    additionalOnClick: () -> Unit = {}
) {
    val mostKnownFor = result.knownFor.sortedBy { it.popularity }.takeUnless { it.isEmpty() }?.get(0)

    val additional = mostKnownFor?.let {
        listOf(
            "${mostKnownFor.title} (${mostKnownFor.releaseDate?.getCalendarYear()})"
        )
    } ?: emptyList()

    SearchResultItemView(
        appNavController = appNavController,
        mediaViewType = MediaViewType.PERSON,
        searchResult = result,
        posterModel = { TmdbUtils.getFullPersonImagePath(result.posterPath) },
        backdropModel = { TmdbUtils.getFullBackdropPath(mostKnownFor?.backdropPath) },
        additionalDetails = { additional },
        additionalOnClick = additionalOnClick
    )
}

private fun storeSearchValue(search: String, preferences: AppPreferences) {
    val recentSearches = preferences.recentSearches
    recentSearches.add(search)
    while (recentSearches.size > 5) {
        recentSearches.removeAt(0)
    }
    preferences.recentSearches = recentSearches
}