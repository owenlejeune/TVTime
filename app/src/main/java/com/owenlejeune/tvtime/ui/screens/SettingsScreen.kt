package com.owenlejeune.tvtime.ui.screens

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Flare
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.OnboardingActivity
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.extensions.unlessEmpty
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.CenteredIconCircle
import com.owenlejeune.tvtime.ui.components.PaletteView
import com.owenlejeune.tvtime.ui.components.PreferenceHeading
import com.owenlejeune.tvtime.ui.components.RadioButtonPreference
import com.owenlejeune.tvtime.ui.components.SliderPreference
import com.owenlejeune.tvtime.ui.components.SwitchPreference
import com.owenlejeune.tvtime.ui.components.TVTLargeTopAppBar
import com.owenlejeune.tvtime.ui.navigation.SettingsNavItem
import com.owenlejeune.tvtime.ui.navigation.SettingsNavigationHost
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.SettingsViewModel
import com.owenlejeune.tvtime.ui.views.HomeTabRecyclerAdapter
import com.owenlejeune.tvtime.ui.views.ItemMoveCallback
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appNavController: NavController
) {
    val applicationViewModel = viewModel<ApplicationViewModel>()
    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarScrollState)

    val appBarTitle = remember { mutableStateOf("") }
    val popBackAction = remember { mutableStateOf({}) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TVTLargeTopAppBar(
                scrollBehavior = scrollBehavior,
                appNavController = appNavController,
                title = { Text(text = appBarTitle.value) },
                navigationIcon = {
                    IconButton(
                        onClick = popBackAction.value
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.content_description_back_button)
                        )
                    }
                },
//                actions = {
//                    IconButton(
//                        onClick = {
//                            restoreAction.value(preferences, settingsViewModel, activity)
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.SettingsBackupRestore,
//                            contentDescription = stringResource(R.string.preferences_restore_content_description)
//                        )
//                    }
//                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            SettingsNavigationHost(
                appNavController = appNavController,
                appBarTitle = appBarTitle,
                popBackAction = popBackAction
            )
        }
    }
}

@Composable
fun SettingsHome(
    settingsNavController: NavController
) {
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
            route = SettingsNavItem.Search.route,
            settingsNavController = settingsNavController
        )

        TopLevelSettingsCard(
            title = stringResource(id = R.string.preference_heading_design),
            subtitle = stringResource(R.string.preference_subtitle_design),
            icon = Icons.Filled.Palette,
            route = SettingsNavItem.Design.route,
            settingsNavController = settingsNavController
        )

        TopLevelSettingsCard(
            title = stringResource(id = R.string.preference_heading_home_screen),
            subtitle = stringResource(R.string.preference_subtitle_home_screen),
            icon = Icons.Filled.Home,
            route = SettingsNavItem.HomeScreen.route,
            settingsNavController = settingsNavController
        )

        TopLevelSettingsCard(
            title = stringResource(id = R.string.preference_heading_special_features),
            subtitle = stringResource(id = R.string.preference_subtitle_special_features),
            icon = Icons.Outlined.Flare,
            route = SettingsNavItem.SpecialFeatures.route,
            settingsNavController = settingsNavController
        )

        TopLevelSettingsCard(
            title = stringResource(id = R.string.preferences_debug_title),
            subtitle = stringResource(R.string.preference_subtitle_debug),
            icon = Icons.Filled.DeveloperBoard,
            route = SettingsNavItem.Developer.route,
            settingsNavController = settingsNavController
        )
    }
}

@Composable
fun SearchPreferences() {
    val settingsViewModel = viewModel<SettingsViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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
}

@Composable
fun DesignPreferences(
    settingsNavController: NavController,
) {
    val settingsViewModel = viewModel<SettingsViewModel>()
    val activity = LocalContext.current as Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TopLevelSettingsCard(
            title = stringResource(id = R.string.preference_heading_dark_mode),
            subtitle = stringResource(R.string.preference_subtitle_dark_mode),
            icon = Icons.Outlined.DarkMode,
            route = SettingsNavItem.DarkMode.route,
            settingsNavController = settingsNavController
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
}

@Composable
fun DarkModePreferences() {
    val activity = LocalContext.current as Activity

    val settingsViewModel = viewModel<SettingsViewModel>()

    val isSelected: (Int) -> Boolean = { settingsViewModel.darkTheme.value == it }
    val onChangeState: (Int) -> Unit = {
        settingsViewModel.setDarkMode(it)
        activity.recreate()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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
}

@Composable
fun HomeScreenPreferences() {
    val settingsViewModel = viewModel<SettingsViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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

        Box(
            modifier = Modifier
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
}

@Composable
fun SpecialFeaturePreferences() {
    val settingsViewModel = viewModel<SettingsViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SwitchPreference(
            titleText = stringResource(R.string.preferences_show_next_mcu_title),
            subtitleText = stringResource(R.string.preferences_show_next_mcu_subtitle),
            checkState = settingsViewModel.showNextMcuProduction.collectAsState(),
            onCheckedChange = {
                settingsViewModel.toggleShowNextMcuProduction()
            }
        )
    }
}

@Composable
fun DevPreferences() {
    if (BuildConfig.DEBUG) {
        val settingsViewModel = viewModel<SettingsViewModel>()

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
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

            val applicationViewModel = viewModel<ApplicationViewModel>()
            val currentStoredRoute = remember { applicationViewModel.storedRoute }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Current stored test route", fontSize = 18.sp)
                    Text(text = currentStoredRoute.value.unlessEmpty("---"))
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        applicationViewModel.setStoredRoute("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Restore,
                        contentDescription = null
                    )
                }
            }
        }
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

@Composable
private fun TopLevelSettingsCard(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    route: String,
    settingsNavController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                onClick = {
                    settingsNavController.navigate(route)
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

//private fun resetAllPreferences(
//    preferences: AppPreferences,
//    settingsViewModel: SettingsViewModel,
//    activity: AppCompatActivity
//) {
//    resetSearchPreferences(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
//    resetDesignPreferences(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
//    resetHomeScreenPreferences(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
//    resetDevModePreference(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
//    activity.recreate()
//    Toast.makeText(activity, "All settings reset successfully", Toast.LENGTH_SHORT).show()
//}
//
//private fun resetSearchPreferences(
//    preferences: AppPreferences,
//    settingsViewModel: SettingsViewModel,
//    activity: AppCompatActivity,
//    skipRecreate: Boolean = false
//) {
//    settingsViewModel.setShowSearchBar(preferences.showSearchBarDefault)
//    settingsViewModel.setMultiSearch(preferences.multiSearchDefault)
//    if (!skipRecreate) {
//        activity.recreate()
//    }
//}
//
//private fun resetDesignPreferences(
//    preferences: AppPreferences,
//    settingsViewModel: SettingsViewModel,
//    activity: AppCompatActivity,
//    skipRecreate: Boolean = false
//) {
//    settingsViewModel.setUseSystemColors(preferences.useSystemColorsDefault)
//    settingsViewModel.setUseWallpaperColors(preferences.useWallpaperColorsDefault)
//    settingsViewModel.setChromaMultiplier(preferences.chromaMultiplierDefault)
//    settingsViewModel.setSelectedColor(preferences.selectedColorDefault)
//    resetDarkModePreferences(preferences = preferences, settingsViewModel = settingsViewModel, activity = activity, skipRecreate = true)
//    if (!skipRecreate) {
//        activity.recreate()
//    }
//}
//
//private fun resetDarkModePreferences(
//    preferences: AppPreferences,
//    settingsViewModel: SettingsViewModel,
//    activity: AppCompatActivity,
//    skipRecreate: Boolean = false
//) {
//    settingsViewModel.setDarkMode(preferences.darkThemeDefault)
//    if (!skipRecreate) {
//        activity.recreate()
//    }
//}
//
//private fun resetDevModePreference(
//    preferences: AppPreferences,
//    settingsViewModel: SettingsViewModel,
//    activity: AppCompatActivity,
//    skipRecreate: Boolean = false
//) {
//    settingsViewModel.setFirstLaunchTesting(preferences.firstLaunchTestingDefault)
//    settingsViewModel.setShowBackdropGallery(preferences.showBackdropGalleryDefault)
//    if (!skipRecreate) {
//        activity.recreate()
//    }
//}
//
//private fun resetHomeScreenPreferences(
//    preferences: AppPreferences,
//    settingsViewModel: SettingsViewModel,
//    activity: AppCompatActivity,
//    skipRecreate: Boolean = false
//) {
//    settingsViewModel.setShowBottomTabLabels(preferences.showBottomTabLabelsDefault)
//    settingsViewModel.setShowPosterTitles(preferences.showPosterTitlesDefault)
//    preferences.moviesTabPosition = preferences.moviesTabPositionDefault
//    preferences.tvTabPosition = preferences.tvTabPositionDefault
//    preferences.peopleTabPosition = preferences.peopleTabPositionDefault
//    preferences.accountTabPosition = preferences.accountTabPositionDefault
//    preferences.showBottomTabLabels = preferences.showBottomTabLabelsDefault
//    if (!skipRecreate) {
//        activity.recreate()
//    }
//}