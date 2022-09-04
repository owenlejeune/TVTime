package com.owenlejeune.tvtime.ui.screens.main

import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.*
import com.owenlejeune.tvtime.ui.navigation.BottomNavItem
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.utils.ResourceUtils
import com.owenlejeune.tvtime.utils.SessionManager
import dev.kdrag0n.monet.factory.ColorSchemeFactory
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTab(
    appNavController: NavController,
    activity: AppCompatActivity,
    route: String? = null,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val topAppBarScrollState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec, topAppBarScrollState)
    }

    val appBarTitle = remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults
                    .largeTopAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.background
                    ),
                title = { Text(text = appBarTitle.value) },
                navigationIcon = {
                    IconButton(
                        onClick = { appNavController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.content_description_back_button)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (route != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    SettingsPage.getByRoute(route).apply {
                        appBarTitle.value = name
                        SettingsPageRenderer(appNavController, activity, preferences)
                    }
                }
            } else {
                SettingsTabView(
                    appNavController = appNavController,
                    appBarTitle = appBarTitle
                )
            }
        }
    }
}

@Composable
private fun SettingsTabView(
    appNavController: NavController,
    appBarTitle: MutableState<String>
) {
    appBarTitle.value = stringResource(id = R.string.nav_settings_title)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TopLevelSettingsCard(
            title = stringResource(id = R.string.preference_heading_search),
            subtitle = stringResource(R.string.preference_subtitle_search),
            icon = Icons.Filled.Search,
            settingsView = SettingsPage.SearchSettings,
            appNavController = appNavController
        )

        TopLevelSettingsCard(
            title = stringResource(id = R.string.preference_heading_design),
            subtitle = stringResource(R.string.preference_subtitle_design),
            icon = Icons.Filled.Palette,
            settingsView = SettingsPage.DesignSettings,
            appNavController = appNavController
        )

        TopLevelSettingsCard(
            title = stringResource(id = R.string.preferences_debug_title),
            subtitle = stringResource(R.string.preference_subtitle_debug),
            icon = Icons.Filled.DeveloperBoard,
            settingsView = SettingsPage.DeveloperSettings,
            appNavController = appNavController
        )
    }
}

@Composable
private fun TopLevelSettingsCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    settingsView: SettingsPage,
    appNavController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                onClick = {
                    appNavController.navigate("${MainNavItem.SettingsView.route}/${settingsView.route}")
                }
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = subtitle,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 16.dp),
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
private fun SearchPreferences(
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val persistentSearch = remember { mutableStateOf(preferences.persistentSearch) }
    SwitchPreference(
        titleText = stringResource(R.string.preferences_persistent_search_title),
        subtitleText = stringResource(R.string.preferences_persistent_search_subtitle),
        checkState = persistentSearch.value,
        onCheckedChange = { isChecked ->
            persistentSearch.value = isChecked
            preferences.persistentSearch = isChecked
        }
    )

    val hideTitle = remember { mutableStateOf(preferences.hideTitle) }
    SwitchPreference(
        titleText = stringResource(R.string.preferences_hide_heading_title),
        subtitleText = stringResource(R.string.preferences_hide_heading_subtitle),
        checkState = hideTitle.value,
        onCheckedChange = { isChecked ->
            hideTitle.value = isChecked
            preferences.hideTitle = isChecked
        },
        enabled = persistentSearch.value
    )
}

@Composable
private fun DesignPreferences(
    appNavController: NavController,
    activity: AppCompatActivity,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    TopLevelSettingsCard(
        title = stringResource(id = R.string.preference_heading_dark_mode),
        subtitle = stringResource(R.string.preference_subtitle_dark_mode),
        icon = Icons.Outlined.DarkMode,
        settingsView = SettingsPage.DarkModeSettings,
        appNavController = appNavController
    )

    val useWallpaperColors = remember { mutableStateOf(preferences.useWallpaperColors) }
    SwitchPreference(
        titleText = stringResource(R.string.preferences_use_wallpaper_colors_title),
        subtitleText = stringResource(R.string.preferences_use_wallpaper_colors_subtitle),
        checkState = useWallpaperColors.value,
        onCheckedChange = { isChecked ->
            useWallpaperColors.value = isChecked
            preferences.useWallpaperColors = isChecked
            activity.recreate()
        }
    )

    val isSystemColorsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    if (isSystemColorsSupported) {
        val useSystemColors = remember { mutableStateOf(preferences.useSystemColors) }
        SwitchPreference(
            titleText = stringResource(id = R.string.preference_system_colors_heading),
            subtitleText = stringResource(R.string.preference_system_colors_subtitle),
            checkState = useSystemColors.value,
            onCheckedChange = { isChecked ->
                useSystemColors.value = isChecked
                preferences.useSystemColors = isChecked
                MonetCompat.useSystemColorsOnAndroid12 = isChecked
                MonetCompat.getInstance().updateMonetColors()
            },
            enabled = useWallpaperColors.value
        )
    }

    SliderPreference(
        titleText = stringResource(id = R.string.preference_chroma_factor_heading),
        value = preferences.chromaMultiplier.toFloat() * 50f,
        onValueChangeFinished = { value ->
            with((value / 50f).toDouble()) {
                preferences.chromaMultiplier = this
                MonetCompat.chromaMultiplier = this
            }
            MonetCompat.getInstance().updateMonetColors()
        },
        enabled = (if (isSystemColorsSupported) !preferences.useSystemColors else true).and(
            useWallpaperColors.value
        )
    )

    val showWallpaperPicker = remember { mutableStateOf(false) }
    val wallpaperPickerModifier =
        if (isSystemColorsSupported && preferences.useSystemColors && useWallpaperColors.value) {
            Modifier
        } else {
            Modifier.clickable { showWallpaperPicker.value = true }
        }
    Text(
        text = stringResource(id = R.string.preference_wallpaper_color_heading),
        style = MaterialTheme.typography.titleLarge,
        fontSize = 20.sp,
        color = if (isSystemColorsSupported && preferences.useSystemColors) {
            MaterialTheme.colorScheme.outline
        } else {
            MaterialTheme.colorScheme.onBackground
        },
        modifier = wallpaperPickerModifier
            .padding(start = 8.dp)
    )
    if (showWallpaperPicker.value) {
        WallpaperPicker(showPopup = showWallpaperPicker)
    }
}

enum class DarkMode {
    Automatic,
    Dark,
    Light
}

@Composable
private fun DarkModePreferences(
    activity: AppCompatActivity,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val selectedValue = remember { mutableStateOf(preferences.darkTheme) }

    val isSelected: (Int) -> Boolean = { selectedValue.value == it }
    val onChangeState: (Int) -> Unit = {
        selectedValue.value = it
        preferences.darkTheme = it
        activity.recreate()
    }

    PreferenceHeading(text = "Automatic")
    RadioButtonPreference(
        selected = isSelected(DarkMode.Automatic.ordinal),
        title = "Follow system",
        icon = Icons.Filled.Brightness6,
        onClick = {
            onChangeState(DarkMode.Automatic.ordinal)
        }
    )

    PreferenceHeading(text = "Manual")
    RadioButtonPreference(
        selected = isSelected(DarkMode.Light.ordinal),
        title = "Light mode",
        icon = Icons.Outlined.LightMode,
        onClick = {
            onChangeState(DarkMode.Light.ordinal)
        }
    )
    RadioButtonPreference(
        selected = isSelected(DarkMode.Dark.ordinal),
        title = "Dark mode",
        icon = Icons.Outlined.DarkMode,
        onClick = {
            onChangeState(DarkMode.Dark.ordinal)
        }
    )
}

@Composable
private fun DevPreferences(
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    if (BuildConfig.DEBUG) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        val firstLaunchTesting = remember { mutableStateOf(preferences.firstLaunchTesting) }
        SwitchPreference(
            titleText = "Always show onboarding flow",
            subtitleText = "Show onboarding flow on each launch",
            checkState = firstLaunchTesting.value,
            onCheckedChange = { isChecked ->
                firstLaunchTesting.value = isChecked
                preferences.firstLaunchTesting = isChecked
            }
        )

        val shouldShowPalette = remember { mutableStateOf(false) }
        Text(
            text = "Show material palette",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .clickable(
                    onClick = {
                        shouldShowPalette.value = true
                    }
                )
        )
        if (shouldShowPalette.value) {
            AlertDialog(
                modifier = Modifier.padding(12.dp),
                title = { Text(text = "Palette") },
                text = { PaletteView() },
                dismissButton = {
                    TextButton(onClick = { shouldShowPalette.value = false }) {
                        Text("Dismiss")
                    }
                },
                confirmButton = {},
                onDismissRequest = { shouldShowPalette.value = false }
            )
        }

        Text(
            text = "Clear session",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .clickable(
                    onClick = {
                        preferences.guestSessionId = ""
                        coroutineScope.launch {
                            SessionManager.clearSession {
                                Toast
                                    .makeText(context, "Cleared session: $it", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            SessionManager.clearSessionV4 {
                                Toast
                                    .makeText(
                                        context,
                                        "Cleared session v4: $it",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                    }
                )
        )

        val useV4Api = remember { mutableStateOf(preferences.useV4Api) }
        SwitchPreference(
            titleText = "Use v4 API",
            checkState = useV4Api.value,
            onCheckedChange = { isChecked ->
                useV4Api.value = isChecked
                preferences.useV4Api = isChecked
            }
        )
    }
}

@Composable
private fun WallpaperPicker(
    showPopup: MutableState<Boolean>,
    preference: AppPreferences = get(AppPreferences::class.java)
) {
    val context = LocalContext.current

    val wallpaperColors = remember { mutableStateOf<List<Int>>(emptyList()) }
    val selectedWallpaperColor = remember { mutableStateOf(0) }
    LaunchedEffect(true) {
        val colors = MonetCompat.getInstance().getAvailableWallpaperColors() ?: emptyList()
        if (colors.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.preference_no_wallpaper_colors), Toast.LENGTH_SHORT).show()
            showPopup.value = false

        }
        wallpaperColors.value = colors
        selectedWallpaperColor.value = MonetCompat.getInstance().getSelectedWallpaperColor() ?: 0
    }

    if (wallpaperColors.value.isNotEmpty() && selectedWallpaperColor.value != 0) {
        val colorCircleSize = 50.dp
        AlertDialog(
            modifier = Modifier.padding(12.dp),
            title = { Text(text = stringResource(id = R.string.preference_wallpaper_color_heading)) },
            text = {
                Row(
                    modifier = Modifier
                        .height(colorCircleSize)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    wallpaperColors.value.forEach {
                        CenteredIconCircle(
                            size = colorCircleSize,
                            backgroundColor = Color(it),
                            iconTint = Color.White,
                            icon = Icons.Filled.Check,
                            contentDescription = null,
                            showIcon = it == selectedWallpaperColor.value,
                            onClick = {
                                if (it != selectedWallpaperColor.value) {
                                    preference.selectedColor = it
                                    MonetCompat
                                        .getInstance()
                                        .updateMonetColors()
                                }
                            }
                        )
                    }

                    CenteredIconCircle(
                        size = colorCircleSize,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                        icon = Icons.Filled.RestartAlt,
                        contentDescription = null,
                        onClick = {
                            if (preference.selectedColor != Int.MAX_VALUE) {
                                preference.selectedColor = Int.MAX_VALUE
                                MonetCompat.getInstance().updateMonetColors()
                            } else {
                                Toast.makeText(context, context.getString(R.string.preference_wallpaper_color_default_selected), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showPopup.value = false }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            },
            confirmButton = {},
            onDismissRequest = { showPopup.value = false }
        )
    }
}

private sealed class SettingsPage(stringRes: Int, val route: String, val SettingsPageRenderer: @Composable (NavController, AppCompatActivity, AppPreferences) -> Unit): KoinComponent {
    private val resources: ResourceUtils by inject()

    val name = resources.getString(stringRes)

    companion object {
        val Pages by lazy {listOf(SearchSettings, DesignSettings, DeveloperSettings, DarkModeSettings) }

        fun getByRoute(route: String): SettingsPage {
            return Pages.map { it.route to it }
                .find { it.first == route }
                ?.second
                ?: throw IllegalArgumentException("Invalid settings page route: $route")
        }
    }

    object SearchSettings: SettingsPage(R.string.preference_heading_search, "search", @Composable { n, a, p -> SearchPreferences(p) } )
    object DesignSettings: SettingsPage(R.string.preference_heading_design, "design", @Composable { n, a, p -> DesignPreferences(n, a, p) } )
    object DeveloperSettings: SettingsPage(R.string.preferences_debug_title,"dev", @Composable { n, a, p -> DevPreferences(p) } )
    object DarkModeSettings: SettingsPage(R.string.preference_heading_dark_mode, "darkmode", @Composable { n, a, p -> DarkModePreferences(a, p) } )
}
