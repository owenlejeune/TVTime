package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.extensions.lazyPagingItems
import com.owenlejeune.tvtime.ui.components.MediaResultCard
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordResultsScreen(
    type: MediaViewType,
    keyword: String,
    id: Int,
    appNavController: NavController
) {
    val mainViewModel = viewModel<MainViewModel>()

    LaunchedEffect(Unit) {
        mainViewModel.getKeywordResults(id, keyword, type)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = keyword.capitalize(Locale.current)) },
                navigationIcon = {
                    IconButton(
                        onClick = { appNavController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val keywordResultsMap = remember { mainViewModel.produceKeywordsResultsFor(type) }
            val keywordResults = keywordResultsMap[id]

            val pagingResults = keywordResults?.collectAsLazyPagingItems()
            LazyColumn(
                modifier = Modifier.padding(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pagingResults?.let {
                    lazyPagingItems(it) { result ->
                        result?.let {
                            MediaResultCard(
                                appNavController = appNavController,
                                mediaViewType = type,
                                id = result.id,
                                backdropPath = TmdbUtils.getFullBackdropPath(result.backdropPath),
                                posterPath = TmdbUtils.getFullPosterPath(result.posterPath),
                                title = result.title,
                                additionalDetails = listOf(result.overview)
                            )
                        }
                    }
                }
            }
        }
    }
}