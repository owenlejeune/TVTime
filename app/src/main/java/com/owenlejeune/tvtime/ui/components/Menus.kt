package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
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
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background),
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        content(this, expanded)
    }
}

@Composable
fun CustomTopAppBarDropdownMenu(
    icon: @Composable () -> Unit = {},
    content: @Composable ColumnScope.(expanded: MutableState<Boolean>) -> Unit = {}
) {
    val expanded = remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded.value = true }) {
            icon()
        }
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false},
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .shadow(elevation = 0.dp),
        offset = DpOffset(16.dp, 0.dp)
    ) {
        content(this, expanded)
    }
}

@Composable
fun CustomMenuItem(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .clickable(onClick = onClick)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CustomMenuDivider() {
    Divider(color = Color.Transparent, modifier = Modifier.padding(vertical = 2.dp))
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