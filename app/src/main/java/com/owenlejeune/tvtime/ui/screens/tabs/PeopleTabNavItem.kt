package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.utils.ResourceUtils
import com.owenlejeune.tvtime.utils.types.TabNavItem
import org.koin.core.component.inject

sealed class PeopleTabNavItem(
    stringRes: Int,
    route: String,
    val screen: @Composable (NavController) -> Unit
): TabNavItem(route) {
    private val resourceUtils: ResourceUtils by inject()

    override val name: String = resourceUtils.getString(stringRes)

    companion object {
        val Items by lazy { listOf(Popular, Trending) }
    }

    object Popular: PeopleTabNavItem(
        stringRes = R.string.popular_today_header,
        route = "people_popular_route",
        screen = { PopularPeopleContent(appNavController = it) }
    )

    object Trending: PeopleTabNavItem(
        stringRes = R.string.nav_trending_title,
        route = "people_trending_route",
        screen = { TrendingPeopleContent(appNavController = it) }
    )
}
