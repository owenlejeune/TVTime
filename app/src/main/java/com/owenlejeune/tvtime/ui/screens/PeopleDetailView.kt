package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.PeopleService
import com.owenlejeune.tvtime.api.tmdb.model.DetailPerson
import com.owenlejeune.tvtime.api.tmdb.model.PersonCreditsResponse
import com.owenlejeune.tvtime.ui.components.ContentCard
import com.owenlejeune.tvtime.ui.components.ExpandableContentCard
import com.owenlejeune.tvtime.ui.components.TwoLineImageTextCard
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PersonDetailView(
    appNavController: NavController,
    personId: Int?
) {
    val person = remember { mutableStateOf<DetailPerson?>(null) }
    personId?.let {
        if (person.value == null) {
            fetchPerson(personId, person)
        }
    }

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetailHeader(
            appNavController = appNavController,
            title = person.value?.name ?: "",
            posterUrl = TmdbUtils.getFullPersonImagePath(person.value?.profilePath),
            posterContentDescription = person.value?.name
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            BiographyCard(person = person.value)

            val credits = remember { mutableStateOf<PersonCreditsResponse?>(null) }
            personId?.let {
                if (credits.value == null) {
                    fetchCredits(personId, credits)
                }
            }

            ContentCard(title = stringResource(R.string.known_for_label)) {
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
                                        "${MainNavItem.DetailView.route}/${content.mediaType}/${content.id}"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            ContentCard(title = stringResource(R.string.also_known_for_label)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val departments = credits.value?.crew?.map { it.department }?.toSet() ?: emptySet()
                    if (departments.isNotEmpty()) {
                        departments.forEach { department ->
                            Text(text = department, color = MaterialTheme.colorScheme.primary)
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val jobsInDepartment = credits.value!!.crew.filter { it.department == department }
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
                                                    "${MainNavItem.DetailView.route}/${content.mediaType}/${content.id}"
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