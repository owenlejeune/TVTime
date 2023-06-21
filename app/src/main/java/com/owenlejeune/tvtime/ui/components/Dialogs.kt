package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.owenlejeune.tvtime.R
import java.text.DecimalFormat

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
        var sliderPosition by remember { mutableStateOf(rating) }
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