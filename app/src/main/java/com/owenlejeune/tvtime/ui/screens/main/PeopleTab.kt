package com.owenlejeune.tvtime.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.PeopleService
import com.owenlejeune.tvtime.ui.components.PeoplePosterGrid
import com.owenlejeune.tvtime.ui.components.SearchView
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PeopleTab(
    appBarTitle: MutableState<String>,
    appNavController: NavHostController,
    fab: MutableState<@Composable () -> Unit>
) {
    appBarTitle.value = stringResource(id = R.string.nav_people_title)

    val service = PeopleService()

    Column {
        SearchView(
            title = appBarTitle.value,
            appNavController = appNavController,
            mediaType = MediaViewType.PERSON,
            fab = fab
        )

        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(id = R.string.nav_popular_people_title),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineLarge
        )

        PeoplePosterGrid(
            fetchPeople =  { peopleList ->
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.getPopular()
                    if (response.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            peopleList.value = response.body()?.results ?: emptyList()
                        }
                    }
                }
            },
            onClick = { id ->
                appNavController.navigate(
                    "${MainNavItem.DetailView.route}/${MediaViewType.PERSON}/${id}"
                )
            }
        )
    }
}