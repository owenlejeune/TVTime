package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog

@Composable
fun TopAppBarDropdownMenu(
    icon: @Composable () -> Unit = {},
    content: @Composable ColumnScope.(expanded: MutableState<Boolean>) -> Unit = {}
) {
    val expanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = {
                expanded.value = true
            }
        ) {
            icon()
        }
    }
    
    DropdownMenu(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceVariant),
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        content(this, expanded)
    }
}

@Composable
fun TopAppBarDialogMenu(
    icon: @Composable () -> Unit = {},
    content: @Composable (showing: MutableState<Boolean>) -> Unit = {}
) {
    val expanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = {
                expanded.value = true
            }
        ) {
            icon()
        }
    }

    if (expanded.value) {
        Dialog(
            onDismissRequest = { expanded.value = false },
            content = { content(expanded) }
        )
//        AlertDialog(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight(),
//            backgroundColor = MaterialTheme.colorScheme.background,
//            onDismissRequest = { expanded.value = false },
//            text = { content(expanded) },
//            buttons = {}
//        )
    }
}