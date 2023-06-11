package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.owenlejeune.tvtime.extensions.rememberWindowSizeClass
import com.owenlejeune.tvtime.ui.navigation.AppNavigationHost
import com.owenlejeune.tvtime.ui.navigation.HomeScreenNavItem
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme
import com.owenlejeune.tvtime.utils.KeyboardManager
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : MonetCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            SessionManager.initialize()
        }

        val mainNavStartRoute = HomeScreenNavItem.SortedItems[0].route

        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()
            setContent {
                AppKeyboardFocusManager()
                TVTimeTheme(monetCompat = monet) {
                    val windowSize = rememberWindowSizeClass()
                    val appNavController = rememberNavController()
                    Box {
                       AppNavigationHost(
                           appNavController = appNavController,
                           mainNavStartRoute = mainNavStartRoute,
                           windowSize = windowSize
                       )
                    }
                }
            }
        }
    }

    @Composable
    private fun AppKeyboardFocusManager() {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current
        DisposableEffect(key1 = context) {
            val keyboardManager = KeyboardManager.getInstance(context)
            keyboardManager.attachKeyboardDismissListener {
                focusManager.clearFocus()
            }
            onDispose {
                keyboardManager.release()
            }
        }
    }
}