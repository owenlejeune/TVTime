package com.owenlejeune.tvtime.ui.screens.main

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.utils.FileUtils
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutView(
    appNavController: NavController
) {
    val context = LocalContext.current

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val topAppBarScrollState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec, topAppBarScrollState)
    }
    Scaffold(
        modifier = Modifier.nestedScroll(connection = scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.nav_about_title)) },
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
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                AboutItem(
                    title = "App Info",
                    subtitle = "v${BuildConfig.VERSION_NAME}",
                    icon = Icons.Outlined.Info,
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            val uri = Uri.fromParts("package", context.packageName, null)
                            data = uri
                        }
                        context.startActivity(intent)
                    }
                )

                var showChangeLog by remember { mutableStateOf(false) }
                AboutItem(
                    title = "Changelog",
                    icon = Icons.Outlined.Description,
                    onClick = { showChangeLog = true }
                )
                ChangeLogDialog(
                    visible = showChangeLog,
                    onDismissRequest = { showChangeLog = false }
                )
            }
        }
    }
}

@Composable
private fun AboutItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                onClick = onClick ?: {},
                enabled = onClick != null
            ),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = subtitle,
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        ) {
            val titleColor = MaterialTheme.colorScheme.onBackground
            val subtitleColor = MaterialTheme.colorScheme.onSurfaceVariant
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = titleColor, fontSize = 20.sp)
            subtitle?.let {
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = subtitleColor)
            }
        }
    }
}

@Composable
private fun ChangeLogDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit
) {
    if (visible) {
        val changeLog = FileUtils.getRawResourceFileAsString(LocalContext.current, R.raw.changelog)

        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(
                    onClick = onDismissRequest
                ) {
                    Text(text = "Close")
                }
            },
            text = {
                MarkdownText(markdown = changeLog)
            }
        )
    }
}