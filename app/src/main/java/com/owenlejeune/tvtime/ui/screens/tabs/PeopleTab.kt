package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.components.PagingPeoplePosterGrid
import com.owenlejeune.tvtime.ui.components.SearchView
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.viewmodel.HomeScreenViewModel
import com.owenlejeune.tvtime.ui.viewmodel.PeopleTabViewModel
import com.owenlejeune.tvtime.utils.types.MediaViewType

@Composable
fun PeopleTab(
    appNavController: NavHostController,
) {
    val homeScreenViewModel = viewModel<HomeScreenViewModel>()
    homeScreenViewModel.appBarTitle.value = stringResource(id = R.string.popular_today_header)

    Column {
        SearchView(
            title = stringResource(id = R.string.popular_today_header),
            appNavController = appNavController,
            mediaType = MediaViewType.PERSON
        )

        val peopleList = PeopleTabViewModel().popularPeople.collectAsLazyPagingItems()

        PagingPeoplePosterGrid(
            lazyPagingItems = peopleList,
            header = {
//                Text(
//                    text = stringResource(R.string.popular_today_header),
//                    modifier = Modifier.padding(start = 8.dp)
//                )
            },
            onClick = { id ->
                appNavController.navigate(
                    AppNavItem.DetailView.withArgs(MediaViewType.PERSON, id)
                )
            }
        )
    }
}