package com.owenlejeune.tvtime.ui.screens.onboarding

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.RadioButtonPreference
import com.owenlejeune.tvtime.ui.components.SearchBar
import com.owenlejeune.tvtime.ui.components.SwitchPreference
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class OnboardingPage(
    val order: Int,
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int? = null,
    val additionalContent: @Composable (AppCompatActivity, MutableState<Boolean>) -> Unit = { a, e -> }
) {

    companion object: KoinComponent {
        private val preferences: AppPreferences by inject()

        fun values() = listOf(IntroPage, SearchViewSettings, SearchTypeSettings, DarkModePage, SystemColorPage).sortedBy { it.order }

        operator fun get(i: Int) = values()[i]
    }

    object IntroPage: OnboardingPage(
        order = 0,
        title = R.string.app_name,
        description = R.string.intro_page_desc,
        image = R.drawable.ic_launcher_foreground
    )

    object SignInPage: OnboardingPage(
        order = 1,
        title = R.string.sign_in_page_title,
        description = R.string.sign_in_page_description,
        image = null,
        additionalContent = @Composable { activity, nextButtonEnabled ->

        }
    )

    object SearchViewSettings: OnboardingPage(
        order = 2,
        title = R.string.search_page_title,
        description = R.string.search_page_description,
        image = R.drawable.ic_search,
        additionalContent = @Composable { _, _ ->
            val persistentSearch = remember { mutableStateOf(preferences.showSearchBar) }

            if (persistentSearch.value) {
                SearchBar(placeholder = "") {}
            } else {
                FloatingActionButton(onClick = {}) {
                    Icon(
                        Icons.Filled.Search,
                        stringResource(id = R.string.preference_heading_search)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SwitchPreference(
                titleText = stringResource(R.string.preferences_persistent_search_title),
                subtitleText = stringResource(R.string.preferences_persistent_search_subtitle),
                checkState = persistentSearch.value,
                onCheckedChange = { isChecked ->
                    persistentSearch.value = isChecked
                    preferences.showSearchBar = isChecked
                }
            )
        }
    )

    object SearchTypeSettings: OnboardingPage(
        order = 3,
        title = R.string.search_type_title,
        description = R.string.search_type_description,
        image = R.drawable.ic_search,
        additionalContent = @Composable { _, _ ->
            val multiSearch = remember { mutableStateOf(preferences.multiSearch) }
            SwitchPreference(
                titleText = stringResource(R.string.preference_multi_search_title),
                subtitleText = stringResource(R.string.preference_multi_search_subtitle),
                checkState = multiSearch.value,
                onCheckedChange = { isChecked ->
                    multiSearch.value = isChecked
                    preferences.multiSearch = isChecked
                }
            )
        }
    )

    object DarkModePage: OnboardingPage(
        order = 4,
        title = R.string.dark_mode_page_title,
        description = R.string.dark_mode_page_description,
        image = R.drawable.ic_baseline_dark_mode,
        additionalContent = @Composable { activity, _ ->
            val selectedValue = remember { mutableStateOf(preferences.darkTheme) }

            val isSelected: (Int) -> Boolean = { selectedValue.value == it }
            val onChangeState: (Int) -> Unit = {
                selectedValue.value = it
                preferences.darkTheme = it
                activity.recreate()
            }

            RadioButtonPreference(
                selected = isSelected(AppPreferences.DarkMode.Automatic.ordinal),
                title = stringResource(R.string.preference_dark_mode_follow_system_label),
                description = "Automatically change between dark and light mode",
                icon = Icons.Filled.Brightness6,
                onClick = {
                    onChangeState(AppPreferences.DarkMode.Automatic.ordinal)
                }
            )
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
    )

    object SystemColorPage: OnboardingPage(
        order = 4,
        title = R.string.system_color_page_title,
        description = R.string.system_color_page_description,
        image = R.drawable.ic_baseline_palette,
        additionalContent = @Composable { activity, _ ->
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
        }
    )

}