package com.owenlejeune.tvtime.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.TextView
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AccountCircle
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.flowlayout.FlowRow
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AuthorDetails
import com.owenlejeune.tvtime.extensions.toDp
import com.owenlejeune.tvtime.extensions.unlessEmpty
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import org.koin.java.KoinJavaComponent

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(12.dp)
            .background(
                color = when {
                    isSystemInDarkTheme() && checkedState.value -> MaterialTheme.colorScheme.primary
                    isSystemInDarkTheme() && !checkedState.value -> MaterialTheme.colorScheme.secondary
                    checkedState.value -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                }
            ),
        shape = RoundedCornerShape(30.dp)
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

@Composable
fun SearchView(
    title: String,
    appNavController: NavHostController,
    mediaType: MediaViewType,
    fab: MutableState<@Composable () -> Unit>,
    preferences: AppPreferences = KoinJavaComponent.get(AppPreferences::class.java)
) {
    val route = AppNavItem.SearchView.withArgs(mediaType, title)
    if (preferences.showSearchBar) {
        SearchBar(
            placeholder = title
        ) {
            appNavController.navigate(route)
        }
    } else {
        fab.value = @Composable {
            FloatingActionButton(
                onClick = {
                    appNavController.navigate(route)
                }
            ) {
                Icon(Icons.Filled.Search, stringResource(id = R.string.preference_heading_search))
            }
        }
    }
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

class ChipInfo(
    val text: String,
    val enabled: Boolean = true
)

sealed class ChipStyle(val mainAxisSpacing: Dp, val crossAxisSpacing: Dp) {
    object Boxy: ChipStyle(8.dp, 4.dp)
    object Rounded: ChipStyle(4.dp, 4.dp)
    class Mixed(val predicate: (ChipInfo) -> ChipStyle): ChipStyle(8.dp, 4.dp)
}

interface ChipColors {
    @Composable fun selectedContainerColor(): Color
    @Composable fun selectedContentColor(): Color
    @Composable fun unselectedContentColor(): Color
    @Composable fun unselectedContainerColor(): Color
}

object ChipDefaults {
    @Composable
    fun roundedChipColors(
        selectedContainerColor: Color = MaterialTheme.colorScheme.inverseSurface,
        unselectedContainerColor: Color = MaterialTheme.colorScheme.inverseSurface,
        selectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
    ): ChipColors {
        return object : ChipColors {
            @Composable override fun selectedContainerColor() = selectedContainerColor
            @Composable override fun selectedContentColor() = selectedContentColor
            @Composable override fun unselectedContainerColor() = unselectedContainerColor
            @Composable override fun unselectedContentColor() = unselectedContentColor
        }
    }

    @Composable
    fun boxyChipColors(
        selectedContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
        unselectedContainerColor: Color = MaterialTheme.colorScheme.secondary,
        selectedContentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
        unselectedContentColor: Color = MaterialTheme.colorScheme.onSecondary
    ): ChipColors {
        return object : ChipColors {
            @Composable override fun selectedContainerColor() = selectedContainerColor
            @Composable override fun selectedContentColor() = selectedContentColor
            @Composable override fun unselectedContainerColor() = unselectedContainerColor
            @Composable override fun unselectedContentColor() = unselectedContentColor
        }
    }
}

@Composable
fun BoxyChip(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodySmall,
    isSelected: Boolean = true,
    onSelectionChanged: (String) -> Unit = {},
    enabled: Boolean = true,
    colors: ChipColors = ChipDefaults.boxyChipColors()
) {
    Surface(
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(5.dp),
        color = if (isSelected) colors.selectedContainerColor() else colors.unselectedContainerColor()
    ) {
        Row(
            modifier = Modifier
                .toggleable(
                    value = isSelected,
                    onValueChange = {
                        onSelectionChanged(text)
                    },
                    enabled = enabled
                )
        ) {
            Text(
                text = text,
                style = style,
                color = if (isSelected) colors.selectedContentColor() else colors.unselectedContentColor(),
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
    onSelectionChanged: (String) -> Unit = {},
    enabled: Boolean = true,
    colors: ChipColors = ChipDefaults.roundedChipColors()
) {
    val borderColor = if (isSelected) colors.selectedContainerColor() else colors.unselectedContainerColor()
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
                    },
                    enabled = enabled
                )
                .padding(8.dp)
        ) {
            Text(
                text = text,
                style = style,
                color = if (isSelected) colors.selectedContentColor() else colors.unselectedContentColor()
            )
        }
    }
}

@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    chips: List<ChipInfo> = emptyList(),
    onSelectedChanged: (String) -> Unit = {},
    chipStyle: ChipStyle = ChipStyle.Boxy,
    roundedChipColors: ChipColors = ChipDefaults.roundedChipColors(),
    boxyChipColors: ChipColors = ChipDefaults.boxyChipColors()
) {

    @Composable
    fun DrawChip(chipStyle: ChipStyle, chip: ChipInfo) {
        when (chipStyle) {
            ChipStyle.Boxy -> {
                BoxyChip(
                    text = chip.text,
                    onSelectionChanged = onSelectedChanged,
                    enabled = chip.enabled,
                    colors = boxyChipColors
                )
            }
            ChipStyle.Rounded -> {
                RoundedChip(
                    text = chip.text,
                    onSelectionChanged = onSelectedChanged,
                    enabled = chip.enabled,
                    colors = roundedChipColors
                )
            }
            is ChipStyle.Mixed -> {
                DrawChip(chipStyle = chipStyle.predicate(chip), chip = chip)
            }
        }
    }

    FlowRow(
        modifier = modifier,
        crossAxisSpacing = chipStyle.crossAxisSpacing,
        mainAxisSpacing = chipStyle.mainAxisSpacing
    ) {
        chips.forEach { chip ->
            DrawChip(chipStyle = chipStyle, chip = chip)
        }
    }
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
        shape = RoundedCornerShape(25.dp),
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FullScreenThumbnailVideoPlayer(
    key: String,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .clickable(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://www.youtube.com/watch?v=$key")
                        }
                        context.startActivity(intent)
                    }
                ),
            model = "https://img.youtube.com/vi/${key}/hqdefault.jpg",
            contentDescription = "",
            placeholder = rememberAsyncImagePainter(model = R.drawable.placeholder)
        )

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
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
    imageSize: DpSize? = null,
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
        val mod = if (imageSize != null) {
            Modifier
                .align(imageAlignment)
                .size(size = imageSize)
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
        AsyncImage(
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            model = TmdbUtils.getFullAvatarPath(author),
            contentDescription = ""
        )
    } else {
        val name = author.name.unlessEmpty(author.username)
        UserInitials(size = size, name = name)
    }
}

@Composable
fun UserInitials(
    size: Dp,
    name: String,
    fontSize: TextUnit = 16.sp
) {
    val sanitizedName = name.replace("\\s+".toRegex(), " ")
    val userName = if(sanitizedName.contains(" ")) {
        sanitizedName.split(" ")[0][0].toString() + sanitizedName.split(" ")[1][0].toString()
    } else {
        if (sanitizedName.length < 3) name else { sanitizedName.substring(0, 2) }
    }
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(size)
            .background(color = MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = userName.uppercase(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = MaterialTheme.colorScheme.background,
                fontSize = fontSize
            )
        )
    }
}

@Composable
fun AccountIcon(
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    val accountDetails = SessionManager.currentSession.value?.accountDetails?.value
    val avatarUrl = accountDetails?.let {
        when {
            accountDetails.avatar.tmdb?.avatarPath?.isNotEmpty() == true -> {
                TmdbUtils.getAccountAvatarUrl(accountDetails)
            }
            accountDetails.avatar.gravatar?.isDefault() == false -> {
                TmdbUtils.getAccountGravatarUrl(accountDetails)
            }
            else -> null
        }
    }

    Box(modifier = modifier
        .clip(CircleShape)
        .clickable(
            enabled = enabled,
            onClick = onClick
        )
    ) {
        if (accountDetails == null) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(size),
                tint = MaterialTheme.colorScheme.secondary
            )
        } else if (avatarUrl == null) {
            val name = accountDetails.name.ifEmpty { accountDetails.username }
            UserInitials(size = size, name = name)
        } else {
            Box(modifier = Modifier.size(size)) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }
        }
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
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
        keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Password),
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

@Composable
fun TimeoutSnackbar(
    modifier: Modifier = Modifier,
    text: String,
    timeoutMillis: Long = 400,
    onDismiss: () -> Unit = {}
) {
    var snackbarVisible by remember { mutableStateOf(true) }

    if (snackbarVisible) {
        Snackbar(
            modifier = modifier
        ) {
            Text(text = text)
        }

        LaunchedEffect(Unit) {
            delay(timeoutMillis)
            snackbarVisible = false
            onDismiss()
        }
    }
}

@Composable
fun CenteredIconCircle(
    size: Dp,
    backgroundColor: Color,
    iconTint: Color,
    icon: ImageVector,
    contentDescription: String?,
    showIcon: Boolean = true,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color = backgroundColor)
            .clickable {
                onClick()
            }
    ) {
        if (showIcon) {
            Column(
                modifier = Modifier.size(size),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(size / 2),
                    tint = iconTint
                )
            }
        }
    }
}

@Composable
fun <T> Spinner(
    modifier: Modifier = Modifier,
    list: List<Pair<String, T>>,
    preselected: Pair<String, T>,
    onSelectionChanged: (Pair<String, T>) -> Unit
) {
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable {
                        expanded = !expanded
                    }
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = selected.first,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable(
                            onClick = {
                                expanded = !expanded
                            }
                        )
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                list.forEach { entry ->
                    DropdownMenuItem(
                        text = { Text(text = entry.first) },
                        onClick = {
                            selected = entry
                            expanded = false
                            onSelectionChanged(selected)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    placeholder: String,
    onClick: () -> Unit
) {
    RoundedTextField(
        modifier = Modifier
            .padding(all = 12.dp)
            .height(55.dp)
            .clickable(
                onClick = onClick
            ),
        value = "",
        enabled = false,
        onValueChange = {  },
        placeHolder = stringResource(id = R.string.search_placeholder, placeholder),
        leadingIcon = {
            Image(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search_icon_content_descriptor),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
            )
        }
    )
}

@Composable
fun MyDivider(modifier: Modifier = Modifier) {
    Divider(thickness = 0.5.dp, modifier = modifier, color = MaterialTheme.colorScheme.secondaryContainer)
}

@Composable
fun SelectableTextItem(
    selected: Boolean,
    onSelected: () -> Unit,
    text: String,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onSelected)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            val size = remember { mutableStateOf(IntSize.Zero) }
            val color = if (selected) selectedColor else unselectedColor
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = color,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .onGloballyPositioned { size.value = it.size }
            )
            Box(
                modifier = Modifier
                    .height(height = if (selected) 3.dp else 1.dp)
                    .width(width = size.value.width.toDp().plus(8.dp))
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(color = color)
            )
        }
    }
}

@Composable
fun SelectableTextChip(
    selected: Boolean,
    onSelected: () -> Unit,
    text: String,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    unselectedColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 25))
            .border(width = 1.dp, color = selectedColor, shape = RoundedCornerShape(percent = 25))
            .background(color = if(selected) selectedColor else unselectedColor)
            .clickable(onClick = onSelected)
    ) {
        Text(
            text = text,
            color = if (selected) unselectedColor else selectedColor,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(12.dp)
        )
    }
}