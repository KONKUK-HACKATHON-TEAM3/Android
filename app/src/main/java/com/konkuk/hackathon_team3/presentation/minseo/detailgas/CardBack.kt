package com.konkuk.hackathon_team3.presentation.minseo.detailgas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.minseok.feed.FeedUiState
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle
import com.konkuk.hackathon_team3.ui.theme.regularStyle

@Composable
fun LikeHeart(
    liked: Boolean,
    count: Int,
    onToggle: (newLiked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAnimating by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("Fire.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = isAnimating,
        speed = 1.2f,
        restartOnPlay = true
    )

    LaunchedEffect(isAnimating, progress) {
        if (isAnimating && progress >= 1f) {
            isAnimating = false
            onToggle(true)
        }
    }

    val displayCount =
        if (isAnimating && !liked) count + 1
        else count

    Row(
        modifier = modifier.noRippleClickable {
            if (!liked && !isAnimating) {
                isAnimating = true
            } else if (liked && !isAnimating) {
                onToggle(false)
            }
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = displayCount.toString())
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isAnimating) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(35.dp).padding(bottom = 5.dp)
                )
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        if (liked) R.drawable.ic_like_true else R.drawable.ic_list_false
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun CardBack(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: FeedUiState = FeedUiState(),
    onToggleLike: (feedId: Long, newLiked: Boolean) -> Unit
) {
    Column(
        modifier = modifier
            .roundedBackgroundWithPadding(
                backgroundColor = Color.White,
                cornerRadius = 10.dp
            )
    ) {
        GasTopbar(backButtonClicked = onCancel)
        Spacer(modifier = Modifier.height(11.dp))
        LazyColumn(
            modifier = Modifier
                .gasComponentDesign()
                .padding(horizontal = 11.dp),
        ) {
            itemsIndexed(uiState.feedList) { index, feed ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = feed.profile.profileImage),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = feed.nickname,
                        fontSize = 12.sp,
                        style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
                    )
                }
                Spacer(modifier = Modifier.height(13.dp))
                if (!feed.text.isNullOrBlank()) {
                    Text(
                        text = feed.text,
                        fontSize = 12.sp,
                        style = KONKUKHACKATHONTEAM3Theme.typography.regularStyle
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(feed.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(13.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${feed.tag}",
                        fontSize = 12.sp,
                        style = KONKUKHACKATHONTEAM3Theme.typography.regularStyle
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    LikeHeart(
                        liked = feed.likeStatus,
                        count = feed.likeCount,
                        onToggle = { newLiked ->
                            onToggleLike(feed.feedId, newLiked)
                        }
                    )

                }
                Spacer(modifier = Modifier.height(16.dp))

                if (index != uiState.feedList.lastIndex) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = Color(0xFFD5D5D5)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    KONKUKHACKATHONTEAM3Theme {
    }
}