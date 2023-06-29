package com.owenlejeune.tvtime.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v4.ListV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.*
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.unlessEmpty
import com.owenlejeune.tvtime.ui.components.Actions
import com.owenlejeune.tvtime.ui.components.ActionsView
import com.owenlejeune.tvtime.ui.components.RatingView
import com.owenlejeune.tvtime.ui.components.Spinner
import com.owenlejeune.tvtime.ui.components.SwitchPreference
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.theme.*
import com.owenlejeune.tvtime.ui.viewmodel.AccountViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    appNavController: NavController,
    itemId: Int,
    windowSize: WindowSizeClass,
    service: ListV4Service = KoinJavaComponent.get(ListV4Service::class.java)
) {
    val accountViewModel = viewModel<AccountViewModel>()
    LaunchedEffect(Unit) {
        accountViewModel.getList(itemId)
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    val listMap = remember { accountViewModel.listMap }
    val parentList = listMap[itemId]

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
                title = { Text(text = parentList?.name ?: "") },
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
            parentList?.let { mediaList ->
                Column(
                    modifier = Modifier
                        .padding(all = 12.dp)
                        .verticalScroll(state = rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val selectedSortOrder = remember { mutableStateOf(mediaList.sortBy) }
                    ListHeader(
                        list = mediaList,
                        selectedSortOrder = selectedSortOrder
                    )

                    val sortedResults = selectedSortOrder.value.sort(mediaList.results)
                    sortedResults.forEach { listItem ->
                        ListItemView(
                            appNavController = appNavController,
                            listItem = listItem,
                            list = parentList
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListHeader(
    list: MediaList,
    selectedSortOrder: MutableState<SortOrder>
) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val url = TmdbUtils.getAccountGravatarUrl(list.createdBy.gravatarHash)
            val model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .diskCacheKey(url ?: "")
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp),
                model = model,
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

        val showSortByOrderDialog = remember { mutableStateOf(false) }
        val showEditListDialog = remember { mutableStateOf(false) }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                onClick = { showEditListDialog.value = true }
            ) {
                Text(text = stringResource(R.string.action_edit))
            }
            Button(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                onClick = { showSortByOrderDialog.value = true }
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

        if (showSortByOrderDialog.value) {
            SortOrderDialog(
                showSortByOrderDialog = showSortByOrderDialog,
                selectedSortOrder = selectedSortOrder
            )
        }

        if (showEditListDialog.value) {
            EditListDialog(
                showEditListDialog = showEditListDialog,
                list = list
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortOrderDialog(
    showSortByOrderDialog: MutableState<Boolean>,
    selectedSortOrder: MutableState<SortOrder>
) {
    AlertDialog(
        modifier = Modifier.wrapContentSize(),
        onDismissRequest = { showSortByOrderDialog.value = false },
        confirmButton = {
            Button(
                onClick = { showSortByOrderDialog.value = false }
            ) {
                Text(text = stringResource(id = R.string.action_dismiss))
            }
        },
        title = { Text(text = stringResource(id = R.string.action_sort_by)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SortOrder.values().forEach {
                    Row(
                        modifier = Modifier.selectable(
                            selected = selectedSortOrder.value == it,
                            onClick = {
                                selectedSortOrder.value = it
                                showSortByOrderDialog.value = false
                            },
                            role = Role.RadioButton
                        ),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSortOrder.value == it,
                            onClick = null
                        )
                        Text(text = stringResource(id = it.stringKey), fontSize = 18.sp)
                    }
                }
            }
        }
    )
}

@Composable
private fun EditListDialog(
    showEditListDialog: MutableState<Boolean>,
    list: MediaList
) {
    val accountViewModel = viewModel<AccountViewModel>()

    val coroutineScope = rememberCoroutineScope()

    var listTitle by remember { mutableStateOf(list.name) }
    var listDescription by remember { mutableStateOf(list.description) }
    var isPublicList by remember { mutableStateOf(list.isPublic) }
    var editSelectedSortOrder by remember { mutableStateOf(list.sortBy) }

    AlertDialog(
        onDismissRequest = { },
        dismissButton = {
            Button(
                onClick = { showEditListDialog.value = false }
            ) {
                Text(text = stringResource(id = R.string.action_dismiss))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val listUpdateBody = ListUpdateBody(listTitle, listDescription, isPublicList, editSelectedSortOrder)
                    coroutineScope.launch {
                        accountViewModel.updateList(list.id, listUpdateBody)
                        showEditListDialog.value = false
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.action_save))
            }
        },
        text = {
           Column(
               verticalArrangement = Arrangement.spacedBy(12.dp)
           ) {
               OutlinedTextField(
                   value = listTitle,
                   onValueChange = { listTitle = it },
                   singleLine = true,
                   label = { Text(text = stringResource(id = R.string.label_name)) }
               )

               OutlinedTextField(
                   value = listDescription,
                   onValueChange = { listDescription = it },
                   modifier = Modifier.heightIn(min = 100.dp),
                   label = { Text(text = stringResource(id = R.string.label_description)) }
               )

               SwitchPreference(
                   titleText = stringResource(id = R.string.label_public_list),
                   checkState = isPublicList,
                   onCheckedChange = { isPublicList = it }
               )

               Text(
                   text = stringResource(id = R.string.action_sort_by),
                   fontSize = 16.sp
               )
               Spinner(
                   list = SortOrder.values().map { stringResource(id = it.stringKey) to it },
                   preselected = Pair(stringResource(id = editSelectedSortOrder.stringKey), editSelectedSortOrder),
                   onSelectionChanged = { editSelectedSortOrder = it.second }
               )
           }
        },
        title = { Text(text = stringResource(id = R.string.title_edit_list)) }
    )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun ListItemView(
    appNavController: NavController,
    listItem: ListItem,
    list: MediaList?
) {
    val accountViewModel = viewModel<AccountViewModel>()
    val scope = rememberCoroutineScope()

    RevealSwipe (
        directions = setOf(RevealDirection.EndToStart),
        hiddenContentEnd = {
            IconButton(
                modifier = Modifier.padding(horizontal = 15.dp),
                onClick = {
                    scope.launch {
                        accountViewModel.deleteListItem(
                            list?.id ?: -1,
                            listItem.id,
                            listItem.mediaType
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.remove_from_list_cd),
                    tint = Color.White
                )
            }
        },
        backgroundCardEndColor = SwipeRemoveBackground
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
                            AppNavItem.DetailView.withArgs(listItem.mediaType, listItem.id)
                        )
                    }
                )
        ) {
            Box(
                modifier = Modifier.height(112.dp)
            ) {
                listItem.backdropPath?.let {
                    val url = TmdbUtils.getFullBackdropPath(listItem.backdropPath)
                    val model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .diskCacheKey(url ?: "")
                        .networkCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build()
                    AsyncImage(
                        model = model,
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
                    val (poster, content, ratingView) = createRefs()

                    val url = TmdbUtils.getFullPosterPath(listItem.posterPath)
                    val model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .diskCacheKey(url ?: "")
                        .networkCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build()
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
                        model = model,
                        contentDescription = listItem.title
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .constrainAs(content) {
                                end.linkTo(ratingView.start, margin = 12.dp)
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
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = listItem.title,
                            color = textColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        ActionsView(
                            itemId = listItem.id,
                            type = listItem.mediaType,
                            actions = listOf(Actions.RATE, Actions.WATCHLIST, Actions.FAVORITE)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }


                    RatingView(
                        progress = listItem.voteAverage / 10f,
                        modifier = Modifier
                            .constrainAs(ratingView) {
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                    )
                }
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