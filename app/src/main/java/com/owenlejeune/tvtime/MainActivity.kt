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
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            SessionManager.initialize()
        }

        setContent {
            AppKeyboardFocusManager()
            MyApp(
                appNavController = rememberNavController()
            )
        }
    }
}

@Composable
fun MyApp(
    appNavController: NavHostController = rememberNavController()
) {
    TVTimeTheme {
        Box {
            MainNavigationRoutes(navController = appNavController)
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