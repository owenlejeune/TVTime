package com.owenlejeune.tvtime.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

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
            backButton, backdropImage, posterImage, titleText, contentColumn, ratingsView
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

        Box(
            Modifier
                .clip(CircleShape)
                .size(60.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .constrainAs(ratingsView) {
                    bottom.linkTo(titleText.top)
                    start.linkTo(posterImage.end, margin = 20.dp)
                }
        ) {
            RatingRing(
                modifier = Modifier.padding(5.dp),
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                progress = mediaItem.value?.voteAverage?.let { it / 10 } ?: 0f,
                textSize = 14.sp,
                ringColor = MaterialTheme.colorScheme.primary,
                ringStrokeWidth = 4.dp,
                size = 50.dp
            )
        }

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

        ActionsView(itemId = itemId, type = mediaType)

        if (mediaItem.value?.overview?.isNotEmpty() == true) {
            OverviewCard(mediaItem = mediaItem)
        }

        CastCard(itemId = itemId, service = service, appNavController = appNavController)

        SimilarContentCard(itemId = itemId, service = service, mediaType = mediaType, appNavController = appNavController)
        
        VideosCard(itemId = itemId, service = service)
        
        ReviewsCard(itemId = itemId, service = service)
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
private fun ActionsView(
    itemId: Int?,
    type: MediaViewType,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    itemId?.let {
        val session = SessionManager.currentSession
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val itemIsRated = if (type == MediaViewType.MOVIE) {
                session.hasRatedMovie(itemId)
            } else {
                session.hasRatedTvShow(itemId)
            }
            val showRatingDialog = remember { mutableStateOf(false) }
            ActionButton(
                modifier = Modifier.weight(1f),
                text = if (itemIsRated) stringResource(R.string.delete_rating_action_label) else stringResource(R.string.rate_action_label),
                onClick = {
                    showRatingDialog.value = true
                }
            )
            RatingDialog(showDialog = showRatingDialog, onValueConfirmed = { rating ->
                // todo post rating
                Toast.makeText(context, "Rating :${rating}", Toast.LENGTH_SHORT).show()
            })
            if (!session.isGuest) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.add_to_list_action_label),
                    onClick = { /*TODO*/ }
                )
                ActionButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.favourite_label),
                    onClick = { /*TODO*/ }
                )
            }
        }
    }
}

@Composable
private fun ActionButton(modifier: Modifier, text: String, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@Composable
private fun RatingDialog(showDialog: MutableState<Boolean>, onValueConfirmed: (Float) -> Unit) {

    fun formatPosition(position: Float): String {
        return DecimalFormat("#.#").format(position/10f)
    }

    if (showDialog.value) {
        var sliderPosition by remember { mutableStateOf(0f) }
        AlertDialog(
            modifier = Modifier.wrapContentHeight(),
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.rating_dialog_title)) },
            confirmButton = {
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        onValueConfirmed.invoke(formatPosition(sliderPosition).toFloat())
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.rating_dialog_confirm))
                }
            },
            dismissButton = {
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
            text = {
                SliderWithLabel(
                    value = sliderPosition,
                    valueRange = 0f..100f,
                    onValueChanged = { sliderPosition = it },
                    sliderLabel = formatPosition(sliderPosition)
                )
            }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReviewsCard(
    itemId: Int?,
    service: DetailService,
    modifier: Modifier = Modifier
) {
    val reviewsResponse = remember { mutableStateOf<ReviewResponse?>(null) }
    itemId?.let {
        if (reviewsResponse.value == null) {
            fetchReviews(itemId, service, reviewsResponse)
        }
    }
    // > 0
    val hasReviews = reviewsResponse.value?.results?.size?.let { it > 0 }
    val m = if (hasReviews == true) {
        modifier.height(400.dp)
    } else {
        modifier.height(200.dp)
    }

    LazyListContentCard(
        modifier = m
            .fillMaxWidth(),
        header = {
            Text(
                text = "Reviews",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var reviewTextState by remember { mutableStateOf("") }

                RoundedTextField(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(top = 4.dp)
                        .weight(1f),
                    value = reviewTextState,
                    onValueChange = { reviewTextState = it },
                    placeHolder = "Add a review",
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    placeHolderTextColor = MaterialTheme.colorScheme.background,
                    textColor = MaterialTheme.colorScheme.onSecondary
                )

                CircleBackgroundColorImage(
                    size = 40.dp,
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
                    image = Icons.Filled.Send,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surfaceVariant),
                    contentDescription = ""
                )
            }

        }
    ) {
        val reviews = reviewsResponse.value?.results ?: emptyList()
        if (reviews.isNotEmpty()) {
            items(reviews.size) { i ->
                val review = reviews[i]

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AvatarImage(
                            size = 50.dp,
                            author = review.authorDetails
                        )

                        // todo - only show this for user's review
                        CircleBackgroundColorImage(
                            image = Icons.Filled.Delete,
                            size = 30.dp,
                            backgroundColor = MaterialTheme.colorScheme.error,
                            contentDescription = "",
                            imageHeight = 15.dp,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = review.author,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )

                        HtmlText(
                            text = review.content,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val createdAt = TmdbUtils.formatDate(review.createdAt)
                        val updatedAt = TmdbUtils.formatDate(review.updatedAt)
                        var timestamp = stringResource(id = R.string.created_at_label, createdAt)
                        if (updatedAt != createdAt) {
                            timestamp += "\n${stringResource(id = R.string.updated_at_label, updatedAt)}"
                        }
                        Text(
                            text = timestamp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
                Divider(
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 50.dp, vertical = 6.dp)
                )
            }
        } else {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 24.dp, vertical = 22.dp),
                    text = stringResource(R.string.no_reviews_label),
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium
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

private fun fetchReviews(id: Int, service: DetailService, reviewResponse: MutableState<ReviewResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = service.getReviews(id)
        if (result.isSuccessful) {
            withContext(Dispatchers.Main) {
                reviewResponse.value = result.body()
            }
        }
    }
}