package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.DetailService
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.api.tmdb.PeopleService
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.api.tmdb.model.*
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.ui.components.*
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DetailView(
    appNavController: NavController,
    itemId: Int?,
    type: MediaViewType
) {
    val service = when(type) {
        MediaViewType.MOVIE -> MoviesService()
        MediaViewType.TV -> TvService()
        else -> throw IllegalArgumentException("Media type given: ${type}, \n     expected one of MediaViewType.MOVIE, MediaViewType.TV") // shouldn't happen
    }

    val mediaItem = remember { mutableStateOf<DetailedItem?>(null) }
    itemId?.let {
        if (mediaItem.value == null) {
            fetchMediaItem(itemId, service, mediaItem)
        }
    }

    val scrollState = rememberScrollState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(state = scrollState)
    ) {
        val (
            backButton, backdropImage, posterImage, titleText, contentColumn
        ) = createRefs()

        Backdrop(
            modifier = Modifier.constrainAs(backdropImage) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            mediaItem = mediaItem
        )

        PosterItem(
            mediaItem = mediaItem.value,
            modifier = Modifier
                .constrainAs(posterImage) {
                    bottom.linkTo(backdropImage.bottom)
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(backButton.bottom)
                }
        )

        TitleText(
            modifier = Modifier.constrainAs(titleText) {
                bottom.linkTo(posterImage.bottom)
                start.linkTo(posterImage.end, margin = 8.dp)
                end.linkTo(parent.end, margin = 16.dp)
            },
            title = mediaItem.value?.title ?: "",
        )

        BackButton(
            modifier = Modifier.constrainAs(backButton) {
                top.linkTo(parent.top)//, 8.dp)
                start.linkTo(parent.start, 12.dp)
                bottom.linkTo(posterImage.top)
            },
            appNavController = appNavController
        )

        ContentColumn(
            modifier = Modifier.constrainAs(contentColumn) {
               top.linkTo(backdropImage.bottom)//, margin = 8.dp)
            },
            itemId = itemId,
            mediaItem = mediaItem,
            service = service,
            mediaType = type,
            appNavController = appNavController
        )
    }
}

@Composable
fun PersonDetailView(
    appNavController: NavController,
    personId: Int?
) {
    val person = remember { mutableStateOf<DetailPerson?>(null) }
    personId?.let {
        if (person.value == null) {
            fetchPerson(personId, person)
        }
    }

    val scrollState = rememberScrollState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(state = scrollState)
    ) {
        val (
            backButton, backdropImage, profileImage, nameText, contentColumn
        ) = createRefs()

        BackdropImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .constrainAs(backdropImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        PosterItem(
            person = person.value,
            modifier = Modifier
                .constrainAs(profileImage) {
                    bottom.linkTo(backdropImage.bottom)
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(backButton.bottom)
                }
        )

        TitleText(
            modifier = Modifier.constrainAs(nameText) {
                bottom.linkTo(profileImage.bottom)
                start.linkTo(profileImage.end, margin = 8.dp)
                end.linkTo(parent.end, margin = 16.dp)
            },
            title = person.value?.name ?: ""
        )

        BackButton(
            modifier = Modifier.constrainAs(backButton) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, 12.dp)
                bottom.linkTo(profileImage.top)
            },
            appNavController = appNavController
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .constrainAs(contentColumn) {
                    top.linkTo(backdropImage.bottom)//, margin = 8.dp)
                },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExpandableContentCard { isExpanded ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                    text = person.value?.biography ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val credits = remember { mutableStateOf<PersonCreditsResponse?>(null) }
            personId?.let {
                if (credits.value == null) {
                    fetchCredits(personId, credits)
                }
            }

            ContentCard(title = stringResource(R.string.known_for_label)) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(credits.value?.cast?.size ?: 0) { i ->
                        val content = credits.value!!.cast[i]

                        val title = if (content.mediaType == MediaViewType.MOVIE) {
                            content.title ?: ""
                        } else {
                            content.name ?: ""
                        }
                        TwoLineImageTextCard(
                            title = title,
                            subtitle = content.character,
                            modifier = Modifier
                                .width(124.dp)
                                .wrapContentHeight(),
                            imageUrl = TmdbUtils.getFullPosterPath(content.posterPath),
                            onItemClicked = {
                                personId?.let {
                                    appNavController.navigate(
                                        "${MainNavItem.DetailView.route}/${content.mediaType}/${content.id}"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            ContentCard(title = stringResource(R.string.also_known_for_label)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val departments = credits.value?.crew?.map { it.department }?.toSet() ?: emptySet()
                    if (departments.isNotEmpty()) {
                        departments.forEach { department ->
                            Text(text = department, color = MaterialTheme.colorScheme.onSurface)
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val jobsInDepartment = credits.value!!.crew.filter { it.department == department }
                                items(jobsInDepartment.size) { i ->
                                    val content = jobsInDepartment[i]
                                    val title = if (content.mediaType == MediaViewType.MOVIE) {
                                        content.title ?: ""
                                    } else {
                                        content.name ?: ""
                                    }
                                    TwoLineImageTextCard(
                                        title = title,
                                        subtitle = content.job,
                                        modifier = Modifier
                                            .width(124.dp)
                                            .wrapContentHeight(),
                                        imageUrl = TmdbUtils.getFullPosterPath(content.posterPath),
                                        onItemClicked = {
                                            personId?.let {
                                                appNavController.navigate(
                                                    "${MainNavItem.DetailView.route}/${content.mediaType}/${content.id}"
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Backdrop(modifier: Modifier, mediaItem: MutableState<DetailedItem?>) {
//        val images = remember { mutableStateOf<ImageCollection?>(null) }
//        itemId?.let {
//            if (images.value == null) {
//                fetchImages(itemId, service, images)
//            }
//        }
    BackdropImage(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        imageUrl = TmdbUtils.getFullBackdropPath(mediaItem.value),
//            collection = images.value
    )
}

@Composable
private fun TitleText(modifier: Modifier, title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(.6f),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Start,
        softWrap = true,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun BackButton(modifier: Modifier, appNavController: NavController) {
    val start = if (isSystemInDarkTheme()) Color.Black else Color.White
    IconButton(
        onClick = { appNavController.popBackStack() },
        modifier = modifier
            .background(
                brush = Brush.radialGradient(colors = listOf(start, Color.Transparent))
            )
            .wrapContentSize()
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.content_description_back_button),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ContentColumn(
    modifier: Modifier,
    itemId: Int?,
    mediaItem: MutableState<DetailedItem?>,
    service: DetailService,
    mediaType: MediaViewType,
    appNavController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (mediaType == MediaViewType.MOVIE) {
            MiscMovieDetails(mediaItem = mediaItem, service as MoviesService)
        } else {
            MiscTvDetails(mediaItem = mediaItem, service as TvService)
        }

        if (mediaItem.value?.overview?.isNotEmpty() == true) {
            OverviewCard(mediaItem = mediaItem)
        }

        CastCard(itemId = itemId, service = service, appNavController = appNavController)

        SimilarContentCard(itemId = itemId, service = service, mediaType = mediaType, appNavController = appNavController)
        
        VideosCard(itemId = itemId, service = service)
    }
}

@Composable
private fun MiscTvDetails(mediaItem: MutableState<DetailedItem?>, service: TvService) {
    mediaItem.value?.let { tv ->
        val series = tv as DetailedTv

        val contentRating = remember { mutableStateOf("") }
        fetchTvContentRating(series.id, service, contentRating)

        MiscDetails(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            year = TmdbUtils.getSeriesRun(series),
            runtime = TmdbUtils.convertRuntimeToHoursMinutes(series),
            genres = series.genres,
            contentRating = contentRating
        )
    }
}

@Composable
private fun MiscMovieDetails(mediaItem: MutableState<DetailedItem?>, service: MoviesService) {
    mediaItem.value?.let { mi ->
        val movie = mi as DetailedMovie

        val contentRating = remember { mutableStateOf("") }
        fetchMovieContentRating(movie.id, service, contentRating)

        MiscDetails(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            year = TmdbUtils.getMovieReleaseYear(movie),
            runtime = TmdbUtils.convertRuntimeToHoursMinutes(movie),
            genres = movie.genres,
            contentRating = contentRating
        )
    }
}

@Composable
private fun MiscDetails(
    modifier: Modifier = Modifier,
    year: String,
    runtime: String,
    genres: List<Genre>,
    contentRating: MutableState<String>
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 8.dp)
        ) {
            Text(text = year, color = MaterialTheme.colorScheme.onBackground)
            Text(
                text = runtime,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 12.dp)
            )
            Text(
                text = contentRating.value,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        ChipGroup(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            chips = genres.map { it.name }
        )
    }
}

@Composable
private fun OverviewCard(mediaItem: MutableState<DetailedItem?>, modifier: Modifier = Modifier) {
    ContentCard(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            text = mediaItem.value?.overview ?: "",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CastCard(itemId: Int?, service: DetailService, appNavController: NavController, modifier: Modifier = Modifier) {
    val castAndCrew = remember { mutableStateOf<CastAndCrew?>(null) }
    itemId?.let {
        if (castAndCrew.value == null) {
            fetchCastAndCrew(itemId, service, castAndCrew)
        }
    }

    ContentCard(
        modifier = modifier,
        title = stringResource(R.string.cast_label),
        backgroundColor = MaterialTheme.colorScheme.primary,
        textColor = MaterialTheme.colorScheme.background
    ) {
        LazyRow(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(castAndCrew.value?.cast?.size ?: 0) { i ->
                val castMember = castAndCrew.value!!.cast[i]
                CastCrewCard(appNavController = appNavController, person = castMember)
            }
        }
    }
}

@Composable
private fun CastCrewCard(appNavController: NavController, person: Person) {
    TwoLineImageTextCard(
        title = person.name,
        modifier = Modifier
            .width(124.dp)
            .wrapContentHeight(),
        subtitle = when (person) {
            is CastMember -> person.character
            is CrewMember -> person.job
            else -> null
        },
        imageUrl = TmdbUtils.getFullPersonImagePath(person),
        noDataImage = R.drawable.no_person_photo,
        titleTextColor = MaterialTheme.colorScheme.onPrimary,
        subtitleTextColor = Color.Unspecified,
        onItemClicked = {
            appNavController.navigate(
                "${MainNavItem.DetailView.route}/${MediaViewType.PERSON}/${person.id}"
            )
        }
    )
}

@Composable
fun SimilarContentCard(
    itemId: Int?,
    service: DetailService,
    mediaType: MediaViewType,
    appNavController: NavController,
    modifier: Modifier = Modifier
) {
    val similarContent = remember { mutableStateOf<HomePageResponse?>(null) }
    itemId?.let {
        if (similarContent.value == null) {
            fetchSimilarContent(itemId, service, similarContent)
        }
    }

    ContentCard(
        modifier = modifier,
        title = stringResource(id = R.string.recommended_label)
    ) {
        LazyRow(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(similarContent.value?.results?.size ?: 0) { i ->
                val content = similarContent.value!!.results[i]

                TwoLineImageTextCard(
                    title = content.title,
                    modifier = Modifier
                        .width(124.dp)
                        .wrapContentHeight(),
                    imageUrl = TmdbUtils.getFullPosterPath(content),
                    onItemClicked = {
                        appNavController.navigate(
                            "${MainNavItem.DetailView.route}/${mediaType}/${content.id}"
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideosCard(itemId: Int?, service: DetailService, modifier: Modifier = Modifier) {
    val videoResponse = remember { mutableStateOf<VideoResponse?>(null) }
    itemId?.let {
        if (videoResponse.value == null) {
            fetchVideos(itemId, service, videoResponse)
        }
    }

    if (videoResponse.value != null && videoResponse.value!!.results.any { it.isOfficial }) {
        val results = videoResponse.value!!.results
        ExpandableContentCard(
            modifier = modifier,
            title = {
                Text(
                    text = stringResource(id = R.string.videos_label),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            toggleTextColor = MaterialTheme.colorScheme.primary
        ) { isExpanded ->
            VideoGroup(results = results, type = Video.Type.TRAILER, title = stringResource(id = Video.Type.TRAILER.stringRes))

            if (isExpanded) {
                Video.Type.values().filter { it != Video.Type.TRAILER}.forEach { type ->
                    VideoGroup(results = results, type = type, title = stringResource(id = type.stringRes))
                }
            }
        }
    }
}

@Composable
private fun VideoGroup(results: List<Video>, type: Video.Type, title: String) {
    val videos = results.filter { it.isOfficial && it.type == type }
    if (videos.isNotEmpty()) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )

        val posterWidth = 120.dp
        LazyRow(modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listItems(videos) { video ->
                FullScreenThumbnailVideoPlayer(
                    key = video.key,
                    modifier = Modifier
                        .width(posterWidth)
                        .height(90.dp)
                )
            }
        }
    }
}

private fun fetchMediaItem(id: Int, service: DetailService, mediaItem: MutableState<DetailedItem?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getById(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                mediaItem.value = response.body()
            }
        }
    }
}

private fun fetchImages(id: Int, service: DetailService, images: MutableState<ImageCollection?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getImages(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                images.value = response.body()
            }
        }
    }
}

private fun fetchCastAndCrew(id: Int, service: DetailService, castAndCrew: MutableState<CastAndCrew?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getCastAndCrew(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                castAndCrew.value = response.body()
            }
        }
    }
}

private fun fetchMovieContentRating(id: Int, service: MoviesService, contentRating: MutableState<String>) {
    CoroutineScope(Dispatchers.IO).launch {
        val results = service.getReleaseDates(id)
        if (results.isSuccessful) {
            val cr = TmdbUtils.getMovieRating(results.body())
            withContext(Dispatchers.Main) {
                contentRating.value = cr
            }
        }
    }
}

private fun fetchTvContentRating(id: Int, service: TvService, contentRating: MutableState<String>) {
    CoroutineScope(Dispatchers.IO).launch {
        val results = service.getContentRatings(id)
        if (results.isSuccessful) {
            val cr = TmdbUtils.getTvRating(results.body())
            withContext(Dispatchers.Main) {
                contentRating.value = cr
            }
        }
    }
}

private fun fetchSimilarContent(id: Int, service: DetailService, similarContent: MutableState<HomePageResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val results = service.getSimilar(id, 1)
        if (results.isSuccessful) {
            withContext(Dispatchers.Main) {
                similarContent.value = results.body()
            }
        }
    }
}

private fun fetchVideos(id: Int, service: DetailService, videoResponse: MutableState<VideoResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val results = service.getVideos(id)
        if (results.isSuccessful) {
            withContext(Dispatchers.Main) {
                videoResponse.value = results.body()
            }
        }
    }
}

private fun fetchPerson(id: Int, person: MutableState<DetailPerson?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = PeopleService().getPerson(id)
        if (result.isSuccessful) {
            withContext(Dispatchers.Main) {
                person.value = result.body()
            }
        }
    }
}

private fun fetchCredits(id: Int, credits: MutableState<PersonCreditsResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = PeopleService().getCredits(id)
        if (result.isSuccessful) {
            withContext(Dispatchers.Main) {
                credits.value = result.body()
            }
        }
    }
}