package com.owenlejeune.tvtime

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.owenlejeune.tvtime.ui.navigation.BottomNavItem
import com.owenlejeune.tvtime.ui.navigation.BottomNavigationRoutes
import com.owenlejeune.tvtime.ui.navigation.MainNavigationRoutes
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val displayUnderStatusBar = remember { mutableStateOf(false) }
            WindowCompat.setDecorFitsSystemWindows(window, !displayUnderStatusBar.value)
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