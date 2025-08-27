package com.konkuk.hackathon_team3.presentation.minseok

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.model.RankingData
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeRankingComponent(
    navigateToRanking: () -> Unit,
    rankings: List<RankingData>,
    modifier: Modifier = Modifier,
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000L)
            currentIndex = (currentIndex + 1) % rankings.size.coerceAtLeast(1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .roundedBackgroundWithPadding(
                backgroundColor = Color.White,
                cornerRadius = 16.dp,
                padding = PaddingValues(vertical = 24.dp, horizontal = 20.dp)
            )
            .noRippleClickable(onClick = navigateToRanking),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "이번 주 순위", color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier
                .clipToBounds()
        ) {
            Box(Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = rankings.getOrNull(currentIndex),
                    transitionSpec = {
                        slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                    },
                    label = "RankingRolling"
                ) { ranking ->
                    if (ranking != null) {
                        RankingItem(rankingData = ranking)
                    }
                }
            }

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_right_arrow_16),
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

@Composable
fun RankingItem(modifier: Modifier = Modifier, rankingData: RankingData) {

    Column(modifier = modifier) {
        Text(
            text = "${rankingData.rank}위 ${rankingData.nickname}",
            style = TextStyle(
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${rankingData.point}점",
            style = TextStyle(
                color = Color.Black,
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Preview
@Composable
private fun HomeRankingComponentPreview() {
    val rankings = listOf(
        RankingData(
            rank = 1,
            nickname = "아빠더",
            point = 1500
        ),
        RankingData(
            rank = 2,
            nickname = "엄마미",
            point = 1000
        ),
        RankingData(
            rank = 3,
            nickname = "아가짱",
            point = 500
        )
    )
    HomeRankingComponent(rankings = rankings, navigateToRanking = {})
}