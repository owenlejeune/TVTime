package com.owenlejeune.tvtime.ui.views.extras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.ContentCard
import com.owenlejeune.tvtime.ui.viewmodel.SpecialFeaturesViewModel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

object SpecialFeaturesViews {

    val viewsMap: Map<Int, @Composable () -> Unit> = mapOf(
        206584 to { GotQuotesView() },
        1399 to { GotQuotesView() }
    )

}

@Composable
fun GotQuotesView(
    appPreferences: AppPreferences = get(AppPreferences::class.java)
) {
    if (appPreferences.showGotQuotes) {
        val scope = rememberCoroutineScope()

        val viewModel = viewModel<SpecialFeaturesViewModel>()
        LaunchedEffect(Unit) {
            viewModel.getGotQuotes()
        }

        val quotes = remember { viewModel.gotQuotes }
        if (quotes.isNotEmpty()) {
            ContentCard(
                heading = {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.quotes_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            modifier = Modifier.offset(x = 8.dp, y = (-8).dp),
                            onClick = {
                                scope.launch { viewModel.getGotQuotes() }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null
                            )
                        }
                    }
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = (-12).dp)
                ) {
                    quotes.forEachIndexed { index, quote ->
                        Column {
                            Text(
                                text = quote.quote,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " - ${quote.character.name}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = FontStyle.Italic
                            )
                        }

                        if (index != quotes.size - 1) {
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(1.dp))
                }
            }
        }
    }
}