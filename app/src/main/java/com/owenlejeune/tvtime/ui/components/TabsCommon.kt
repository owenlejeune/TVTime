package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.owenlejeune.tvtime.utils.types.TabNavItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(
    tabs: List<TabNavItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    selectedTabTextColor: Color = MaterialTheme.colorScheme.primary,
    unselectedTabTextColor: Color = MaterialTheme.colorScheme.onBackground,
    tabTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    tabIndicatorColor: Color = MaterialTheme.colorScheme.primary
) {
    val scope = rememberCoroutineScope()
    
    TabRow(
        modifier = modifier,
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        indicator = { tabPositions ->
            SmallTabIndicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState = pagerState, tabPositions = tabPositions),
                color = tabIndicatorColor
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                text = {
                    Text(
                        text = tab.name,
                        style = tabTextStyle,
                        color = if (pagerState.currentPage == index) selectedTabTextColor else unselectedTabTextColor,
                        textAlign = TextAlign.Center
                    )
               },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScrollableTabs(
    tabs: List<TabNavItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    selectedTabTextColor: Color = MaterialTheme.colorScheme.primary,
    unselectedTabTextColor: Color = MaterialTheme.colorScheme.onBackground,
    tabTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    tabIndicatorColor: Color = MaterialTheme.colorScheme.primary
) {
    val scope = rememberCoroutineScope()

    ScrollableTabRow(
        modifier = modifier
            .fillMaxWidth(),
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        edgePadding = 8.dp,
        indicator = { tabPositions ->
            SmallTabIndicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState = pagerState, tabPositions = tabPositions),
                color = tabIndicatorColor
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                text = {
                    Text(
                        text = tab.name,
                        style = tabTextStyle,
                        color = if (pagerState.currentPage == index) selectedTabTextColor else unselectedTabTextColor
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@Composable
fun SmallTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Spacer(
        modifier
            .padding(horizontal = 28.dp)
            .height(2.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

