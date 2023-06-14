package com.owenlejeune.tvtime.ui.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.PeopleService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailPerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.PersonCreditsResponse
import com.owenlejeune.tvtime.ui.components.ContentCard
import com.owenlejeune.tvtime.ui.components.DetailHeader
import com.owenlejeune.tvtime.ui.components.ExpandableContentCard
import com.owenlejeune.tvtime.ui.components.TwoLineImageTextCard
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun PersonDetailScreen(
    appNavController: NavController,
    personId: Int?
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    val person = remember { mutableStateOf<DetailPerson?>(null) }
    personId?.let {
        if (person.value == null) {
            fetchPerson(personId, person)
        }
    }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val topAppBarScrollState = rememberTopAppBarScrollState()
    val scrollBehaviour = remember(decayAnimationSpec) {
        TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            SmallTopAppBar(
                scrollBehavior = scrollBehaviour,
                colors = TopAppBarDefaults
                    .smallTopAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                title = { Text(text = person.value?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = { appNavController.popBackStack() }) {
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
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .verticalScroll(state = rememberScrollState())
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailHeader(
                    posterUrl = TmdbUtils.getFullPersonImagePath(person.value?.profilePath),
                    posterContentDescription = person.value?.profilePath
                )

                BiographyCard(person = person.value)

                val credits = remember { mutableStateOf<PersonCreditsResponse?>(null) }
                personId?.let {
                    if (credits.value == null) {
                        fetchCredits(personId, credits)
                    }
                }

                ContentCard(
                    title = stringResource(R.string.known_for_label)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(credits.value?.cast?.size ?: 0) { i ->
                            val content = credits.value!!.cast[i]

                            TwoLineImageTextCard(
                                title = content.name,
                                titleTextColor = MaterialTheme.colorScheme.primary,
                                subtitle = content.character,
                                modifier = Modifier
                                    .width(124.dp)
                                    .wrapContentHeight(),
                                imageUrl = TmdbUtils.getFullPosterPath(content.posterPath),
                                onItemClicked = {
                                    personId?.let {
                                        appNavController.navigate(
                                            AppNavItem.DetailView.withArgs(content.mediaType, content.id)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                val departments = credits.value?.crew?.map { it.department }?.toSet() ?: emptySet()
                if (departments.isNotEmpty()) {
                    ContentCard(title = stringResource(R.string.also_known_for_label)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            departments.forEach { department ->
                                Text(text = department, color = MaterialTheme.colorScheme.primary)
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val jobsInDepartment =
                                        credits.value!!.crew.filter { it.department == department }
                                    items(jobsInDepartment.size) { i ->
                                        val content = jobsInDepartment[i]
                                        val title = if (content.mediaType == MediaViewType.MOVIE) {
                                            content.title ?: ""
                                        } else {
                                            content.name ?: ""
                                        }
                                        TwoLineImageTextCard(
                                            title = title,
                                            subtitle = content.job,
                                            modifier = Modifier
                                                .width(124.dp)
                                                .wrapContentHeight(),
                                            imageUrl = TmdbUtils.getFullPosterPath(content.posterPath),
                                            onItemClicked = {
                                                personId?.let {
                                                    appNavController.navigate(
                                                        AppNavItem.DetailView.withArgs(content.mediaType, content.id)
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BiographyCard(person: DetailPerson?) {
    ExpandableContentCard { isExpanded ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp),
            text = person?.biography ?: "",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun fetchPerson(id: Int, person: MutableState<DetailPerson?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = PeopleService().getPerson(id)
        if (result.isSuccessful) {
            withContext(Dispatchers.Main) {
                person.value = result.body()
            }
        }
    }
}

private fun fetchCredits(id: Int, credits: MutableState<PersonCreditsResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = PeopleService().getCredits(id)
        if (result.isSuccessful) {
            withContext(Dispatchers.Main) {
                credits.value = result.body()
            }
        }
    }
}