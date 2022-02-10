package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.ui.components.PosterGrid

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TvTab() {
    PosterGrid { tvList ->
        val service = TvService()
        service.getPopularTv { isSuccessful, response ->
            if (isSuccessful) {
                tvList.value = response!!.tv
            }
        }
    }
}