package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.owenlejeune.tvtime.extensions.launchActivity
import com.owenlejeune.tvtime.preferences.AppPreferences
import org.koin.android.ext.android.inject

class AppRoutingActivity: AppCompatActivity() {

    private val preferences: AppPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (preferences.firstLaunchTesting || preferences.firstLaunch) {
            launchActivity(OnboardingActivity::class.java)
        } else {
            launchActivity(MainActivity::class.java)
        }
    }

}