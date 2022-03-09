package com.owenlejeune.tvtime.ui.screens.tabs.bottom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.model.RatedMovie
import com.owenlejeune.tvtime.api.tmdb.model.RatedTopLevelMedia
import com.owenlejeune.tvtime.api.tmdb.model.RatedTv
import com.owenlejeune.tvtime.ui.components.RoundedLetterImage
import com.owenlejeune.tvtime.ui.components.SignInDialog
import com.owenlejeune.tvtime.ui.components.TopAppBarDropdownMenu
import com.owenlejeune.tvtime.ui.navigation.AccountTabNavItem
import com.owenlejeune.tvtime.ui.navigation.ListFetchFun
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import com.owenlejeune.tvtime.ui.screens.tabs.top.Tabs
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val GUEST_SIGN_IN = "guest_sign_in"
private const val SIGN_OUT = "sign_out"
private const val ACCOUNT_SIGN_OUT = "account_sign_out"
private const val NO_SESSION_SIGN_IN = "no_session_sign_in"
private const val NO_SESSION_SIGN_IN_GUEST = "no_session_sign_in_guest"

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AccountTab(
    appNavController: NavHostController,
    appBarTitle: MutableState<String>,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({})
) {
    if (SessionManager.currentSession?.isAuthorized == false) {
        appBarTitle.value = stringResource(id = R.string.account_header_title_formatted).replace("%1\$s", stringResource(id = R.string.account_name_guest))
    } else {
        appBarTitle.value = stringResource(id = R.string.account_not_logged_in)
    }

    val lastSelectedOption = remember { mutableStateOf("") }

    appBarActions.value = {
        AccountDropdownMenu(session = SessionManager.currentSession, lastSelectedOption = lastSelectedOption)
    }

    if (lastSelectedOption.value.isNotBlank() || lastSelectedOption.value.isBlank()) {
        SessionManager.currentSession?.let { session ->
            val tabs = if (session.isAuthorized) {
                AccountTabNavItem.GuestItems
            } else {
                AccountTabNavItem.GuestItems
            }

            Column {
                val pagerState = rememberPagerState()
                Tabs(tabs = tabs, pagerState = pagerState)
                AccountTabs(
                    appNavController = appNavController,
                    tabs = tabs,
                    pagerState = pagerState
                )
            }
        }
    }
}

@Composable
fun AccountTabContent(
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    listFetchFun: ListFetchFun
) {
    val contentItems = listFetchFun()

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
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
                val ratedItem = contentItems[i] as RatedTopLevelMedia

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable(
                        onClick = {
                            appNavController.navigate(
                                "${MainNavItem.DetailView.route}/${mediaViewType}/${ratedItem.id}"
                            )
                        }
                    )
                ) {
                    Image(
                        modifier = Modifier
                            .width(60.dp)
                            .height(80.dp),
                        painter = rememberImagePainter(
                            data = TmdbUtils.getFullPosterPath(ratedItem.posterPath)
                        ),
                        contentDescription = ""
                    )

                    Column(
                        modifier = Modifier.height(80.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = ratedItem.name,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 18.sp
                        )

                        val date = when (ratedItem) {
                            is RatedMovie -> ratedItem.releaseDate
                            is RatedTv -> ratedItem.firstAirDate
                            else -> ""
                        }
                        Text(
                            text = date,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = stringResource(id = R.string.rating_test, (ratedItem.rating * 10).toInt()),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountDropdownMenu(
    session: SessionManager.Session?,
    lastSelectedOption: MutableState<String>
) {
    TopAppBarDropdownMenu(
        icon = {
            when(session?.isAuthorized) {
                true -> {  }
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
    DropdownMenuItem(
        onClick = {
            showSignInDialog.value = true
        },
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(text = stringResource(R.string.action_sign_in), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    if (showSignInDialog.value) {
        SignInDialog(showDialog = showSignInDialog) { success ->
            if (success) {
                lastSelectedOption.value = NO_SESSION_SIGN_IN
                expanded.value = false
            }
        }
    }

    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp))

    DropdownMenuItem(
        onClick = {
            createGuestSession(lastSelectedOption)
            expanded.value = false
        }
    ) {
        Text(text = stringResource(R.string.action_sign_in_as_guest), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun NoSessionAccountIcon() {
    Icon(
        modifier = Modifier
            .size(50.dp)
            .padding(end = 8.dp),
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
    DropdownMenuItem(
        onClick = {
            showSignInDialog.value = true
        },
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(text = stringResource(id = R.string.action_sign_in), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    if (showSignInDialog.value) {
        SignInDialog(showDialog = showSignInDialog) { success ->
            if (success) {
                lastSelectedOption.value = GUEST_SIGN_IN
                expanded.value = false
            }
        }
    }

    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp))

    DropdownMenuItem(
        onClick = {
            signOut(lastSelectedOption)
            expanded.value = false
        }
    ) {
        Text(text = stringResource(id = R.string.action_sign_out), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GuestSessionIcon() {
    val guestName = stringResource(id = R.string.account_name_guest)
    RoundedLetterImage(size = 40.dp, character = guestName[0], modifier = Modifier.padding(end = 8.dp), topPadding = 40.dp / 8)
}

@Composable
private fun AuthorizedSessionMenuItems(
    expanded: MutableState<Boolean>,
    lastSelectedOption: MutableState<String>
) {
    DropdownMenuItem(
        onClick = {
            lastSelectedOption.value = ACCOUNT_SIGN_OUT
            expanded.value = false
        }
    ) {
        Text(text = stringResource(id = R.string.action_sign_out), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        tabs[page].screen(appNavController, tabs[page].mediaType, tabs[page].listFetchFun)
    }
}