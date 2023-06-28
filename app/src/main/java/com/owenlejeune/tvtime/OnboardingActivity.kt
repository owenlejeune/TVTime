package com.owenlejeune.tvtime

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.BuildCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.owenlejeune.tvtime.extensions.launchActivity
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.screens.onboarding.OnboardingPage
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@OptIn(ExperimentalPagerApi::class)
class OnboardingActivity: MonetCompatActivity() {

    companion object {
        fun showActivity(sourceActivity: Context) {
            val intent = Intent(sourceActivity, OnboardingActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            sourceActivity.startActivity(intent)
        }
    }

    private val preferences: AppPreferences by inject()

    private lateinit var pagerState: PagerState
    private lateinit var coroutineScope: CoroutineScope

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()
            setContent {
                TVTimeTheme(monetCompat = monet) {
                    OnboardingUi()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }
        })
    }

    @Composable
    fun OnboardingUi() {
        pagerState = rememberPagerState()
        coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
        ) {
            val isFirstPage = pagerState.currentPage == 0
            val isLastPage = pagerState.currentPage == OnboardingPage.values().size - 1

            if (!isFirstPage) {
                IconButton(
                    modifier = Modifier.padding(12.dp),
                    onClick = {
                        onBackPressedDispatcher.onBackPressed()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            val nextButtonEnabled = remember { mutableStateOf(true) }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                count = OnboardingPage.values().size,
                userScrollEnabled = false
            ) { page ->
                Box(modifier = Modifier.padding(start = 12.dp, end = 24.dp)) {
                    OnboardingPageUi(page = OnboardingPage[page], nextButtonEnabled)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp)
            ) {
                OnboardingPage[pagerState.currentPage].footer.invoke(this)
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        enabled = true,
                        onClick = {
                            preferences.firstLaunch = false
                            launchActivity(MainActivity::class.java)
                        }
                    ) {
                        val skipText = if (preferences.firstLaunchTesting)
                            stringResource(id = R.string.action_skip_testing)
                        else
                            stringResource(id = R.string.action_skip)
                        Text(text = skipText)
                    }

                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier
                            .padding(16.dp),
                        activeColor = MaterialTheme.colorScheme.secondary
                    )

                    Button(
                        shape = CircleShape,
                        enabled = nextButtonEnabled.value,
                        onClick = {
                            if (isLastPage) {
                                preferences.firstLaunch = false
                                launchActivity(MainActivity::class.java)
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        }
                    ) {
                        if (!isLastPage) {
                            Text(stringResource(id = R.string.get_started))
                        } else {
                            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun OnboardingPageUi(page: OnboardingPage, nextButtonEnabled: MutableState<Boolean>) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            page.image?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            Text(
                text = stringResource(id = page.title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = page.description),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(50.dp))
            page.additionalContent(this@Column, this@OnboardingActivity, nextButtonEnabled)
        }
    }

    private fun handleBackPressed() {
        if (pagerState.currentPage == 0) {
            finish()
        } else {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        }
    }
}