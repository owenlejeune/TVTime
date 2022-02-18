package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.owenlejeune.tvtime.ui.navigation.MainNavigationRoutes
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme
import com.owenlejeune.tvtime.utils.KeyboardManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppKeyboardFocusManager()
            val displayUnderStatusBar = remember { mutableStateOf(false) }
//            WindowCompat.setDecorFitsSystemWindows(window, !displayUnderStatusBar.value)
//            val statusBarColor = if (displayUnderStatusBar.value) {
//                Color.Transparent
//            } else {
//                MaterialTheme.colorScheme.background
//            }
//            val systemUiController = rememberSystemUiController()
//            systemUiController.setStatusBarColor(statusBarColor, !isSystemInDarkTheme())
            MyApp(
                appNavController = rememberNavController(),
                displayUnderStatusBar = displayUnderStatusBar
            )
        }
    }
}

@Composable
fun MyApp(
    appNavController: NavHostController = rememberNavController(),
    displayUnderStatusBar: MutableState<Boolean> = mutableStateOf(false)
) {
    TVTimeTheme {
        Box {
            MainNavigationRoutes(navController = appNavController, displayUnderStatusBar = displayUnderStatusBar)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
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