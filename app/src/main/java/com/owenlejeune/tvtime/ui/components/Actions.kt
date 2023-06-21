package com.owenlejeune.tvtime.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.theme.FavoriteSelected
import com.owenlejeune.tvtime.ui.theme.RatingSelected
import com.owenlejeune.tvtime.ui.theme.WatchlistSelected
import com.owenlejeune.tvtime.ui.theme.actionButtonColor
import com.owenlejeune.tvtime.ui.viewmodel.AccountViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.launch
import java.text.DecimalFormat

enum class Actions {
    RATE,
    WATCHLIST,
    LIST,
    FAVORITE
}

@Composable
fun ActionsView(
    itemId: Int,
    type: MediaViewType,
    actions: List<Actions> = listOf(Actions.RATE, Actions.WATCHLIST, Actions.LIST, Actions.FAVORITE),
    modifier: Modifier = Modifier
) {
    val accountViewModel = viewModel<AccountViewModel>()
    val mainViewModel = viewModel<MainViewModel>()

    LaunchedEffect(Unit) {
        mainViewModel.getAccountStates(itemId, type)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (actions.contains(Actions.RATE)) {
            RateButton(
                itemId = itemId,
                type = type,
                mainViewModel = mainViewModel
            )
        }
        if (actions.contains(Actions.WATCHLIST)) {
            WatchlistButton(
                itemId = itemId,
                type = type,
                accountViewModel = accountViewModel,
                mainViewModel = mainViewModel
            )
        }
        if (actions.contains(Actions.FAVORITE)) {
            FavoriteButton(
                itemId = itemId,
                type = type,
                accountViewModel = accountViewModel,
                mainViewModel = mainViewModel
            )
        }
        if (actions.contains(Actions.LIST)) {
            ListButton(
                itemId = itemId,
                type = type
            )
        }
    }
}

@Composable
fun ActionButton(
    imageVector: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    filledIconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = MaterialTheme.colorScheme.background
    val tintColor = remember { Animatable(bgColor) }
    LaunchedEffect(isSelected) {
        val target = if (isSelected) filledIconColor else bgColor
        tintColor.animateTo(targetValue = target, animationSpec = tween(300))
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .height(40.dp)
            .requiredWidthIn(min = 40.dp)
            .background(color = MaterialTheme.colorScheme.actionButtonColor)
            .clickable(onClick = onClick)
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.Center),
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tintColor.value
        )
    }
}

@Composable
private fun RateButton(
    itemId: Int,
    type: MediaViewType,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val accountStates = remember { mainViewModel.produceAccountStatesFor(type) }
    val itemIsRated = accountStates[itemId]?.isRated ?: false

    val showRatingDialog = remember { mutableStateOf(false) }

    ActionButton(
        imageVector = Icons.Filled.Star,
        contentDescription = "",
        isSelected = itemIsRated,
        filledIconColor = RatingSelected,
        onClick = { showRatingDialog.value = true },
        modifier = modifier
    )

    val userRating = accountStates[itemId]?.rating?.times(2)?.toFloat() ?: 0f
    RatingDialog(
        showDialog = showRatingDialog,
        rating = userRating,
        onValueConfirmed = { rating ->
            if (rating > 0f) {
                scope.launch { mainViewModel.postRating(itemId, rating, type) }
            } else {
                scope.launch { mainViewModel.deleteRating(itemId, type) }
            }
        }
    )
}

@Composable
fun WatchlistButton(
    itemId: Int,
    type: MediaViewType,
    accountViewModel: AccountViewModel,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val accountStates = remember { mainViewModel.produceAccountStatesFor(type) }
    val itemIsWatchlisted = accountStates[itemId]?.isWatchListed ?: false

    ActionButton(
        modifier = modifier,
        imageVector = Icons.Filled.Bookmark,
        contentDescription = "",
        isSelected = itemIsWatchlisted,
        filledIconColor = WatchlistSelected,
        onClick = {
            scope.launch {
                accountViewModel.addToWatchlist(type, itemId, !itemIsWatchlisted)
                mainViewModel.getAccountStates(itemId, type)
            }
        }
    )
}

@Composable
fun ListButton(
    itemId: Int,
    type: MediaViewType,
    modifier: Modifier = Modifier
) {
    CircleBackgroundColorImage(
        modifier = modifier.clickable(
            onClick = {}
        ),
        size = 40.dp,
        backgroundColor = MaterialTheme.colorScheme.actionButtonColor,
        image = Icons.Filled.List,
        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.background),
        contentDescription = ""
    )
}

@Composable
fun FavoriteButton(
    itemId: Int,
    type: MediaViewType,
    accountViewModel: AccountViewModel,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val accountStates = remember { mainViewModel.produceAccountStatesFor(type) }
    val itemIsFavorited = accountStates[itemId]?.isFavorite ?: false

    ActionButton(
        modifier = modifier,
        imageVector = Icons.Filled.Favorite,
        contentDescription = "",
        isSelected = itemIsFavorited,
        filledIconColor = FavoriteSelected,
        onClick = {
            scope.launch {
                accountViewModel.addToFavourites(type, itemId, !itemIsFavorited)
                mainViewModel.getAccountStates(itemId, type)
            }
        }
    )
}