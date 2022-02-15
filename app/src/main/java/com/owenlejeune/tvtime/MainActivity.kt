package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.ui.navigation.MainNavigationRoutes
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val displayUnderStatusBar = remember { mutableStateOf(false) }
//            WindowCompat.setDecorFitsSystemWindows(window, !displayUnderStatusBar.value)
//            val statusBarColor = if (displayUnderStatusBar.value) {
//                Color.Transparent
//            } else {
//                MaterialTheme.colorScheme.background
//            }
//            val systemUiController = rememberSystemUiController()
//            systemUiController.setStatusBarColor(statusBarColor, !isSystemInDarkTheme())
            MyApp(displayUnderStatusBar = displayUnderStatusBar)
        }
    }
}

@Composable
fun MyApp(displayUnderStatusBar: MutableState<Boolean> = mutableStateOf(false)) {
    TVTimeTheme {
        val appNavController = rememberNavController()
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