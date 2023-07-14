package com.owenlejeune.tvtime.ui.components

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreRouteButton() {
    val applicationViewModel = viewModel<ApplicationViewModel>()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    applicationViewModel.setStoredRoute(applicationViewModel.currentRoute.value)
                    Toast.makeText(context, "Stored route \"${applicationViewModel.storedRoute.value}\"", Toast.LENGTH_SHORT).show()
                },
                onLongClick = {
                    applicationViewModel.setStoredRoute("")
                    Toast.makeText(context, "Stored route \"\"", Toast.LENGTH_SHORT).show()
                }
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.Upload,
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NavStoredRouteButton(appNavController: NavController) {
    val applicationViewModel = viewModel<ApplicationViewModel>()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .clickable {
                val route = applicationViewModel.storedRoute.value
                if (route.isNotEmpty()) {
                    Toast.makeText(context, "Navigating to \"$route\"", Toast.LENGTH_SHORT).show()
                    appNavController.navigate(route)
                }
            }
    ) {
        Icon(
            imageVector = Icons.Outlined.Reply,
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun BackButton(navController: NavController) {
    IconButton(
        onClick = { navController.popBackStack() }
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.content_description_back_button),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}