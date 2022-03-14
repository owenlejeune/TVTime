package com.owenlejeune.tvtime.ui.screens.tabs.bottom

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.ui.components.*
import com.owenlejeune.tvtime.ui.navigation.AccountTabNavItem
import com.owenlejeune.tvtime.ui.navigation.ListFetchFun
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import com.owenlejeune.tvtime.ui.screens.tabs.top.ScrollableTabs
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    when (SessionManager.currentSession?.isAuthorized) {
        false -> {
            appBarTitle.value =
                stringResource(id = R.string.account_header_title_formatted, stringResource(id = R.string.account_name_guest))
        }
        true -> {
            appBarTitle.value =
                stringResource(id = R.string.account_header_title_formatted, getAccountName(SessionManager.currentSession?.accountDetails))
        }
        else -> {
            appBarTitle.value = stringResource(id = R.string.account_not_logged_in)
        }
    }

    val lastSelectedOption = remember { mutableStateOf("") }

    appBarActions.value = {
        AccountDropdownMenu(session = SessionManager.currentSession, lastSelectedOption = lastSelectedOption)
    }

    if (lastSelectedOption.value.isNotBlank() || lastSelectedOption.value.isBlank()) {
        SessionManager.currentSession?.let { session ->
            val tabs = if (session.isAuthorized) {
                AccountTabNavItem.AuthorizedItems
            } else {
                AccountTabNavItem.GuestItems
            }

            Column {
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
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    listFetchFun: ListFetchFun,
    clazz: KClass<T>
) {
    val contentItems = listFetchFun()

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (contentItems.isEmpty()) {
            item {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 15.dp),
                    text = stringResource(R.string.no_rated_content_message),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(contentItems.size) { i ->
                when (clazz) {
                    RatedTv::class, RatedMovie::class -> {
                        val item = contentItems[i] as RatedTopLevelMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            name = item.name,
                            date = item.releaseDate,
                            rating = item.rating
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
                            rating = item.rating
                        )
                    }
                    FavoriteMovie::class, FavoriteTvSeries::class -> {
                        val item = contentItems[i] as FavoriteMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            name = item.title,
                            date = item.releaseDate
                        )
                    }
                    WatchlistMovie::class, WatchlistTvSeries::class -> {
                        val item = contentItems[i] as WatchlistMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            name = item.title,
                            date = item.releaseDate
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaItemRow(
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    id: Int,
    name: String,
    date: String,
    posterPath: String? = null,
    rating: Float? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(
            onClick = {
                appNavController.navigate(
                    "${MainNavItem.DetailView.route}/${mediaViewType}/${id}"
                )
            }
        )
    ) {
        Image(
            modifier = Modifier
                .width(60.dp)
                .height(80.dp),
            painter = rememberImagePainter(
                data = posterPath
            ),
            contentDescription = ""
        )

        Column(
            modifier = Modifier.height(80.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp
            )

            Text(
                text = date,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (rating != null) {
                Text(
                    text = stringResource(id = R.string.rating_test, (rating * 10).toInt()),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun AccountDropdownMenu(
    session: SessionManager.Session?,
    lastSelectedOption: MutableState<String>
) {
    CustomTopAppBarDropdownMenu(
        icon = {
            when(session?.isAuthorized) {
                true -> { AuthorizedSessionIcon() }
                false -> { GuestSessionIcon() }
                null -> { NoSessionAccountIcon() }
            }
        }
    ) { expanded ->
        when(session?.isAuthorized) {
            true -> { AuthorizedSessionMenuItems(expanded = expanded, lastSelectedOption = lastSelectedOption) }
            false -> { GuestSessionMenuItems(expanded = expanded, lastSelectedOption = lastSelectedOption) }
            null -> { NoSessionMenuItems(expanded = expanded, lastSelectedOption = lastSelectedOption) }
        }
    }
}

@Composable
private fun NoSessionMenuItems(
    expanded: MutableState<Boolean>,
    lastSelectedOption: MutableState<String>
) {
    val showSignInDialog = remember { mutableStateOf(false) }
    CustomMenuItem(
        text = stringResource(id = R.string.action_sign_in),
        onClick = {
            showSignInDialog.value = true
        }
    )

    CustomMenuDivider()

    if (showSignInDialog.value) {
        SignInDialog(showDialog = showSignInDialog) { success ->
            if (success) {
                lastSelectedOption.value = NO_SESSION_SIGN_IN
                expanded.value = false
            }
        }
    }

    CustomMenuItem(
        text = stringResource(R.string.action_sign_in_as_guest),
        onClick = {
            createGuestSession(lastSelectedOption)
            expanded.value = false
        }
    )
}

@Composable
private fun NoSessionAccountIcon() {
    Icon(
        modifier = Modifier.size(45.dp),
        imageVector = Icons.Filled.AccountCircle,
        contentDescription = stringResource(R.string.account_menu_content_description),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun GuestSessionMenuItems(
    expanded: MutableState<Boolean>,
    lastSelectedOption: MutableState<String>
) {
    val showSignInDialog = remember { mutableStateOf(false) }
    CustomMenuItem(
        text = stringResource(id = R.string.action_sign_in),
        onClick = { showSignInDialog.value = true }
    )

    if (showSignInDialog.value) {
        SignInDialog(showDialog = showSignInDialog) { success ->
            if (success) {
                lastSelectedOption.value = GUEST_SIGN_IN
                expanded.value = false
            }
        }
    }

    CustomMenuDivider()

    CustomMenuItem(
        text = stringResource(id = R.string.action_sign_out),
        onClick = {
            signOut(lastSelectedOption)
            expanded.value = false
        }
    )
}

@Composable
private fun GuestSessionIcon() {
    val guestName = stringResource(id = R.string.account_name_guest)
    RoundedLetterImage(size = 40.dp, character = guestName[0], topPadding = 40.dp / 8)
}

@Composable
private fun AuthorizedSessionMenuItems(
    expanded: MutableState<Boolean>,
    lastSelectedOption: MutableState<String>
) {
    CustomMenuItem(
        text = stringResource(id = R.string.action_sign_out),
        onClick = {
            signOut(lastSelectedOption)
            expanded.value = false
        }
    )
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
    if (accountDetails == null || avatarUrl == null) {
        val accLetter = (accountDetails?.name?.ifEmpty { accountDetails.username } ?: " ")[0]
        RoundedLetterImage(size = 40.dp, character = accLetter, topPadding = 40.dp / 8)
    } else {
        Box(modifier = Modifier.size(50.dp)) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
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
    SessionManager.clearSession { isSuccessful ->
        if (isSuccessful) {
            lastSelectedOption.value = SIGN_OUT
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
        tabs[page].screen(appNavController, tabs[page].mediaType, tabs[page].listFetchFun, tabs[page].listType)
    }
}