package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.DetailService
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.SearchService
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.main.MediaResultCard
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(
    appNavController: NavHostController,
    title: String,
    mediaViewType: MediaViewType
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        val searchValue = rememberSaveable { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }

        SmallTopAppBar(
            title = {
                TextField(
                    value = searchValue.value,
                    onValueChange = { searchValue.value = it },
                    placeholder = { Text(text = stringResource(id = R.string.search_placeholder, title)) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequester),
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

        val showLoadingAnimation = remember { mutableStateOf(false) }
        if (showLoadingAnimation.value) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.background
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.background,
                progress = 0f
            )
        }

        if (searchValue.value.isNotEmpty()) {
            when (mediaViewType) {
                MediaViewType.TV -> {
                    SearchResultListView(
                        showLoadingAnimation = showLoadingAnimation,
                        currentQuery = searchValue,
                        searchExecutor = { searchResults: MutableState<List<SearchResultTv>> ->
                            searchTv(searchValue.value, searchResults)
                        }
                    ) { tv ->
                        TvSearchResultView(result = tv, appNavController = appNavController)
                    }
                }
                MediaViewType.MOVIE -> {
                    SearchResultListView(
                        showLoadingAnimation = showLoadingAnimation,
                        currentQuery = searchValue,
                        searchExecutor = { searchResults: MutableState<List<SearchResultMovie>> ->
                            searchMovies(searchValue.value, searchResults)
                        }
                    ) { movie ->
                        MovieSearchResultView(result = movie, appNavController = appNavController)
                    }
                }
                MediaViewType.PERSON -> {
                    SearchResultListView(
                        showLoadingAnimation = showLoadingAnimation,
                        currentQuery = searchValue,
                        searchExecutor = { searchResults: MutableState<List<SearchResultPerson>> ->
                            searchPeople(searchValue.value, searchResults)
                        }
                    ) { person ->
                        PeopleSearchResultView(result = person, appNavController = appNavController)
                    }
                }
                MediaViewType.MIXED -> {
                    SearchResultListView(
                        showLoadingAnimation = showLoadingAnimation,
                        currentQuery = searchValue,
                        searchExecutor = { searchResults: MutableState<List<SortableSearchResult>> ->
                            searchMulti(searchValue.value, searchResults)
                        },
                    ) { item ->
                        when (item.mediaType) {
                            MediaViewType.MOVIE -> MovieSearchResultView(
                                appNavController = appNavController,
                                result = item as SearchResultMovie
                            )
                            MediaViewType.TV -> TvSearchResultView(
                                appNavController = appNavController,
                                result = item as SearchResultTv
                            )
                            MediaViewType.PERSON -> PeopleSearchResultView(
                                appNavController = appNavController,
                                result = item as SearchResultPerson
                            )
                            else -> {}
                        }
                    }
                }
                else -> {}
            }
        }

        LaunchedEffect(key1 = "") {
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun <T: SortableSearchResult> SearchResultListView(
    showLoadingAnimation: MutableState<Boolean>,
    currentQuery: MutableState<String>,
    searchExecutor: (MutableState<List<T>>) -> Unit,
    viewRenderer: @Composable (T) -> Unit
) {
    val searchResults = remember { mutableStateOf(emptyList<T>()) }

    LaunchedEffect(key1 = currentQuery.value) {
        showLoadingAnimation.value = true
        searchExecutor(searchResults)
        showLoadingAnimation.value = false
    }

    if (currentQuery.value.isNotEmpty() && searchResults.value.isEmpty()) {
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
    LazyColumn(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val items = searchResults.value.sortedByDescending { it.popularity }
        listItems(items) { item ->
            viewRenderer(item)
        }
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
        title = searchResult.name,
        additionalDetails = additionalDetails(searchResult)
    )
}

@Composable
private fun MovieSearchResultView(
    appNavController: NavHostController,
    result: SearchResultMovie
) {
    val cast = remember { mutableStateOf<List<CastMember>?>(null) }
    getCast(result.id, MoviesService(), cast)

    SearchResultItemView(
        appNavController = appNavController,
        mediaViewType = MediaViewType.MOVIE,
        searchResult = result,
        posterModel = { TmdbUtils.getFullPosterPath(result.posterPath) },
        backdropModel = { TmdbUtils.getFullBackdropPath(result.backdropPath) },
        additionalDetails = {
            listOf(
                TmdbUtils.releaseYearFromData(result.releaseDate),
                cast.value?.joinToString(separator = ", ") { it.name } ?: ""
            )
        }
    )
}

@Composable
private fun TvSearchResultView(
    appNavController: NavHostController,
    result: SearchResultTv
) {
    val context = LocalContext.current

    val cast = remember { mutableStateOf<List<CastMember>?>(null) }
    getCast(result.id, TvService(), cast)

    SearchResultItemView(
        appNavController = appNavController,
        mediaViewType = MediaViewType.TV,
        searchResult = result,
        posterModel = { TmdbUtils.getFullPosterPath(result.posterPath) },
        backdropModel = { TmdbUtils.getFullBackdropPath(result.backdropPath) },
        additionalDetails = {
            listOf(
                "${TmdbUtils.releaseYearFromData(result.releaseDate)}  ${context.getString(R.string.search_result_tv_series)}",
                cast.value?.joinToString(separator = ", ") { it.name } ?: ""
            )
        }
    )
}

@Composable
private fun PeopleSearchResultView(
    appNavController: NavHostController,
    result: SearchResultPerson
) {
    val mostKnownFor = result.knownFor.sortedBy { it.popularity }[0]

    SearchResultItemView(
        appNavController = appNavController,
        mediaViewType = MediaViewType.PERSON,
        searchResult = result,
        posterModel = { TmdbUtils.getFullPersonImagePath(result.profilePath) },
        backdropModel = { TmdbUtils.getFullBackdropPath(mostKnownFor.backdropPath) },
        additionalDetails = {
            listOf(
                "${mostKnownFor.title} (${TmdbUtils.releaseYearFromData(mostKnownFor.releaseDate)})"
            )
        }
    )
}

private fun searchMovies(
    query: String,
    searchResults: MutableState<List<SearchResultMovie>>
) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = SearchService().searchMovies(query)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                searchResults.value = response.body()?.results ?: emptyList()
            }
        }
    }
}

private fun searchTv(
    query: String,
    searchResults: MutableState<List<SearchResultTv>>
) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = SearchService().searchTv(query)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                searchResults.value = response.body()?.results ?: emptyList()
            }
        }
    }
}

private fun searchPeople(
    query: String,
    searchResults: MutableState<List<SearchResultPerson>>
) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = SearchService().searchPeople(query)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                searchResults.value = response.body()?.results ?: emptyList()
            }
        }
    }
}

private fun searchMulti(
    query: String,
    searchResults: MutableState<List<SortableSearchResult>>
) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = SearchService().searchMulti(query)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                searchResults.value = response.body()?.results ?: emptyList()
            }
        }
    }
}

private fun getCast(
    id: Int,
    detailService: DetailService,
    cast: MutableState<List<CastMember>?>
) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = detailService.getCastAndCrew(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                cast.value = response.body()?.cast?.let {
                    val end = minOf(2, it.size)
                    it.subList(0, end)
                }
            }
        }
    }
}