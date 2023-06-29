package com.owenlejeune.tvtime.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.extensions.getCalendarYear
import com.owenlejeune.tvtime.extensions.lazyPagingItems
import com.owenlejeune.tvtime.ui.components.MediaResultCard
import com.owenlejeune.tvtime.ui.components.SelectableTextChip
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.ui.viewmodel.SearchViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    appNavController: NavHostController,
    title: String,
    mediaViewType: MediaViewType
) {
    val searchViewModel = viewModel<SearchViewModel>()

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

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

        TopAppBar(
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
                            IconButton(onClick = { searchValue.value = "" }) {
                                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear search query")
                            }
                        }
                    }
                )
            },
            navigationIcon = {
                IconButton(onClick = { appNavController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.content_description_back_button)
                    )
                }
            }
        )
        Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.surfaceVariant)

        Row(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SelectableTextChip(
                selected = viewType.value == MediaViewType.MOVIE,
                onSelected = { viewType.value = MediaViewType.MOVIE },
                text = stringResource(id = R.string.nav_movies_title)
            )
            SelectableTextChip(
                selected = viewType.value == MediaViewType.TV,
                onSelected = { viewType.value = MediaViewType.TV },
                text = stringResource(id = R.string.nav_tv_title)
            )
            SelectableTextChip(
                selected = viewType.value == MediaViewType.PERSON,
                onSelected = { viewType.value = MediaViewType.PERSON },
                text = stringResource(id = R.string.nav_people_title)
            )
            SelectableTextChip(
                selected = viewType.value == MediaViewType.MIXED,
                onSelected = { viewType.value = MediaViewType.MIXED },
                text = stringResource(id = R.string.search_multi_title)
            )
        }

        when (viewType.value) {
            MediaViewType.TV -> {
                TvResultsView(appNavController = appNavController, searchViewModel = searchViewModel)
            }
            MediaViewType.MOVIE -> {
                MovieResultsView(appNavController = appNavController, searchViewModel = searchViewModel)
            }
            MediaViewType.PERSON -> {
                PeopleResultsView(appNavController = appNavController, searchViewModel = searchViewModel)
            }
            MediaViewType.MIXED -> {
                MultiResultsView(appNavController = appNavController, searchViewModel = searchViewModel)
            }
            else -> {}
        }

        LaunchedEffect(key1 = "") {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun MovieResultsView(
    appNavController: NavHostController,
    searchViewModel: SearchViewModel
) {
    val context = LocalContext.current

    val results = remember { searchViewModel.movieResults }
    results.value?.let {
        val pagingItems = it.collectAsLazyPagingItems()
        if (pagingItems.itemCount > 0) {
            LazyColumn(
                modifier = Modifier.padding(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                handleLoadState(context, pagingItems.loadState.refresh)
                lazyPagingItems(
                    lazyPagingItems = pagingItems,
                    key = { i -> pagingItems[i]!!.id }
                ) { item ->
                    item?.let {
                        MovieSearchResultView(
                            appNavController = appNavController,
                            result = item
                        )
                    }
                }
                handleLoadState(context, pagingItems.loadState.append)
            }
        } else {
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
}

@Composable
private fun TvResultsView(
    appNavController: NavHostController,
    searchViewModel: SearchViewModel
) {
    val context = LocalContext.current

    val results = remember { searchViewModel.tvResults }
    results.value?.let {
        val pagingItems = it.collectAsLazyPagingItems()
        if (pagingItems.itemCount > 0) {
            LazyColumn(
                modifier = Modifier.padding(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                handleLoadState(context, pagingItems.loadState.refresh)
                lazyPagingItems(
                    lazyPagingItems = pagingItems,
                    key = { i -> pagingItems[i]!!.id }
                ) { item ->
                    item?.let {
                        TvSearchResultView(
                            appNavController = appNavController,
                            result = item
                        )
                    }
                }
                handleLoadState(context, pagingItems.loadState.append)
            }
        } else {
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
}

@Composable
private fun PeopleResultsView(
    appNavController: NavHostController,
    searchViewModel: SearchViewModel
) {
    val context = LocalContext.current

    val results = remember { searchViewModel.peopleResults }
    results.value?.let {
        val pagingItems = it.collectAsLazyPagingItems()
        if (pagingItems.itemCount > 0) {
            LazyColumn(
                modifier = Modifier.padding(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                handleLoadState(context, pagingItems.loadState.refresh)
                lazyPagingItems(
                    lazyPagingItems = pagingItems,
                    key = { i -> pagingItems[i]!!.id }
                ) { item ->
                    item?.let {
                        PeopleSearchResultView(
                            appNavController = appNavController,
                            result = item
                        )
                    }
                }
                handleLoadState(context, pagingItems.loadState.append)
            }
        } else {
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
}

@Composable
private fun MultiResultsView(
    appNavController: NavHostController,
    searchViewModel: SearchViewModel
) {
    val context = LocalContext.current

    val results = remember { searchViewModel.multiResults }
    results.value?.let {
        val pagingItems = it.collectAsLazyPagingItems()
        if (pagingItems.itemCount > 0) {
            LazyColumn(
                modifier = Modifier.padding(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                handleLoadState(context, pagingItems.loadState.refresh)
                lazyPagingItems(
                    lazyPagingItems = pagingItems,
                    key = { i -> pagingItems[i]!!.id }
                ) { item ->
                    item?.let {
                        when (item.mediaType) {
                            MediaViewType.MOVIE -> {
                                MovieSearchResultView(
                                    appNavController = appNavController,
                                    result = item as SearchResultMovie
                                )
                            }
                            MediaViewType.TV -> {
                                TvSearchResultView(
                                    appNavController = appNavController,
                                    result = item as SearchResultTv
                                )
                            }
                            MediaViewType.PERSON -> {
                                PeopleSearchResultView(
                                    appNavController = appNavController,
                                    result = item as SearchResultPerson
                                )
                            }
                            else ->{}
                        }
                    }
                }
                handleLoadState(context, pagingItems.loadState.append)
            }
        } else {
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
    additionalDetails: (T) -> List<String> = { emptyList() }
) {
    MediaResultCard(
        appNavController = appNavController,
        mediaViewType = mediaViewType,
        id = searchResult.id,
        backdropPath = backdropModel(searchResult),
        posterPath = posterModel(searchResult),
        title = searchResult.title,
        additionalDetails = additionalDetails(searchResult)
    )
}

@Composable
private fun MovieSearchResultView(
    appNavController: NavHostController,
    result: SearchResultMovie,
    service: MoviesService = get(MoviesService::class.java)
) {
    LaunchedEffect(Unit) {
        service.getCastAndCrew(result.id)
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
        }
    )
}

@Composable
private fun TvSearchResultView(
    appNavController: NavHostController,
    result: SearchResultTv,
    service: TvService = get(TvService::class.java)
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        service.getCastAndCrew(result.id)
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
        }
    )
}

@Composable
private fun PeopleSearchResultView(
    appNavController: NavHostController,
    result: SearchResultPerson
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
        additionalDetails = { additional }
    )
}