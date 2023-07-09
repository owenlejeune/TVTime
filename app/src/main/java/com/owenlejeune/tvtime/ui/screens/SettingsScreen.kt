package com.owenlejeune.tvtime.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.OnboardingActivity
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.*
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.viewmodel.SettingsViewModel
import com.owenlejeune.tvtime.ui.views.HomeTabRecyclerAdapter
import com.owenlejeune.tvtime.ui.views.ItemMoveCallback
import com.owenlejeune.tvtime.utils.ResourceUtils
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appNavController: NavController,
    route: String? = null,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarScrollState)

    val appBarTitle = remember { mutableStateOf("") }
    val defaultRestoreAction = ::resetAllPreferences
    val restoreAction = remember { mutableStateOf<SettingsResetHandler>(defaultRestoreAction) }

    val settingsViewModel = viewModel<SettingsViewModel>()

    val activity = LocalContext.current as AppCompatActivity

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
                },
                actions = {
                    IconButton(
                        onClick = {
                            restoreAction.value(preferences, settingsViewModel, activity)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SettingsBackupRestore,
                            contentDescription = stringResource(R.string.preferences_restore_content_description)
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
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    SettingsPage.getByRoute(route).apply {
                        appBarTitle.value = name
                        restoreAction.value = resetPreferencesHandler
                        settingsPageRenderer(appNavController, activity)
                    }
                }
            } else {
                restoreAction.value = ::resetAllPreferences
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
            title = stringResource(id = R.string.preference_heading_home_screen),
            subtitle = stringResource(R.string.preference_subtitle_home_screen),
            icon = Icons.Filled.Home,
            settingsView = SettingsPage.HomeScreenSettings,
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
                    appNavController.navigate(AppNavItem.SettingsView.withArgs(settingsView.route))
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
private fun SearchPreferences() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()

    SwitchPreference(
        titleText = stringResource(R.string.preferences_persistent_search_title),
        subtitleText = stringResource(R.string.preferences_persistent_search_subtitle),
        checkState = settingsViewModel.showSearchBar.collectAsState(),
        onCheckedChange = {
            settingsViewModel.toggleShowSearchBar()
        }
    )

    SwitchPreference(
        titleText = stringResource(R.string.preference_multi_search_title),
        subtitleText = stringResource(R.string.preference_multi_search_subtitle),
        checkState = settingsViewModel.useMultiSearch.collectAsState(),
        onCheckedChange = {
            settingsViewModel.toggleMultiSearch()
        }
    )
}

@Composable
private fun DesignPreferences(
    appNavController: NavController,
    activity: AppCompatActivity
) {
    val settingsViewModel = viewModel<SettingsViewModel>()

    TopLevelSettingsCard(
        title = stringResource(id = R.string.preference_heading_dark_mode),
        subtitle = stringResource(R.string.preference_subtitle_dark_mode),
        icon = Icons.Outlined.DarkMode,
        settingsView = SettingsPage.DarkModeSettings,
        appNavController = appNavController
    )

    SwitchPreference(
        titleText = stringResource(R.string.preferences_use_wallpaper_colors_title),
        subtitleText = stringResource(R.string.preferences_use_wallpaper_colors_subtitle),
        checkState = settingsViewModel.useWallpaperColors.collectAsState(),
        onCheckedChange = {
            settingsViewModel.toggleUseWallpaperColors()
            activity.recreate()
        }
    )

    val isSystemColorsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    if (isSystemColorsSupported) {
        SwitchPreference(
            titleText = stringResource(id = R.string.preference_system_colors_heading),
            subtitleText = stringResource(R.string.preference_system_colors_subtitle),
            checkState = settingsViewModel.useSystemColors.collectAsState(),
            onCheckedChange = { isChecked ->
                settingsViewModel.toggleUseSystemColors()
                MonetCompat.useSystemColorsOnAndroid12 = isChecked
                MonetCompat.getInstance().updateMonetColors()
            },
            enabled = settingsViewModel.useSystemColors.collectAsState().value
        )
    }

    SliderPreference(
        titleText = stringResource(id = R.string.preference_chroma_factor_heading),
        value = settingsViewModel.chromaMultiplier.collectAsState(),
        onValueChangeFinished = { value ->
            with((value / 50f)) {
                settingsViewModel.setChromaMultiplier(value)
                MonetCompat.chromaMultiplier = this
            }
            MonetCompat.getInstance().updateMonetColors()
        },
        enabled = (if (isSystemColorsSupported) !settingsViewModel.useSystemColors.collectAsState().value else true)
            .and(settingsViewModel.useWallpaperColors.collectAsState().value)
    )

    val showWallpaperPicker = remember { mutableStateOf(false) }
    val wallpaperPickerModifier =
        if (isSystemColorsSupported && settingsViewModel.useSystemColors.collectAsState().value && settingsViewModel.useWallpaperColors.collectAsState().value) {
            Modifier
        } else {
            Modifier.clickable { showWallpaperPicker.value = true }
        }
    Text(
        text = stringResource(id = R.string.preference_wallpaper_color_heading),
        style = MaterialTheme.typography.titleLarge,
        fontSize = 20.sp,
        color = if (isSystemColorsSupported && settingsViewModel.useSystemColors.collectAsState().value) {//preferences.useSystemColors) {
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

@Composable
private fun DarkModePreferences(
    activity: AppCompatActivity
) {
    val settingsViewModel = viewModel<SettingsViewModel>()

    val isSelected: (Int) -> Boolean = { settingsViewModel.darkTheme.value == it }
    val onChangeState: (Int) -> Unit = {
        settingsViewModel.setDarkMode(it)
        activity.recreate()
    }

    PreferenceHeading(text = stringResource(R.string.preference_dark_mode_automatic_heading))
    RadioButtonPreference(
        selected = isSelected(AppPreferences.DarkMode.Automatic.ordinal),
        title = stringResource(R.string.preference_dark_mode_follow_system_label),
        icon = Icons.Filled.Brightness6,
        onClick = {
            onChangeState(AppPreferences.DarkMode.Automatic.ordinal)
        }
    )

    PreferenceHeading(text = stringResource(R.string.preference_dark_mode_maual_heading))
    RadioButtonPreference(
        selected = isSelected(AppPreferences.DarkMode.Light.ordinal),
        title = stringResource(R.string.preference_dark_mode_light_mode_label),
        icon = Icons.Outlined.LightMode,
        onClick = {
            onChangeState(AppPreferences.DarkMode.Light.ordinal)
        }
    )
    RadioButtonPreference(
        selected = isSelected(AppPreferences.DarkMode.Dark.ordinal),
        title = stringResource(R.string.preference_dark_mode_dark_mode_label),
        icon = Icons.Outlined.DarkMode,
        onClick = {
            onChangeState(AppPreferences.DarkMode.Dark.ordinal)
        }
    )
}

@Composable
private fun HomeScreenPreferences() {
    val settingsViewModel = viewModel<SettingsViewModel>()

    PreferenceHeading(text = stringResource(R.string.preference_look_and_feel_heading))

    SwitchPreference(
        titleText = stringResource(R.string.preference_show_text_labels_title),
        subtitleText = stringResource(R.string.preference_show_text_labels_subtitle),
        checkState = settingsViewModel.showBottomTabLabels.collectAsState(),
        onCheckedChange = {
            settingsViewModel.toggleShowBottomTabLabels()
        }
    )

    SwitchPreference(
        titleText = stringResource(R.string.preference_show_poster_titles_title),
        subtitleText = stringResource(R.string.preference_show_poster_titles_subtitle),
        checkState = settingsViewModel.showPosterTitles.collectAsState(),
        onCheckedChange = {
            settingsViewModel.toggleShowPosterTitles()
        }
    )
    
    PreferenceHeading(text = stringResource(R.string.preference_home_tab_order_heading))

    Box(modifier = Modifier
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(10.dp)
        )
        .padding(horizontal = 12.dp)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context)
                    val mAdapter = HomeTabRecyclerAdapter()
                    val touchCallback = ItemMoveCallback(mAdapter)
                    val touchHelper = ItemTouchHelper(touchCallback)
                    touchHelper.attachToRecyclerView(this)
                    adapter = mAdapter
                }
            },
            update = { }
        )
    }
}

@Composable
private fun DevPreferences() {
    if (BuildConfig.DEBUG) {
        val settingsViewModel = viewModel<SettingsViewModel>()

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        SwitchPreference(
            titleText = "Show backdrop gallery",
            subtitleText = "Show galleries for movies/tv backdrops",
            checkState = settingsViewModel.showBackdropGallery.collectAsState(),
            onCheckedChange = {
                settingsViewModel.toggleShowBackdropGallery()
            }
        )

        SwitchPreference(
            titleText = "Always show onboarding flow",
            subtitleText = "Show onboarding flow on each launch",
            checkState = settingsViewModel.firstLaunchTesting.collectAsState(),
            onCheckedChange = {
                settingsViewModel.toggleFirstLaunchTesting()
            }
        )
        Text(
            text = "Show onboarding UI",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .clickable {
                    OnboardingActivity.showActivity(context)
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
                        coroutineScope.launch {
                            SessionManager.clearSession()
                        }
                    }
                )
        )
    }
}

@Composable
private fun WallpaperPicker(
    showPopup: MutableState<Boolean>
) {
    val context = LocalContext.current

    val settingsViewModel = viewModel<SettingsViewModel>()

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
                                    settingsViewModel.setSelectedColor(it)
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
                            if (settingsViewModel.selectedColor.value != Int.MAX_VALUE) {
                                settingsViewModel.setSelectedColor(Int.MAX_VALUE)
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

private fun resetAllPreferences(
    preferences: AppPreferences,
    settingsViewModel: SettingsViewModel,
    activity: AppCompatActivity
) {
    resetSearchPreferences(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
    resetDesignPreferences(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
    resetHomeScreenPreferences(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
    resetDevModePreference(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
    activity.recreate()
    Toast.makeText(activity, "All settings reset successfully", Toast.LENGTH_SHORT).show()
}

private fun resetSearchPreferences(
    preferences: AppPreferences,
    settingsViewModel: SettingsViewModel,
    activity: AppCompatActivity,
    skipRecreate: Boolean = false
) {
    settingsViewModel.setShowSearchBar(preferences.showSearchBarDefault)
    settingsViewModel.setMultiSearch(preferences.multiSearchDefault)
    if (!skipRecreate) {
        activity.recreate()
    }
}

private fun resetDesignPreferences(
    preferences: AppPreferences,
    settingsViewModel: SettingsViewModel,
    activity: AppCompatActivity,
    skipRecreate: Boolean = false
) {
    settingsViewModel.setUseSystemColors(preferences.useSystemColorsDefault)
    settingsViewModel.setUseWallpaperColors(preferences.useWallpaperColorsDefault)
    settingsViewModel.setChromaMultiplier(preferences.chromaMultiplierDefault)
    settingsViewModel.setSelectedColor(preferences.selectedColorDefault)
    resetDarkModePreferences(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
    if (!skipRecreate) {
        activity.recreate()
    }
}

private fun resetDarkModePreferences(
    preferences: AppPreferences,
    settingsViewModel: SettingsViewModel,
    activity: AppCompatActivity,
    skipRecreate: Boolean = false
) {
    settingsViewModel.setDarkMode(preferences.darkThemeDefault)
    if (!skipRecreate) {
        activity.recreate()
    }
}

private fun resetDevModePreference(
    preferences: AppPreferences,
    settingsViewModel: SettingsViewModel,
    activity: AppCompatActivity,
    skipRecreate: Boolean = false
) {
    settingsViewModel.setFirstLaunchTesting(preferences.firstLaunchTestingDefault)
    settingsViewModel.setShowBackdropGallery(preferences.showBackdropGalleryDefault)
    if (!skipRecreate) {
        activity.recreate()
    }
}

private fun resetHomeScreenPreferences(
    preferences: AppPreferences,
    settingsViewModel: SettingsViewModel,
    activity: AppCompatActivity,
    skipRecreate: Boolean = false
) {
    settingsViewModel.setShowBottomTabLabels(preferences.showBottomTabLabelsDefault)
    settingsViewModel.setShowPosterTitles(preferences.showPosterTitlesDefault)
    preferences.moviesTabPosition = preferences.moviesTabPositionDefault
    preferences.tvTabPosition = preferences.tvTabPositionDefault
    preferences.peopleTabPosition = preferences.peopleTabPositionDefault
    preferences.accountTabPosition = preferences.accountTabPositionDefault
    preferences.showBottomTabLabels = preferences.showBottomTabLabelsDefault
    if (!skipRecreate) {
        activity.recreate()
    }
}

private typealias SettingsResetHandler = (AppPreferences, SettingsViewModel, AppCompatActivity) -> Unit
private typealias SettingsPageRenderer = @Composable (NavController, AppCompatActivity) -> Unit

private sealed class SettingsPage(
    stringRes: Int,
    val route: String,
    val settingsPageRenderer: SettingsPageRenderer,
    val resetPreferencesHandler: SettingsResetHandler
): KoinComponent {
    private val resources: ResourceUtils by inject()

    val name = resources.getString(stringRes)

    companion object {
        val Pages by lazy {
            listOf(
                SearchSettings,
                DesignSettings,
                DeveloperSettings,
                DarkModeSettings,
                HomeScreenSettings
            )
        }

        fun getByRoute(route: String): SettingsPage {
            return Pages.map { it.route to it }
                .find { it.first == route }
                ?.second
                ?: throw IllegalArgumentException("Invalid settings page route: $route")
        }
    }

    object SearchSettings: SettingsPage(
        R.string.preference_heading_search,
        "search",
        @Composable { _, _ -> SearchPreferences() },
        ::resetSearchPreferences
    )
    object DesignSettings: SettingsPage(
        R.string.preference_heading_design,
        "design",
        @Composable { n, a -> DesignPreferences(n, a) },
        ::resetDesignPreferences
    )
    object HomeScreenSettings: SettingsPage(
        R.string.preference_heading_home_screen,
        "home",
        @Composable { _, _ -> HomeScreenPreferences() },
        ::resetHomeScreenPreferences
    )
    object DeveloperSettings: SettingsPage(
        R.string.preferences_debug_title,
        "dev",
        @Composable { _, _ -> DevPreferences() },
        ::resetDevModePreference
    )
    object DarkModeSettings: SettingsPage(
        R.string.preference_heading_dark_mode,
        "darkmode",
        @Composable { _, a -> DarkModePreferences(a) },
        ::resetDarkModePreferences
    )
}