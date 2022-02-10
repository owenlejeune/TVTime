package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MoviesTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Movies Tab",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TvTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "TV Tab",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun FavouritesTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Favourites Tab",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SettingsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Settings Tab",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}