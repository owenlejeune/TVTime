package com.owenlejeune.tvtime.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v4.ListV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AccountList
import com.owenlejeune.tvtime.extensions.lazyPagingItems
import com.owenlejeune.tvtime.ui.viewmodel.AccountViewModel
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.launch
import java.text.DecimalFormat

private const val TAG = "Dialogs"

@Composable
fun RatingDialog(
    showDialog: MutableState<Boolean>,
    rating: Float,
    onValueConfirmed: (Float) -> Unit
) {
    val formatPosition: (Float) -> String = { position ->
        DecimalFormat("#.#").format(position.toInt()*5/10f)
    }

    if (showDialog.value) {
        var sliderPosition by remember { mutableFloatStateOf(rating) }
        val formatted = formatPosition(sliderPosition).toFloat()
        AlertDialog(
            modifier = Modifier.wrapContentHeight(),
            onDismissRequest = { showDialog.value = false },
            title = {
                if (rating > 0f) {
                    Text(text = stringResource(id = R.string.my_rating_dialog_title))
                } else {
                    Text(text = stringResource(R.string.rating_dialog_title))
                }
            },
            confirmButton = {
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        onValueConfirmed.invoke(formatted)
                        showDialog.value = false
                    }
                ) {
                    Text(
                        text = if (formatted > 0f) {
                            stringResource(id = R.string.rating_dialog_confirm)
                        } else {
                            stringResource(id = R.string.rating_dialog_delete)
                        }
                    )
                }
            },
            dismissButton = {
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
            text = {
                SliderWithLabel(
                    value = sliderPosition,
                    valueRange = 0f..20f,
                    onValueChanged = {
                        sliderPosition = it
                    },
                    sliderLabel = "${sliderPosition.toInt() * 5}%"
                )
            }
        )
    }
}

@Composable
fun AddToListDialog(
    itemId: Int,
    itemType: MediaViewType,
    showDialog: MutableState<Boolean>
) {
    if (showDialog.value) {
        val scope = rememberCoroutineScope()

        val accountViewModel = viewModel<AccountViewModel>()
        val lists = accountViewModel.userLists.collectAsLazyPagingItems()

        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(text = stringResource(id = R.string.add_to_list_action_label))
            },
            text = {
                Column(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filter = remember { mutableStateOf("") }
                    RoundedTextField(
                        modifier = Modifier
                            .height(55.dp)
                            .padding(bottom = 12.dp),
                        value = filter.value,
                        onValueChange = { filter.value = it },
                        placeHolder = stringResource(id = R.string.search_placeholder, stringResource(id = R.string.lists)),
                        leadingIcon = {
                            Image(
                                imageVector = Icons.Filled.Search,
                                contentDescription = stringResource(R.string.search_icon_content_descriptor),
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        if (lists.itemCount == 0) {
                            item {
                                Spacer(modifier = Modifier.weight(1f))
                                Text(stringResource(id = R.string.no_lists_message))
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        lazyPagingItems(lists) { list ->
                            (list as AccountList).apply {
                                if (list.name.contains(filter.value)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                scope.launch {
                                                    accountViewModel.addToList(
                                                        listId = list.id,
                                                        itemId = itemId,
                                                        itemType = itemType
                                                    )
                                                }
                                            }
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.list_count_label, list.name, list.numberOfItems),
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(id = R.string.action_dismiss))
                }
            }
        )
    }
}