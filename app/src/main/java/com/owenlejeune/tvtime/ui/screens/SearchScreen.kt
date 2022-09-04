package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun SearchScreen(
    appNavController: NavHostController
) {
    Column(
        modifier = Modifier
            .clickable(
                onClick = {
                    appNavController.popBackStack()
                }
            )
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Search"
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}