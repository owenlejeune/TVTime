package com.owenlejeune.tvtime.ui.screens.tabs.bottom

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.ui.navigation.AccountTabNavItem
import com.owenlejeune.tvtime.ui.navigation.ListFetchFun
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import com.owenlejeune.tvtime.ui.screens.tabs.top.Tabs
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AccountTab(appNavController: NavHostController, appBarTitle: MutableState<String>) {
    if (SessionManager.currentSession.isGuest) {
        appBarTitle.value = "Hello, Guest"
    }

    val tabs = if (SessionManager.currentSession.isGuest) {
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

@Composable
fun AccountTabContent(
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    listFetchFun: ListFetchFun
) {
    val contentItems = listFetchFun()

    LazyColumn(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        items(contentItems.size) { i ->
            val ratedItem = contentItems[i]

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
                    Text(text = ratedItem.title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)

                    Text(text = ratedItem.releaseDate, color = MaterialTheme.colorScheme.onBackground)

                    Text(text = "Rating: ${(ratedItem.rating*10).toInt()}%", color = MaterialTheme.colorScheme.onBackground)
                }
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
        tabs[page].screen(appNavController, tabs[page].mediaType, tabs[page].listFetchFun)
    }
}