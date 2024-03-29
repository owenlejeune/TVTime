package com.owenlejeune.tvtime.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ALPHA = 0.7f

@Composable
fun ProfileMenuOverlay(
    appNavController: NavController,
    visible: Boolean,
    onDismissRequest: () -> Unit
) {
    if (visible) {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = ALPHA))
                .clickable(onClick = onDismissRequest)
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(vertical = 100.dp, horizontal = 24.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f).compositeOver(MaterialTheme.colorScheme.primaryContainer),
                    contentColor = Color.White.copy(alpha = 0.8f).compositeOver(MaterialTheme.colorScheme.onPrimaryContainer)
                )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    val currentSessionState = remember { SessionManager.currentSession }
                    val currentSession = currentSessionState.value

                    currentSession?.let {
                        ProfileMenuItem {
                            AccountIcon(
                                size = 48.dp,
                                enabled = false
                            )
                            Text(
                                text = currentSession.accountDetails.value?.name ?: "",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } ?: run {
                        ProfileMenuItem {
                            Text(
                                text = stringResource(id = R.string.account_not_logged_in),
                                fontSize = 16.sp
                            )
                        }
                    }

                    MenuDivider()

                    currentSession?.let {
                        ProfileMenuItem(
                            onClick = {
                                onDismissRequest()
                                appNavController.navigate(AppNavItem.AccountView.route)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(id = R.string.nav_account_title),
                                fontSize = 16.sp
                            )
                        }
                    }

                    ProfileMenuItem(
                        onClick = {
                            onDismissRequest()
                            appNavController.navigate(AppNavItem.SettingsView.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.nav_settings_title),
                            fontSize = 16.sp
                        )
                    }

                    ProfileMenuItem(
                        onClick = {
                            onDismissRequest()
                            appNavController.navigate(AppNavItem.AboutView.route)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.nav_about_title),
                            fontSize = 16.sp
                        )
                    }

                    MenuDivider()

                    ProfileMenuItem(
                        onClick = {
                            onDismissRequest()
                            CoroutineScope(Dispatchers.IO).launch {
                                if (currentSession != null) {
                                    SessionManager.clearSession()
                                } else {
                                    SessionManager.signInPart1(context) {
                                        appNavController.navigate(
                                            AppNavItem.WebLinkView.withArgs(it)
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        if (currentSession != null) {
                            Icon(
                                imageVector = Icons.Outlined.Logout,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(id = R.string.action_sign_out),
                                fontSize = 16.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Login,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(id = R.string.action_sign_in),
                                fontSize = 16.sp
                            )
                        }
                    }

                    MenuDivider()

                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stringResource(id = R.string.app_name)} v${BuildConfig.VERSION_NAME}",
                            fontSize = 10.sp
                        )
                        Text(text = "•")
                        Icon(
                            painter = painterResource(id = R.drawable.tmdb_logo),
                            tint = Color.Unspecified,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.powered_by_tmdb),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuDivider() {
    MyDivider(modifier = Modifier.padding(vertical = 9.dp))
}

@Composable
private fun ProfileMenuItem(
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .padding(horizontal = 4.dp)
            .clickable(
                enabled = onClick != null,
                onClick = onClick ?: {}
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 9.dp)
                .fillMaxWidth(),
            content = content
        )
    }
}

class ProfileMenuColors internal constructor(val statusBarColor: Color, val navBarColor: Color)

object ProfileMenuDefaults {

    @Composable
    fun systemBarColors(
        statusBarColor: Color = MaterialTheme.colorScheme.background,
        navBarColor: Color = MaterialTheme.colorScheme.background
    ) = ProfileMenuColors(statusBarColor, navBarColor)
}

@Composable
fun ProfileMenuContainer(
    colors: ProfileMenuColors = ProfileMenuDefaults.systemBarColors(),
    appNavController: NavController,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val statusBarColor = if (visible) {
        Color.Black.copy(alpha = ALPHA).compositeOver(background = colors.statusBarColor)
    } else {
        colors.statusBarColor
    }
    val navBarColor = if (visible) {
        Color.Black.copy(alpha = ALPHA).compositeOver(background = colors.navBarColor)
    } else {
        colors.navBarColor
    }

    val applicationViewModel = viewModel<ApplicationViewModel>()
    applicationViewModel.statusBarColor.value = statusBarColor
    applicationViewModel.navigationBarColor.value = navBarColor

    Box {
        content()

        ProfileMenuOverlay(
            visible = visible,
            onDismissRequest = onDismissRequest,
            appNavController = appNavController
        )
    }
}