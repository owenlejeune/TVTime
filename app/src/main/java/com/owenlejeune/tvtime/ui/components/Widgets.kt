package com.owenlejeune.tvtime.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

sealed class ChipStyle(val mainAxisSpacing: Dp, val crossAxisSpacing: Dp) {
    object Boxy: ChipStyle(8.dp, 4.dp)
    object Rounded: ChipStyle(4.dp, 4.dp)
    class Mixed(val predicate: (String) -> ChipStyle): ChipStyle(8.dp, 4.dp)
}

@Composable
fun BoxyChip(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    isSelected: Boolean = true,
    onSelectionChanged: (String) -> Unit = {}
) {
    Surface(
//        modifier = Modifier.padding(4.dp),
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
fun RoundedChip(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    isSelected: Boolean = false,
    onSelectionChanged: (String) -> Unit = {}
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val radius = style.fontSize.value.dp * 2
    Surface(
        border = BorderStroke(width = 1.dp, borderColor),
        shape = RoundedCornerShape(radius),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .toggleable(
                    value = isSelected,
                    onValueChange = {
                        onSelectionChanged(text)
                    }
                )
                .padding(8.dp)
        ) {
            Text(
                text = text,
                style = style,
                color = if (isSelected) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    chips: List<String> = emptyList(),
    onSelectedChanged: (String) -> Unit = {},
    chipStyle: ChipStyle = ChipStyle.Boxy
) {

    @Composable
    fun DrawChip(chipStyle: ChipStyle, chip: String) {
        when (chipStyle) {
            ChipStyle.Boxy -> {
                BoxyChip(
                    text = chip,
                    onSelectionChanged = onSelectedChanged
                )
            }
            ChipStyle.Rounded -> {
                RoundedChip(
                    text = chip,
                    onSelectionChanged = onSelectedChanged
                )
            }
            is ChipStyle.Mixed -> {
                DrawChip(chipStyle = chipStyle.predicate(chip), chip = chip)
            }
        }
    }

    FlowRow(
        modifier = modifier,
        crossAxisSpacing = 4.dp,
        mainAxisSpacing = chipStyle.mainAxisSpacing
    ) {
        chips.forEach { chip ->
            DrawChip(chipStyle = chipStyle, chip = chip)
        }
    }
}

@Preview
@Composable
fun ChipPreview() {
    BoxyChip("Test Chip")
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
            Modifier
                .align(imageAlignment)
                .height(height = imageHeight)
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
        val text = if (author.name.isNotEmpty()) author.name[0] else author.username[0]
        RoundedLetterImage(
            size = size,
            character = text
        )
//        Box(
//            modifier = Modifier
//                .clip(CircleShape)
//                .size(size)
//                .background(color = MaterialTheme.colorScheme.tertiary)
//        ) {
//            Text(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(top = size / 5),
//                text = if (author.name.isNotEmpty()) author.name[0].uppercase() else author.username[0].toString(),
//                color = MaterialTheme.colorScheme.onTertiary,
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.titleLarge
//            )
//        }
    }
}

@Composable
fun RoundedLetterImage(
    size: Dp,
    character: Char,
    modifier: Modifier = Modifier,
    topPadding: Dp = size / 5
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(size)
            .background(color = MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding),
            text = character.uppercase(),
            color = MaterialTheme.colorScheme.onTertiary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun ThemedOutlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = androidx.compose.material.LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = androidx.compose.material.MaterialTheme.shapes.small
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = {
                if (isError) {
                    Icon(Icons.Filled.Error, "error", tint = MaterialTheme.colorScheme.error)
                } else {
                    trailingIcon?.invoke()
                }
            },
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onBackground,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorCursorColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun PasswordOutlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = androidx.compose.material.LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = androidx.compose.material.MaterialTheme.shapes.small
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    ThemedOutlineTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        isError = isError,
        errorMessage = errorMessage,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }
            val description = if (passwordVisible) "Hide password" else "Show password"
            IconButton(onClick = { passwordVisible = !passwordVisible } ) {
                Icon(imageVector = image, contentDescription = description)
            }
        }
    )
}

@Composable
fun LinkableText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(0xFF64B5F6),
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = TextDecoration.Underline,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        style = style
    )
}