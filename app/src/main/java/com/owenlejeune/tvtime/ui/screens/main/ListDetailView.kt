package com.owenlejeune.tvtime.ui.screens.main

import android.content.Context
import android.content.Intent
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v4.ListV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListItem
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.MediaList
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.unlessEmpty
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailView(
    appNavController: NavController,
    itemId: Int?,
    windowSize: WindowSizeClass,
    preferences: AppPreferences = KoinJavaComponent.get(AppPreferences::class.java)
) {
    val service = ListV4Service()

    val listItem = remember { mutableStateOf<MediaList?>(null) }
    itemId?.let {
        if (listItem.value == null) {
            fetchList(itemId, service, listItem)
        }
    }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val topAppBarScrollState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults
                    .smallTopAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                title = { Text(text = listItem.value?.name ?: "") },
                navigationIcon = {
                    IconButton(
                        onClick = { appNavController.popBackStack() }
                    ) {
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
            listItem.value?.let { mediaList ->
                Column(
                    modifier = Modifier
                        .padding(all = 12.dp)
                        .verticalScroll(state = rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ListHeader(list = mediaList)

                    mediaList.results.forEach { listItem ->
                        ListItemView(
                            appNavController = appNavController,
                            listItem = listItem
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListHeader(list: MediaList) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp),
                model = TmdbUtils.getAccountGravatarUrl(list.createdBy.gravatarHash),
                contentDescription = null,
            )
            Text(
                text = stringResource(id = R.string.list_created_by, list.createdBy.name),
                fontSize = 20.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(all = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.list_description_label),
                    fontSize = 22.sp
                )

                Text(text = list.description.unlessEmpty(stringResource(id = R.string.no_description_provided)))
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewStatCard(top = list.results.size.toString(), bottom = stringResource(R.string.items_in_list_label))

            OverviewStatCard(top = "${(list.averageRating*10).roundToInt()}%", bottom = stringResource(R.string.average_rating_label))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewStatCard(
                top = TmdbUtils.convertRuntimeToHoursAndMinutes(list.runtime),
                bottom = stringResource(R.string.runtime_label)
            )

            OverviewStatCard(
                top = TmdbUtils.formatRevenue(list.revenue),
                bottom = stringResource(R.string.total_revenue_label)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                onClick = {  }
            ) {
                Text(text = stringResource(R.string.action_edit))
            }
            Button(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                onClick = {  }
            ) {
                Text(text = stringResource(R.string.action_sort_by))
            }
            Button(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                onClick = { shareListUrl(context, list.id) }
            ) {
                Text(text = stringResource(R.string.action_share))
            }
        }
    }
}

@Composable
private fun RowScope.OverviewStatCard(
    top: String,
    bottom: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .weight(1f)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = top,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = bottom,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListItemView(
    appNavController: NavController,
    listItem: ListItem
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .clickable(
                onClick = {
                    appNavController.navigate(
                        "${MainNavItem.DetailView.route}/${listItem.mediaType}/${listItem.id}"
                    )
                }
            )
    ) {
        Box(
            modifier = Modifier.height(112.dp)
        ) {
            listItem.backdropPath?.let {
                AsyncImage(
                    model = TmdbUtils.getFullBackdropPath(listItem.backdropPath),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .blur(radius = 10.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                )

                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .blur(radius = 10.dp)
                )
            }

            ConstraintLayout(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                val (poster, content, delete) = createRefs()

                AsyncImage(
                    modifier = Modifier
                        .constrainAs(poster) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }
                        .aspectRatio(0.7f)
                        .clip(RoundedCornerShape(10.dp)),
                    model = TmdbUtils.getFullPosterPath(listItem.posterPath) ?: R.drawable.placeholder_transparent,
                    contentDescription = listItem.title
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .constrainAs(content) {
                            end.linkTo(delete.start, margin = 12.dp)
                            start.linkTo(poster.end, margin = 12.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.matchParent
                        }
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    val textColor = if (listItem.backdropPath != null || isSystemInDarkTheme()) {
                        Color.White
                    } else {
                        Color.Black
                    }
                    Text(
                        text = listItem.title,
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Image(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.remove_from_list_cd),
                    colorFilter = ColorFilter.tint(color = Color.Red),
                    modifier = Modifier.constrainAs(delete) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end, margin = 12.dp)
                    }
                )
            }
        }
    }
}

private fun shareListUrl(context: Context, listId: Int) {
    val shareUrl = "https://www.themoviedb.org/list/$listId"
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareUrl)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

private fun fetchList(
    itemId: Int,
    service: ListV4Service,
    listItem: MutableState<MediaList?>
) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getList(itemId)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                listItem.value = response.body()
            }
        }
    }
}