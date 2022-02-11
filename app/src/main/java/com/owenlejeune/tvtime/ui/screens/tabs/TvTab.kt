package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.ui.components.PosterGrid

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TvTab(appNavController: NavController) {
    PosterGrid(appNavController = appNavController) { tvList ->
        val service = TvService()
        service.getPopularTv { isSuccessful, response ->
            if (isSuccessful) {
                tvList.value = response!!.tv
            }
        }
    }
}