package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.ui.components.PosterGrid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TvTab(appNavController: NavController) {
    PosterGrid(appNavController = appNavController) { tvList ->
        val service = TvService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getPopularTv()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    tvList.value = response.body()!!.tv
                }
            }
        }
    }
}