package com.owenlejeune.tvtime.ui.screens

import android.content.Context
import android.content.Intent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.owenlejeune.tvtime.ui.components.TVTTopAppBar
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.utils.SessionManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebLinkScreen(
    url: String,
    appNavController: NavController
) {
    val applicationViewModel = viewModel<ApplicationViewModel>()
    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            TVTTopAppBar(
                title = {},
                appNavController = appNavController,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (url.contains("auth")) {
                                SessionManager.cancelSignIn()
                            }
                            appNavController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            val webViewState = rememberWebViewState(url = url)
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize(),
                client = MYmdbWebClient(),
                onCreated = {
                    it.settings.javaScriptCanOpenWindowsAutomatically = true
                    it.settings.useWideViewPort = false
                    it.settings.allowContentAccess = true
                    it.settings.blockNetworkImage = false
                    it.settings.blockNetworkLoads = false
                    it.settings.loadsImagesAutomatically = true
                    it.clipToOutline = false
                    it.isNestedScrollingEnabled = true
                    it.settings.displayZoomControls = true
                    it.settings.setSupportZoom(true)
                    it.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                    it.isVerticalScrollBarEnabled = true
                    it.isHorizontalScrollBarEnabled = true
                    it.settings.domStorageEnabled = true
                    it.settings.databaseEnabled = true
                    it.settings.javaScriptEnabled = true
                    it.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    it.setLayerType(View.LAYER_TYPE_HARDWARE,null)
                    it.postInvalidate()
                    it.isClickable = true
                }
            )
        }
    }
}

class MYmdbWebClient: AccompanistWebViewClient(), KoinComponent {

    private val context: Context by inject()

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request?.url.toString().contains("tvtime")) {
            val uri = request!!.url
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return true
        }
        return super.shouldOverrideUrlLoading(view, request)
    }
}