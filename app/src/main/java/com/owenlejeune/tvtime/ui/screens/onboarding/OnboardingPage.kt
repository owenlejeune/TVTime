package com.owenlejeune.tvtime.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.owenlejeune.tvtime.R

sealed class OnboardingPage(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int? = null,
    val additionalContent: @Composable (AppCompatActivity) -> Unit = {}
) {

    companion object {
        fun values() = listOf(ExamplePage)

        operator fun get(i: Int) = values()[i]
    }

    object ExamplePage: OnboardingPage(
        R.string.app_name,
        R.string.example_page_desc,
        R.drawable.ic_launcher_foreground
    )

}