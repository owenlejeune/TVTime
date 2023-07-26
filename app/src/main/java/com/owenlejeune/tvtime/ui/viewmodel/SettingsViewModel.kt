package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.owenlejeune.tvtime.preferences.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel: ViewModel() {

    private companion object Backer: KoinComponent {
        private val preferences: AppPreferences by inject()

        private val _showSearchBar = MutableStateFlow(preferences.showSearchBar)
        private val _useMultiSearch = MutableStateFlow(preferences.multiSearch)
        private val _useWallpaperColor = MutableStateFlow(preferences.useWallpaperColors)
        private val _darkTheme = MutableStateFlow(preferences.darkTheme)
        private val _useSystemColors = MutableStateFlow(preferences.useSystemColors)
        private val _chromaMultiplier = MutableStateFlow(preferences.chromaMultiplier)
        private val _selectedColor = MutableStateFlow(preferences.selectedColor)
        private val _showBottomTabLabels = MutableStateFlow(preferences.showBottomTabLabels)
        private val _showPosterTitles = MutableStateFlow(preferences.showPosterTitles)
        private val _firstLaunchTesting = MutableStateFlow(preferences.firstLaunchTesting)
        private val _showBackdropGallery = MutableStateFlow(preferences.showBackdropGallery)
        private val _showNextMcuProduction = MutableStateFlow(preferences.showNextMcuProduction)
        private val _showFloatingBottomBar = MutableStateFlow(preferences.floatingBottomBar)
    }

    val showSearchBar = _showSearchBar.asStateFlow()
    val useMultiSearch = _useMultiSearch.asStateFlow()
    val useWallpaperColors = _useWallpaperColor.asStateFlow()
    val darkTheme = _darkTheme.asStateFlow()
    val useSystemColors = _useSystemColors.asStateFlow()
    val chromaMultiplier = _chromaMultiplier.asStateFlow()
    val selectedColor = _selectedColor.asStateFlow()
    val showBottomTabLabels = _showBottomTabLabels.asStateFlow()
    val showPosterTitles = _showPosterTitles.asStateFlow()
    val firstLaunchTesting = _firstLaunchTesting.asStateFlow()
    val showBackdropGallery = _showBackdropGallery.asStateFlow()
    val showNextMcuProduction = _showNextMcuProduction.asStateFlow()
    val showFloatingBottomBar = _showFloatingBottomBar.asStateFlow()

    fun toggleShowSearchBar() {
        _showSearchBar.value = _showSearchBar.value.not()
        preferences.showSearchBar = _showSearchBar.value
    }

    fun setShowSearchBar(value: Boolean) {
        _showSearchBar.value = value
        preferences.showSearchBar = value
    }

    fun toggleMultiSearch() {
        _useMultiSearch.value = _useMultiSearch.value.not()
        preferences.multiSearch = _useMultiSearch.value
    }

    fun setMultiSearch(value: Boolean) {
        _useMultiSearch.value = value
        preferences.multiSearch = value
    }

    fun toggleUseWallpaperColors() {
        _useWallpaperColor.value = _useWallpaperColor.value.not()
        preferences.useWallpaperColors = _useWallpaperColor.value
    }

    fun setUseWallpaperColors(value: Boolean) {
        _useWallpaperColor.value = value
        preferences.useWallpaperColors = value
    }

    fun setDarkMode(darkMode: Int) {
        _darkTheme.value = darkMode
        preferences.darkTheme = _darkTheme.value
    }

    fun toggleUseSystemColors() {
        _useSystemColors.value = _useSystemColors.value.not()
        preferences.useSystemColors = _useSystemColors.value
    }

    fun setUseSystemColors(value: Boolean) {
        _useSystemColors.value = value
        preferences.useSystemColors = value
    }

    fun setChromaMultiplier(multiplier: Double) {
        _chromaMultiplier.value = multiplier
        preferences.chromaMultiplier = _chromaMultiplier.value
    }

    fun setSelectedColor(selectedColor: Int) {
        _selectedColor.value = selectedColor
        preferences.selectedColor = _selectedColor.value
    }

    fun toggleShowBottomTabLabels() {
        _showBottomTabLabels.value = _showBottomTabLabels.value.not()
        preferences.showBottomTabLabels = _showBottomTabLabels.value
    }

    fun setShowBottomTabLabels(value: Boolean) {
        _showBottomTabLabels.value = value
        preferences.showBottomTabLabels = value
    }

    fun toggleShowPosterTitles() {
        _showPosterTitles.value = _showPosterTitles.value.not()
        preferences.showPosterTitles = _showPosterTitles.value
    }

    fun setShowPosterTitles(value: Boolean) {
        _showPosterTitles.value = value
        preferences.showPosterTitles = value
    }

    fun toggleFirstLaunchTesting() {
        _firstLaunchTesting.value = _firstLaunchTesting.value.not()
        preferences.firstLaunchTesting = _firstLaunchTesting.value
    }

    fun setFirstLaunchTesting(value: Boolean) {
        _firstLaunchTesting.value = value
        preferences.firstLaunchTesting = value
    }

    fun toggleShowBackdropGallery() {
        _showBackdropGallery.value = _showBackdropGallery.value.not()
        preferences.showBackdropGallery = _showBackdropGallery.value
    }

    fun setShowBackdropGallery(value: Boolean) {
        _showBackdropGallery.value = value
        preferences.showBackdropGallery = value
    }

    fun toggleShowNextMcuProduction() {
        _showNextMcuProduction.value = _showNextMcuProduction.value.not()
        preferences.showNextMcuProduction = _showNextMcuProduction.value
    }

    fun setShowNextMcuProduction(value: Boolean) {
        _showNextMcuProduction.value = value
        preferences.showNextMcuProduction = value
    }

    fun toggleShowFloatingBottomBar() {
        _showFloatingBottomBar.value = _showFloatingBottomBar.value.not()
        preferences.floatingBottomBar = _showFloatingBottomBar.value
    }

    fun setShowFloatingBottomBar(value: Boolean) {
        _showFloatingBottomBar.value = value
        preferences.floatingBottomBar = value
    }

}