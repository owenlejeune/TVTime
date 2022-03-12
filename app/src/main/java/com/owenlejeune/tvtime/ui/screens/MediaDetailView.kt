package com.owenlejeune.tvtime.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.DetailService
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.api.tmdb.model.*
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.ui.components.*
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.theme.RatingSelected
import com.owenlejeune.tvtime.ui.theme.actionButtonColor
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.DecimalFormat

@Composable
fun MediaDetailView(
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

   Column(
       modifier = Modifier
           .background(color = MaterialTheme.colorScheme.background)
           .verticalScroll(rememberScrollState())
           .fillMaxSize()
           .padding(bottom = 16.dp),
       verticalArrangement = Arrangement.spacedBy(16.dp)
   ) {
       DetailHeader(
           appNavController = appNavController,
           title = mediaItem.value?.title ?: "",
           posterUrl = TmdbUtils.getFullPosterPath(mediaItem.value?.posterPath),
           posterContentDescription = mediaItem.value?.title,
           backdropUrl = TmdbUtils.getFullBackdropPath(mediaItem.value?.backdropPath),
           rating = mediaItem.value?.voteAverage?.let { it / 10 }
       )

       Column(
           modifier = Modifier.padding(horizontal = 16.dp),
           verticalArrangement = Arrangement.spacedBy(16.dp)
       ) {
           if (type == MediaViewType.MOVIE) {
               MiscMovieDetails(mediaItem = mediaItem, service as MoviesService)
           } else {
               MiscTvDetails(mediaItem = mediaItem, service as TvService)
           }

           OverviewCard(itemId = itemId, mediaItem = mediaItem, service = service)

           CastCard(itemId = itemId, service = service, appNavController = appNavController)

           SimilarContentCard(itemId = itemId, service = service, mediaType = type, appNavController = appNavController)

           VideosCard(itemId = itemId, service = service)

           ActionsView(itemId = itemId, type = type, service = service)

           ReviewsCard(itemId = itemId, service = service)
       }

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
    service: DetailService,
    modifier: Modifier = Modifier
) {
    itemId?.let {
        val session = SessionManager.currentSession
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RateButton(
                modifier = Modifier.weight(1f),
                itemId = itemId,
                type = type,
                service = service
            )

            if (session?.isAuthorized == true) {
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
private fun RateButton(
    itemId: Int,
    type: MediaViewType,
    service: DetailService,
    modifier: Modifier = Modifier
) {
    val session = SessionManager.currentSession
    val context = LocalContext.current

    val itemIsRated = remember {
        mutableStateOf(
            if (type == MediaViewType.MOVIE) {
                session?.hasRatedMovie(itemId) == true
            } else {
                session?.hasRatedTvShow(itemId) == true
            }
        )
    }

    val showRatingDialog = remember { mutableStateOf(false) }
    val showSessionDialog = remember { mutableStateOf(false) }

    CircleBackgroundColorImage(
        modifier = Modifier.clickable(
            onClick = {
                if (SessionManager.currentSession != null) {
                    showRatingDialog.value = true
                } else {
                    showSessionDialog.value = true
                }
            }
        ),
        size = 40.dp,
        backgroundColor = MaterialTheme.colorScheme.actionButtonColor,
        painter = painterResource(id = R.drawable.ic_rating_star),
        colorFilter = ColorFilter.tint(color = if (itemIsRated.value) RatingSelected else MaterialTheme.colorScheme.background),
        contentDescription = ""
    )

    RatingDialog(showDialog = showRatingDialog, onValueConfirmed = { rating ->
        if (rating > 0f) {
            postRating(context, rating, itemId, service, itemIsRated)
        } else {
            deleteRating(context, itemId, service, itemIsRated)
        }
    })

    CreateSessionDialog(showDialog = showSessionDialog, onSessionReturned = {
        showRatingDialog.value = it
    })
}

fun postRating(context: Context, rating: Float, itemId: Int, service: DetailService, itemIsRated: MutableState<Boolean>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.postRating(itemId, RatingBody(rating = rating))
        if (response.isSuccessful) {
            SessionManager.currentSession?.refresh(changed = arrayOf(SessionManager.Session.Changed.RatedMovies, SessionManager.Session.Changed.RatedTv))
            withContext(Dispatchers.Main) {
                itemIsRated.value = true
            }
        } else {
            withContext(Dispatchers.Main) {
                val errorObj = JSONObject(response.errorBody().toString())
                Toast.makeText(context, "Error: ${errorObj.getString("status_message")}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun deleteRating(context: Context, itemId: Int, service: DetailService, itemIsRated: MutableState<Boolean>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.deleteRating(itemId)
        if (response.isSuccessful) {
            SessionManager.currentSession?.refresh(changed = arrayOf(SessionManager.Session.Changed.RatedMovies, SessionManager.Session.Changed.RatedTv))
            withContext(Dispatchers.Main) {
                itemIsRated.value = false
            }
        } else {
            withContext(Dispatchers.Main) {
                val errorObj = JSONObject(response.errorBody().toString())
                Toast.makeText(context, "Error: ${errorObj.getString("status_message")}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun CreateSessionDialog(showDialog: MutableState<Boolean>, onSessionReturned: (Boolean) -> Unit) {
    if (showDialog.value) {
        AlertDialog(
            modifier = Modifier.wrapContentHeight(),
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.sign_in_dialog_title)) },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                SessionManager.requestNewGuestSession()?.let {
                                    withContext(Dispatchers.Main) {
                                        showDialog.value = false
                                        onSessionReturned(true)
                                    }
                                }
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.action_continue_as_guest))
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showDialog.value = false
                        }
                    ) {
                        Text(text = stringResource(R.string.action_sign_in))
                    }
                }
            }
        )
    }
}

@Composable
private fun RatingDialog(showDialog: MutableState<Boolean>, onValueConfirmed: (Float) -> Unit) {

    fun formatPosition(position: Float): String {
        return DecimalFormat("#.#").format(position.toInt()*5/10f)
    }

    if (showDialog.value) {
        var sliderPosition by remember { mutableStateOf(0f) }
        val formatted = formatPosition(sliderPosition).toFloat()
        AlertDialog(
            modifier = Modifier.wrapContentHeight(),
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.rating_dialog_title)) },
            confirmButton = {
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        onValueConfirmed.invoke(formatted)
                        showDialog.value = false
                    }
                ) {
                    Text(
                        text = if (formatted > 0f) {
                            stringResource(id = R.string.rating_dialog_confirm)
                        } else {
                            stringResource(id = R.string.rating_dialog_delete)
                        }
                    )
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
                    valueRange = 0f..20f,
                    onValueChanged = {
                        sliderPosition = it
                     },
                    sliderLabel = "${sliderPosition.toInt() * 5}%",
                )
            }
        )
    }
}

@Composable
private fun OverviewCard(itemId: Int?, mediaItem: MutableState<DetailedItem?>, service: DetailService, modifier: Modifier = Modifier) {
    val keywordResponse = remember { mutableStateOf<KeywordsResponse?>(null) }
    if (itemId != null) {
        if (keywordResponse.value == null) {
            fetchKeywords(itemId, service, keywordResponse)
        }
    }

    mediaItem.value?.let { mi ->
        ContentCard(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mi.tagline?.let { tagline ->
                    Text(
                        text = tagline,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic
                    )
                }
                Text(
                    text = mi.overview ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )


                keywordResponse.value?.keywords?.let { keywords ->
                    val names = keywords.map { it.name }
                    ChipGroup(
                        chips = names,
                        chipStyle = ChipStyle.Rounded,
                        onSelectedChanged = { chip ->
                            if (service is MoviesService) {
//                            Toast.makeText(context, chip, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
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
            if (SessionManager.currentSession?.isAuthorized == true) {
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
                            imageSize = DpSize(width = 20.dp, height = 15.dp),
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

private fun fetchKeywords(id: Int, service: DetailService, keywordsResponse: MutableState<KeywordsResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = service.getKeywords(id)
        if (result.isSuccessful) {
            withContext(Dispatchers.Main) {
                keywordsResponse.value = result.body()
            }
        }
    }
}