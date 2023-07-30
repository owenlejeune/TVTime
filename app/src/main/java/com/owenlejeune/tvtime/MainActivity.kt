package com.owenlejeune.tvtime

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.owenlejeune.tvtime.extensions.copy
import com.owenlejeune.tvtime.extensions.rememberWindowSizeClass
import com.owenlejeune.tvtime.ui.navigation.AppNavigationHost
import com.owenlejeune.tvtime.ui.navigation.HomeScreenNavItem
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme
import com.owenlejeune.tvtime.ui.viewmodel.ConfigurationViewModel
import com.owenlejeune.tvtime.utils.KeyboardManager
import com.owenlejeune.tvtime.utils.NetworkStatus
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : MonetCompatActivity() {

    @SuppressLint("AutoboxingStateValueProperty")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            SessionManager.initialize()
        }

        val mainNavStartRoute = HomeScreenNavItem.SortedItems[0].route

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                monet.awaitMonetReady()

                WindowCompat.setDecorFitsSystemWindows(window, false)

                setContent {
                    AppKeyboardFocusManager()
                    TVTimeTheme(monetCompat = monet) {
                        val snackbarHostState = SnackbarHostState()

                        val configViewModel = viewModel<ConfigurationViewModel>()
                        val networkStatus = configViewModel.networkStatus.collectAsState()
                        if (networkStatus.value == NetworkStatus.Disconnected) {
                            LaunchedEffect(networkStatus) {
                                snackbarHostState.showSnackbar(getString(R.string.network_disconnected_message))
                            }
                        }

                        val lastResponseCode = remember { configViewModel.lastResponseCode }
                        val code = lastResponseCode.value
                        LaunchedEffect(code) {
                            when (code) {
                                429 -> Toast.makeText(
                                    this@MainActivity,
                                    getString(R.string.network_api_rate_limit_reached),
                                    Toast.LENGTH_SHORT
                                ).show()

                                in 400..599 -> Toast.makeText(
                                    this@MainActivity,
                                    getString(R.string.network_error_occurred, code),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        Scaffold(
                            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                        ) { innerPadding ->
                            val windowSize = rememberWindowSizeClass()
                            val appNavController = rememberNavController()
                            Box(modifier = Modifier
                                .padding(innerPadding.copy(bottom = 0.dp, top = 0.dp))
                                .fillMaxSize()
                            ) {
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