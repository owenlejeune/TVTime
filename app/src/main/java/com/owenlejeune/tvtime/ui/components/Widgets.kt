package com.owenlejeune.tvtime.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.flowlayout.FlowRow
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.model.AuthorDetails
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

@Composable
fun TopLevelSwitch(
    text: String,
    checkedState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onCheckChanged: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(12.dp),
        shape = RoundedCornerShape(30.dp),
        backgroundColor = when {
            isSystemInDarkTheme() && checkedState.value -> MaterialTheme.colorScheme.primary
            isSystemInDarkTheme() && !checkedState.value -> MaterialTheme.colorScheme.secondary
            checkedState.value -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                color = when {
                    isSystemInDarkTheme() && checkedState.value -> MaterialTheme.colorScheme.onPrimary
                    isSystemInDarkTheme() && !checkedState.value -> MaterialTheme.colorScheme.onSecondary
                    checkedState.value -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                },
                modifier = Modifier.padding(30.dp, 12.dp),
                fontSize = 18.sp
            )
            CustomSwitch(
                modifier = Modifier.padding(40.dp, 12.dp),
                checked = checkedState.value,
                onCheckedChange = { isChecked ->
                    checkedState.value = isChecked
                    onCheckChanged(isChecked)
                }
            )
        }
    }
}

@Composable
fun CustomSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {},
    width: Dp = 30.dp,
    height: Dp = 15.dp,
    colors: CustomSwitchColors = CustomSwitchColors.topLevelColors(),
    enabled: Boolean = true
) {
    val checkedState = remember { mutableStateOf(checked) }

    val gapBetweenThumbAndTrackEdge = 2.dp

    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge
    val animatePosition = animateFloatAsState(
        targetValue = if (checkedState.value) {
            with(LocalDensity.current) { (width - thumbRadius - gapBetweenThumbAndTrackEdge).toPx() }
        } else {
            with (LocalDensity.current) { (thumbRadius + gapBetweenThumbAndTrackEdge).toPx() }
        }
    )

    val uncheckedTrackColor = if (enabled) {
        if (isSystemInDarkTheme()) colors.darkUncheckedTrackColor else colors.lightUncheckedTrackColor
    } else {
        if (isSystemInDarkTheme()) colors.darkDisabledTrackColor else colors.lightDisabledTrackColor
    }
    val uncheckedThumbColor = if (enabled) {
        if (isSystemInDarkTheme()) colors.darkUncheckedThumbColor else colors.lightUncheckedThumbColor
    } else {
        if (isSystemInDarkTheme()) colors.darkDisabledThumbColor else colors.lightDisabledThumbColor
    }
    val checkedTrackColor = if (enabled) {
        if (isSystemInDarkTheme()) colors.darkCheckedTrackColor else colors.lightCheckedTrackColor
    } else {
        if (isSystemInDarkTheme()) colors.darkDisabledTrackColor else colors.lightDisabledTrackColor
    }
    val checkedThumbColor = if (enabled) {
        if (isSystemInDarkTheme()) colors.darkCheckedThumbColor else colors.lightCheckedThumbColor
    } else {
        if (isSystemInDarkTheme()) colors.darkDisabledThumbColor else colors.lightDisabledThumbColor
    }

    Canvas(
        modifier = modifier
            .size(width = width, height = height)
            .scale(scale = 2f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        checkedState.value = !checkedState.value
                        onCheckedChange(checkedState.value)
                    }
                )
            }
    ) {
        drawRoundRect(
            color = if (checkedState.value) checkedTrackColor else uncheckedTrackColor,
            cornerRadius = CornerRadius(x = 10.dp.toPx(), y = 10.dp.toPx())
        )
        drawCircle(
            color = if (checkedState.value) checkedThumbColor else uncheckedThumbColor,
            radius = thumbRadius.toPx(),
            center = Offset(
                x = animatePosition.value,
                y = size.height / 2
            )
        )
    }
}

@Preview(name = "TopLevelSwitch", showBackground = true)
@Preview(name = "Dark TopLevelSwitch", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TopLevelSwitchPreview() {
    val context = LocalContext.current
    TopLevelSwitch("This is a switch") { isChecked ->
        Toast.makeText(context, "Switch changed to $isChecked", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun SearchFab(
    focusSearchBar: MutableState<Boolean> = mutableStateOf(false),
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val context = LocalContext.current
    FloatingActionButton(onClick = {
        focusSearchBar.value = true
//        focusRequester.requestFocus()
//        Toast.makeText(context, "Search Clicked!", Toast.LENGTH_SHORT).show()
    }) {
        Icon(Icons.Filled.Search, "")
    }
}

@Preview
@Composable
fun SearchFabPreview() {
    SearchFab()
}

@Composable
fun MinLinesText(
    text: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val lineHeight = style.fontSize*4/3

    Text(
        modifier = modifier
            .sizeIn(
                minHeight = with(LocalDensity.current) {
                    (lineHeight * minLines).toDp()
                }
            ),
        text = text,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        style = style
    )
}

@Composable
fun Chip(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    isSelected: Boolean = true,
    onSelectionChanged: (String) -> Unit = {}
) {
    Surface(
        modifier = Modifier.padding(4.dp),
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(5.dp),
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.secondary
    ) {
        Row(
            modifier = Modifier
                .toggleable(
                    value = isSelected,
                    onValueChange = {
                        onSelectionChanged(text)
                    }
                )
        ) {
            Text(
                text = text,
                style = style,
                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    chips: List<String> = emptyList(),
    onSelectedChanged: (String) -> Unit = {},
) {
    FlowRow(
        modifier = modifier
    ) {
        chips.forEach { chip ->
            Chip(
                text = chip,
                onSelectionChanged = onSelectedChanged
            )
        }
    }
}

@Preview
@Composable
fun ChipPreview() {
    Chip("Test Chip")
}

/**
 * @param progress The progress of the ring as a value between 0 and 1
 */
@Composable
fun RatingRing(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    size: Dp = 60.dp,
    ringStrokeWidth: Dp = 4.dp,
    ringColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.White,
    textSize: TextUnit = 14.sp
) {
    Box(
        modifier = modifier
            .size(size)
//            .size(60.dp)
//            .padding(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = progress,
            strokeWidth = ringStrokeWidth,
            color = ringColor
        )

        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "${(progress*100).toInt()}%",
            color = textColor,
            fontSize = textSize
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    requestFocus: Boolean = false,
    placeHolder: String = "",
    placeHolderTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    cursorColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        color = backgroundColor
    ) {
        Row(Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (leadingIcon != null) {
                leadingIcon()
            }
            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty() && placeHolder.isNotEmpty()) {
                    Text(
                        text = placeHolder,
                        style = textStyle,
                        color = placeHolderTextColor
                    )
                }
                val bringIntoViewRequester = remember { BringIntoViewRequester() }
                val coroutineScope = rememberCoroutineScope()
                BasicTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent {
                            if (it.isFocused) {
                                coroutineScope.launch {
                                    delay(200)
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        },
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    textStyle = textStyle.copy(color = textColor),
                    cursorBrush = SolidColor(cursorColor),
                    maxLines = maxLines,
                    enabled = enabled,
                    readOnly = readOnly,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                )
            }
            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }

    if (requestFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview(widthDp = 300, heightDp = 40)
@Composable
private fun RoundedEditTextPreview() {
    RoundedTextField(
        value = "this is my value",
        onValueChange = {},
        placeHolder = "this is my placeholder",
        trailingIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = ""
            )
       },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = ""
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullScreenThumbnailVideoPlayer(
    key: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val showFullscreenView = remember { mutableStateOf(false) }

    Image(
        modifier = modifier
            .clickable(
                onClick = {
                    showFullscreenView.value = true
                }
            ),
        painter = rememberImagePainter(
            data = "https://img.youtube.com/vi/${key}/hqdefault.jpg",
            builder = {
                placeholder(R.drawable.placeholder)
            }
        ),
        contentDescription = ""
    )

    if (showFullscreenView.value) {
        Dialog(
            onDismissRequest = { showFullscreenView.value = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()) {
                AndroidView(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    factory = {
                        YouTubePlayerView(context).apply {
                            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.loadVideo(key, 0f)
                                }
                            })
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HtmlText(text: String, modifier: Modifier = Modifier, color: Color = Color.Unspecified, parseMarkdownFirst: Boolean = true) {
    val htmlString = if (parseMarkdownFirst) {
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(text)
        HtmlGenerator(text, parsedTree, flavour).generateHtml()
    } else {
        text
    }
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context) },
        update = { textView ->
            textView.textSize = 14f
            textView.text = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT)
            textView.setTextColor(color.toArgb())
        }
    )
}

@Composable
fun CircleBackgroundColorImage(
    size: Dp,
    backgroundColor: Color,
    image: ImageVector,
    modifier: Modifier = Modifier,
    imageHeight: Dp? = null,
    imageAlignment: Alignment = Alignment.Center,
    contentDescription: String? = null,
    colorFilter: ColorFilter? = null
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(size)
            .background(color = backgroundColor)
    ) {
        val mod = if (imageHeight != null) {
            Modifier.align(imageAlignment).height(height = imageHeight)
        } else {
            Modifier.align(imageAlignment)
        }
        Image(
            imageVector = image,
            contentDescription = contentDescription,
            modifier = mod,
            colorFilter = colorFilter
        )
    }
}

@Composable
fun AvatarImage(
    size: Dp,
    author: AuthorDetails,
    modifier: Modifier = Modifier
) {
    if (author.avatarPath != null) {
        Image(
            modifier = modifier.size(size),
            painter = rememberImagePainter(
                data = TmdbUtils.getFullAvatarPath(author),
                builder = {
                    transformations(CircleCropTransformation())
                }
            ),
            contentDescription = ""
        )
    } else {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(size)
                .background(color = MaterialTheme.colorScheme.tertiary)
        ) {
            Text(
                modifier = Modifier.fillMaxSize().padding(top = size/5),
                text = if (author.name.isNotEmpty()) author.name[0].uppercase() else author.username[0].toString(),
                color = MaterialTheme.colorScheme.onTertiary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}