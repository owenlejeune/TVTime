package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.utils.types.MediaViewType

@Composable
fun MediaResultCard(
    appNavController: NavController,
    mediaViewType: MediaViewType,
    id: Int,
    backdropPath: Any?,
    posterPath: Any?,
    title: String,
    additionalDetails: List<String>,
    modifier: Modifier = Modifier,
    rating: Float? = null
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.then(Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    appNavController.navigate(
                        AppNavItem.DetailView.withArgs(mediaViewType, id)
                    )
                }
            )
        )
    ) {
        Box(
            modifier = Modifier.height(112.dp)
        ) {
            backdropPath?.let {
                val model = ImageRequest.Builder(LocalContext.current)
                    .data(backdropPath)
                    .diskCacheKey(backdropPath.toString())
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .blur(radius = 10.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                )

                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .blur(radius = 10.dp)
                )
            }

            ConstraintLayout(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                val (poster, content, ratingView) = createRefs()

                val model = ImageRequest.Builder(LocalContext.current)
                    .data(posterPath)
                    .diskCacheKey(posterPath?.toString() ?: "")
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
                AsyncImage(
                    modifier = Modifier
                        .constrainAs(poster) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }
                        .aspectRatio(0.7f)
                        .clip(RoundedCornerShape(10.dp)),
                    model = model,
                    contentDescription = title
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .constrainAs(content) {
                            end.linkTo(
                                rating?.let { ratingView.start } ?: parent.end,
                                margin = 12.dp
                            )
                            start.linkTo(poster.end, margin = 12.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.matchParent
                        }
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    val textColor = backdropPath?.let { Color.White } ?: if (isSystemInDarkTheme()) Color.White else Color.Black
                    Text(
                        text = title,
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    additionalDetails.forEach {
                        Text(
                            text = it,
                            color = textColor,
                            fontSize = 14.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                rating?.let {
                    RatingView(
                        progress = rating / 10f,
                        modifier = Modifier
                            .constrainAs(ratingView) {
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                    )
                }
            }
        }
    }
}