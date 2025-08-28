package com.konkuk.hackathon_team3.presentation.minseo.detailgas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.minseok.feed.FeedUiState
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding

@Composable
fun CardBack(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: FeedUiState = FeedUiState()
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
                    Text(text = feed.nickname)
                }
                Spacer(modifier = Modifier.height(13.dp))
                Text(text = feed.text ?: "")
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
                    Text(text = "${feed.tag}")
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.noRippleClickable()) {
                        Text(text = "${feed.likeCount}")
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_list_false),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
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