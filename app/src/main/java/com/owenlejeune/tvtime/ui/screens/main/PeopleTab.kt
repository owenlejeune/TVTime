package com.owenlejeune.tvtime.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.components.PagingPeoplePosterGrid
import com.owenlejeune.tvtime.ui.components.SearchView
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.viewmodel.PeopleTabViewModel

@Composable
fun PeopleTab(
    appBarTitle: MutableState<String>,
    appNavController: NavHostController,
    fab: MutableState<@Composable () -> Unit>
) {
    appBarTitle.value = stringResource(id = R.string.nav_people_title)

    Column {
        SearchView(
            title = appBarTitle.value,
            appNavController = appNavController,
            mediaType = MediaViewType.PERSON,
            fab = fab
        )

        val peopleList = PeopleTabViewModel().popularPeople.collectAsLazyPagingItems()

        PagingPeoplePosterGrid(
            lazyPagingItems = peopleList,
            header = {
                Text(
                    text = stringResource(R.string.popular_today_header),
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            onClick = { id ->
                appNavController.navigate(
                    "${MainNavItem.DetailView.route}/${MediaViewType.PERSON}/${id}"
                )
            }
        )
    }
}