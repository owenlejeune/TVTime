package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.owenlejeune.tvtime.extensions.launchActivity
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.viewmodel.ConfigurationViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel

class AppRoutingActivity: AppCompatActivity() {

    private val preferences: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val configurationViewModel = getViewModel<ConfigurationViewModel>()
                configurationViewModel.getConfigurations()
                TmdbUtils.setup(configurationViewModel)

                if (preferences.firstLaunchTesting || preferences.firstLaunch) {
                    launchActivity(OnboardingActivity::class.java)
                } else {
                    launchActivity(MainActivity::class.java)
                }
            }
        }
    }

}