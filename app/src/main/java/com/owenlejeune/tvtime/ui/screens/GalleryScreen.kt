package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.components.PosterItem
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryView(
    id: Int,
    type: MediaViewType,
    appNavController: NavController
) {
    val applicationViewModel = viewModel<ApplicationViewModel>()
    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults
                    .topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                title = { Text(text = "Images") },
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
            when (type) {
                MediaViewType.PERSON -> PersonGalleryView(id = id)
                MediaViewType.TV,
                    MediaViewType.MOVIE -> MediaGalleryView(id = id, type = type)
                else -> {}
            }
        }
    }
}

@Composable
private fun MediaGalleryView(
    id: Int,
    type: MediaViewType
) {
    // todo - add support for images
}

@Composable
private fun PersonGalleryView(
    id: Int
) {
    val mainViewModel = viewModel<MainViewModel>()

    val imagesMap = remember { mainViewModel.peopleImagesMap }
    val images = imagesMap[id] ?: emptyList()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(images) { image ->
            PosterItem(
                width = 120.dp,
                url = TmdbUtils.getFullPersonImagePath(image.filePath),
                placeholder = Icons.Filled.Person,
                title = ""
            )
        }
    }
}