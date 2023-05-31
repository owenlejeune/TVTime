package com.owenlejeune.tvtime.ui.screens.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.V4AccountList
import com.owenlejeune.tvtime.extensions.unlessEmpty
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.PagingPosterGrid
import com.owenlejeune.tvtime.ui.components.RoundedLetterImage
import com.owenlejeune.tvtime.ui.components.SignInDialog
import com.owenlejeune.tvtime.ui.navigation.AccountTabNavItem
import com.owenlejeune.tvtime.ui.navigation.ListFetchFun
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.main.tabs.top.ScrollableTabs
import com.owenlejeune.tvtime.ui.viewmodel.RecommendedMediaViewModel
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.get
import kotlin.reflect.KClass

private const val GUEST_SIGN_IN = "guest_sign_in"
private const val SIGN_OUT = "sign_out"
private const val NO_SESSION_SIGN_IN = "no_session_sign_in"
private const val NO_SESSION_SIGN_IN_GUEST = "no_session_sign_in_guest"

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AccountTab(
    appNavController: NavHostController,
    appBarTitle: MutableState<String>,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({})
) {
    val lastSelectedOption = remember { mutableStateOf("") }

    val lso = lastSelectedOption.value
    if (SessionManager.isV4SignInInProgress) {
        appBarTitle.value = stringResource(id = R.string.account_not_logged_in)
        AccountLoadingView()
        v4SignInPart2(lastSelectedOption)
    } else {
        when (SessionManager.currentSession?.isAuthorized) {
            false -> {
                appBarTitle.value =
                    stringResource(
                        id = R.string.account_header_title_formatted,
                        stringResource(id = R.string.account_name_guest)
                    )
            }
            true -> {
                appBarTitle.value =
                    stringResource(
                        id = R.string.account_header_title_formatted,
                        getAccountName(SessionManager.currentSession?.accountDetails)
                    )
            }
            else -> {
                appBarTitle.value = stringResource(id = R.string.account_not_logged_in)
            }
        }

        appBarActions.value = {
            AccountDropdownMenu(
                session = SessionManager.currentSession,
                lastSelectedOption = lastSelectedOption
            )
        }

        if (!SessionManager.isV4SignInInProgress) {
            SessionManager.currentSession?.let { session ->
                val tabs = if (session.isAuthorized) {
                    AccountTabNavItem.AuthorizedItems
                } else {
                    AccountTabNavItem.GuestItems
                }

                Column {
                    when (session.isAuthorized) {
                        true -> {
                            AuthorizedSessionIcon()
                        }
                        false -> {
                            GuestSessionIcon()
                        }
                    }

                    val pagerState = rememberPagerState()
                    ScrollableTabs(tabs = tabs, pagerState = pagerState)
                    AccountTabs(
                        appNavController = appNavController,
                        tabs = tabs,
                        pagerState = pagerState
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountLoadingView() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

private fun getAccountName(accountDetails: AccountDetails?): String {
    if (accountDetails != null) {
        if (accountDetails.name.isNotEmpty()) {
            return accountDetails.name
        } else if (accountDetails.username.isNotEmpty()) {
            return accountDetails.username
        }
    }
    return ""
}

@Composable
fun <T: Any> AccountTabContent(
    noContentText: String,
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    listFetchFun: ListFetchFun,
    clazz: KClass<T>
) {
    val contentItems = listFetchFun()

    if (contentItems.isEmpty()) {
        Column {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = noContentText,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contentItems.size) { i ->
                when (clazz) {
                    RatedTv::class, RatedMovie::class -> {
                        val item = contentItems[i] as RatedTopLevelMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath),
                            name = item.name,
                            date = item.releaseDate,
                            rating = item.rating,
                            description = item.overview
                        )
                    }
                    RatedEpisode::class -> {
                        val item = contentItems[i] as RatedMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            name = item.name,
                            date = item.releaseDate,
                            rating = item.rating,
                            description = item.overview
                        )
                    }
                    FavoriteMovie::class, FavoriteTvSeries::class -> {
                        val item = contentItems[i] as FavoriteMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath),
                            name = item.title,
                            date = item.releaseDate,
                            description = item.overview
                        )
                    }
                    WatchlistMovie::class, WatchlistTvSeries::class -> {
                        val item = contentItems[i] as WatchlistMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath),
                            name = item.title,
                            date = item.releaseDate,
                            description = item.overview
                        )
                    }
                    V4AccountList::class -> {
                        val item = contentItems[i] as V4AccountList
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            name = item.name,
                            date = item.createdAt,
                            description = item.description.unlessEmpty(stringResource(R.string.no_description_provided)),
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedAccountTabContent(
    noContentText: String,
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
) {
    val viewModel = when (mediaViewType) {
        MediaViewType.MOVIE -> RecommendedMediaViewModel.RecommendedMoviesVM
        MediaViewType.TV -> RecommendedMediaViewModel.RecommendedTvVM
        else -> throw IllegalArgumentException("Media type given: ${mediaViewType}, \n     expected one of MediaViewType.MOVIE, MediaViewType.TV") // shouldn't happen
    }
    val mediaListItems = viewModel.mediaItems.collectAsLazyPagingItems()

    if (mediaListItems.itemCount < 1) {
        Column {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = noContentText,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        PagingPosterGrid(
            lazyPagingItems = mediaListItems,
            onClick = { id ->
                appNavController.navigate(
                    "${MainNavItem.DetailView.route}/${mediaViewType}/${id}"
                )
            }
        )
    }
}

@Composable
private fun MediaItemRow(
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    id: Int,
    name: String,
    date: String,
    description: String,
    posterPath: String? = null,
    backdropPath: String? = null,
    rating: Float? = null
) {
    MediaResultCard(
        appNavController = appNavController,
        mediaViewType = mediaViewType,
        id = id,
        backdropPath = backdropPath,
        posterPath = posterPath,
        title = "$name (${date.split("-")[0]})",
        additionalDetails = listOf(description),
        rating = rating
    )
}

@Composable
private fun AccountDropdownMenu(
    session: SessionManager.Session?,
    lastSelectedOption: MutableState<String>
) {
    val expanded = remember { mutableStateOf(false) }
    
    IconButton(
        onClick = { expanded.value = true }
    ) {
        Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = stringResource(id = R.string.nav_account_title))
    }
    
    DropdownMenu(
        expanded = expanded.value, 
        onDismissRequest = { expanded.value = false }
    ) {
        when(session?.isAuthorized) {
            true -> { AuthorizedSessionMenuItems(expanded = expanded, lastSelectedOption = lastSelectedOption) }
            false -> { GuestSessionMenuItems(expanded = expanded, lastSelectedOption = lastSelectedOption) }
            null -> { NoSessionMenuItems(expanded = expanded, lastSelectedOption = lastSelectedOption) }
        }
    }
}

@Composable
private fun AuthorizedSessionMenuItems(
    expanded: MutableState<Boolean>,
    lastSelectedOption: MutableState<String>,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.action_sign_out)) },
        onClick = {
            if (preferences.useV4Api) {
                signOutV4(lastSelectedOption)
            } else {
                signOut(lastSelectedOption)
            }
            expanded.value = false
        }
    )
}

@Composable
private fun GuestSessionMenuItems(
    expanded: MutableState<Boolean>,
    lastSelectedOption: MutableState<String>
) {
    val showSignInDialog = remember { mutableStateOf(false) }

    if (showSignInDialog.value) {
        SignInDialog(showDialog = showSignInDialog) { success ->
            if (success) {
                lastSelectedOption.value = GUEST_SIGN_IN
                expanded.value = false
            }
        }
    }

    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.action_sign_in)) },
        onClick = { showSignInDialog.value = true }
    )

    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.action_sign_out)) },
        onClick = {
            signOut(lastSelectedOption)
            expanded.value = false
        }
    )
}

@Composable
private fun NoSessionMenuItems(
    expanded: MutableState<Boolean>,
    lastSelectedOption: MutableState<String>,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val showSignInDialog = remember { mutableStateOf(false) }

    if (showSignInDialog.value) {
        SignInDialog(showDialog = showSignInDialog) { success ->
            if (success) {
                lastSelectedOption.value = NO_SESSION_SIGN_IN
                expanded.value = false
            }
        }
    }

    val context = LocalContext.current
    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.action_sign_in)) },
        onClick = {
            if (preferences.useV4Api) {
                v4SignInPart1(context)
            } else {
                showSignInDialog.value = true
            }
        }
    )

    DropdownMenuItem(
        text = { Text(text = stringResource(id = R.string.action_sign_in_as_guest)) },
        onClick = {
            createGuestSession(lastSelectedOption)
            expanded.value = false
        }
    )
}

private fun v4SignInPart1(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        SessionManager.signInWithV4Part1(context)
    }
}

private fun v4SignInPart2(lastSelectedOption: MutableState<String>) {
    CoroutineScope(Dispatchers.IO).launch {
        val signIn = SessionManager.signInWithV4Part2()
        if (signIn)  {
            withContext(Dispatchers.Main) {
                lastSelectedOption.value = NO_SESSION_SIGN_IN
            }
        }
    }
}

@Composable
private fun GuestSessionIcon() {
    val guestName = stringResource(id = R.string.account_name_guest)
    RoundedLetterImage(size = 60.dp, character = guestName[0])
}

@Composable
private fun AuthorizedSessionIcon() {
    val accountDetails = SessionManager.currentSession?.accountDetails
    val avatarUrl = accountDetails?.let {
        when {
            accountDetails.avatar.tmdb?.avatarPath?.isNotEmpty() == true -> {
                TmdbUtils.getAccountAvatarUrl(accountDetails)
            }
            accountDetails.avatar.gravatar?.isDefault() == false -> {
                TmdbUtils.getAccountGravatarUrl(accountDetails)
            }
            else -> null
        }
    }

    Box(modifier = Modifier.padding(start = 12.dp)) {
        if (accountDetails == null || avatarUrl == null) {
            val accLetter = (accountDetails?.name?.ifEmpty { accountDetails.username } ?: " ")[0]
            RoundedLetterImage(size = 60.dp, character = accLetter)
        } else {
            Box(modifier = Modifier.size(60.dp)) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

private fun createGuestSession(lastSelectedOption: MutableState<String>) {
    CoroutineScope(Dispatchers.IO).launch {
        val session = SessionManager.requestNewGuestSession()
        if (session != null) {
            withContext(Dispatchers.Main) {
                lastSelectedOption.value = NO_SESSION_SIGN_IN_GUEST
            }
        }
    }
}

private fun signOut(lastSelectedOption: MutableState<String>) {
    CoroutineScope(Dispatchers.IO).launch {
        SessionManager.clearSession { isSuccessful ->
            if (isSuccessful) {
                lastSelectedOption.value = SIGN_OUT
            }
        }
    }
}

private fun signOutV4(lastSelectedOption: MutableState<String>) {
    CoroutineScope(Dispatchers.IO).launch {
        SessionManager.clearSessionV4 { isSuccessful ->
            if (isSuccessful) {
                lastSelectedOption.value = SIGN_OUT
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun AccountTabs(
    tabs: List<AccountTabNavItem>,
    pagerState: PagerState,
    appNavController: NavHostController
) {
    HorizontalPager(count = tabs.size, state = pagerState) { page ->
        tabs[page].screen(tabs[page].noContentText, appNavController, tabs[page].mediaType, tabs[page].listFetchFun, tabs[page].listType)
    }
}