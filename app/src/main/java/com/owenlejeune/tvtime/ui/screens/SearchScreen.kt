package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType

@Composable
fun SearchScreen(
    appNavController: NavHostController,
    title: String,
    mediaViewType: MediaViewType
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        var searchValue by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }

        SmallTopAppBar(
            modifier = Modifier,
            title = {
                TextField(
                    value = searchValue,
                    onValueChange = { searchValue = it },
                    placeholder = { Text(text = stringResource(id = R.string.search_placeholder, title)) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                )
            },
            navigationIcon = {
                IconButton(onClick = { appNavController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.content_description_back_button)
                    )
                }
            }
        )

        LaunchedEffect(key1 = "") {
            focusRequester.requestFocus()
        }
    }
}