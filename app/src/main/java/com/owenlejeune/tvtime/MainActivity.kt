package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    TVTimeTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}