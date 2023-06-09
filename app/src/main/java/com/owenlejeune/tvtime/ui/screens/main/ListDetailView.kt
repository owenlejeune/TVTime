package com.owenlejeune.tvtime.ui.screens.main
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.rememberSplineBasedDecay
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.AccountService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MarkAsFavoriteBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.ListV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.*
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.unlessEmpty
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.Spinner
import com.owenlejeune.tvtime.ui.components.SwitchPreference
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.theme.*
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
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
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    val service = ListV4Service()

    val parentList = remember { mutableStateOf<MediaList?>(null) }
    itemId?.let {
        if (parentList.value == null) {
            fetchList(itemId, service, parentList)
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
                title = { Text(text = parentList.value?.name ?: "") },
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
            parentList.value?.let { mediaList ->
                Column(
                    modifier = Modifier
                        .padding(all = 12.dp)
                        .verticalScroll(state = rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val selectedSortOrder = remember { mutableStateOf(mediaList.sortBy) }
                    ListHeader(
                        list = mediaList,
                        selectedSortOrder = selectedSortOrder,
                        service = service,
                        parentList = parentList
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
    selectedSortOrder: MutableState<SortOrder>,
    service: ListV4Service,
    parentList: MutableState<MediaList?>
) {
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
                list = list,
                service = service,
                parentList = parentList,
                selectedSortOrder = selectedSortOrder
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
    list: MediaList,
    service: ListV4Service,
    parentList: MutableState<MediaList?>,
    selectedSortOrder: MutableState<SortOrder>
) {
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
                        val response = service.updateList(list.id, listUpdateBody)
                        if (response.isSuccessful) {
                            fetchList(list.id, service, parentList)
                            selectedSortOrder.value = editSelectedSortOrder
                        }
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
    list: MutableState<MediaList?>
) {
    val context = LocalContext.current
    RevealSwipe (
        directions = setOf(RevealDirection.EndToStart),
        hiddenContentEnd = {
            IconButton(
                modifier = Modifier.padding(horizontal = 15.dp),
                onClick = {
                    removeItemFromList(
                        context = context,
                        itemId = listItem.id,
                        itemType =  listItem.mediaType,
                        itemName = listItem.title,
                        service = ListV4Service(),
                        list = list
                    )
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
                    val (poster, content, ratingView) = createRefs()

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
                        ActionButtonRow(listItem)
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

@Composable
private fun ActionButtonRow(listItem: ListItem) {
    val session = SessionManager.currentSession.value

    val (isFavourited, isWatchlisted, isRated) = if (listItem.mediaType == MediaViewType.MOVIE) {
        Triple(
            session?.hasFavoritedMovie(listItem.id) == true,
            session?.hasWatchlistedMovie(listItem.id) == true,
            session?.hasRatedMovie(listItem.id) == true
        )
    } else {
        Triple(
            session?.hasFavoritedTvShow(listItem.id) == true,
            session?.hasWatchlistedTvShow(listItem.id) == true,
            session?.hasRatedTvShow(listItem.id) == true
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActionButton(
            itemId = listItem.id,
            type = listItem.mediaType,
            iconRes = R.drawable.ic_favorite,
            contentDescription = stringResource(id = R.string.favourite_label),
            isSelected = isFavourited,
            filledIconColor = FavoriteSelected,
            onClick = ::addToFavorite
        )

        ActionButton(
            itemId = listItem.id,
            type = listItem.mediaType,
            iconRes = R.drawable.ic_watchlist,
            contentDescription = "",
            isSelected = isWatchlisted,
            filledIconColor = WatchlistSelected,
            onClick = ::addToWatchlist
        )

        val context = LocalContext.current
        ActionButton(
            itemId = listItem.id,
            type = listItem.mediaType,
            iconRes = R.drawable.ic_rating_star,
            contentDescription = "",
            isSelected = isRated,
            filledIconColor = RatingSelected,
            onClick = { c, i, t, s, f ->
                // todo - add rating
                Toast.makeText(context, "Rating", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

private fun addToWatchlist(
    context: Context,
    itemId: Int,
    type: MediaViewType,
    itemIsWatchlisted: MutableState<Boolean>,
    onWatchlistChanged: (Boolean) -> Unit
) {
    val currentSession = SessionManager.currentSession.value
    val accountId = currentSession!!.accountDetails.value!!.id
    CoroutineScope(Dispatchers.IO).launch {
        val response = AccountService().addToWatchlist(accountId, WatchlistBody(type, itemId, !itemIsWatchlisted.value))
        if (response.isSuccessful) {
            currentSession.refresh(changed = SessionManager.Session.Changed.Watchlist)
            withContext(Dispatchers.Main) {
                itemIsWatchlisted.value = !itemIsWatchlisted.value
                onWatchlistChanged(itemIsWatchlisted.value)
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun addToFavorite(
    context: Context,
    itemId: Int,
    type: MediaViewType,
    itemIsFavorited: MutableState<Boolean>,
    onFavoriteChanged: (Boolean) -> Unit
) {
    val currentSession = SessionManager.currentSession.value
    val accountId = currentSession!!.accountDetails.value!!.id
    CoroutineScope(Dispatchers.IO).launch {
        val response = AccountService().markAsFavorite(accountId, MarkAsFavoriteBody(type, itemId, !itemIsFavorited.value))
        if (response.isSuccessful) {
            currentSession.refresh(changed = SessionManager.Session.Changed.Favorites)
            withContext(Dispatchers.Main) {
                itemIsFavorited.value = !itemIsFavorited.value
                onFavoriteChanged(itemIsFavorited.value)
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

private fun removeItemFromList(
    context: Context,
    itemName: String,
    itemId: Int,
    itemType: MediaViewType,
    service: ListV4Service,
    list: MutableState<MediaList?>
) {
    CoroutineScope(Dispatchers.IO).launch {
        val listId = list.value?.id ?: 0
        val removeItem = DeleteListItemsItem(itemId, itemType)
        val result = service.deleteListItems(listId, DeleteListItemsBody(listOf(removeItem)))
        if (result.isSuccessful) {
            SessionManager.currentSession.value?.refresh(SessionManager.Session.Changed.List)
            service.getList(listId).body()?.let {
                withContext(Dispatchers.Main) {
                    list.value = it
                }
            }
            Toast.makeText(context, "Successfully removed $itemName", Toast.LENGTH_SHORT).show()
        } else {
            Log.w("RemoveListItemError", result.toString())
            Toast.makeText(context, "An error occurred!", Toast.LENGTH_SHORT).show()
        }
    }
}