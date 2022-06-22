package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.*
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

    private val preferences: AppPreferences by inject()

    private lateinit var pagerState: PagerState
    private lateinit var coroutineScope: CoroutineScope

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
    }

    @Composable
    fun OnboardingUi() {
        pagerState = rememberPagerState()
        coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
//                    .background(MaterialTheme.colorScheme.primary),
                count = OnboardingPage.values().size,
                userScrollEnabled = false
            ) { page ->
                OnboardingPageUi(page = OnboardingPage[page])
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp)
            ) {
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    activeColor = MaterialTheme.colorScheme.secondary
                )

                val isLastPage = pagerState.currentPage == OnboardingPage.values().size - 1
                Button(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    shape = CircleShape,
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
                    if (isLastPage) {
                        Text(stringResource(id = R.string.get_started))
                    } else {
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }

    @Composable
    fun OnboardingPageUi(page: OnboardingPage) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            page.image?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.secondary)
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
            Spacer(modifier = Modifier.height(12.dp))
            page.additionalContent(this@OnboardingActivity)
        }
    }

    override fun onBackPressed() {
        if (pagerState.currentPage == 0) {
            finish()
        } else {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
        }
    }
}