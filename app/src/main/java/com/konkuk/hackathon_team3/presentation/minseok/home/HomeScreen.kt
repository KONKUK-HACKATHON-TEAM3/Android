package com.konkuk.hackathon_team3.presentation.minseok.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.minseo.component.GasHomeCalendar
import com.konkuk.hackathon_team3.presentation.minseok.HomeRankingComponent
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle
import com.konkuk.hackathon_team3.ui.theme.regularStyle
import java.time.LocalDate

@Composable
fun MainRoute(
    navigateToRanking: () -> Unit,
    navigateToGasWriting: () -> Unit,
    navigateToAddFamily: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToAlarm: () -> Unit,
    navigateToFeed: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HomerScreen(
        navigateToRanking = navigateToRanking,
        navigateToRecordWrite = navigateToGasWriting,
        navigateToCalendar = navigateToCalendar,
        navigateToAddFamily = navigateToAddFamily,
        navigateToAlarm = navigateToAlarm,
        modifier = modifier,
        uiState = uiState,
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.loadHome() },
        navigateToFeed = navigateToFeed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomerScreen(
    navigateToRanking: () -> Unit,
    navigateToRecordWrite: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToAddFamily: () -> Unit,
    navigateToAlarm: () -> Unit,
    navigateToFeed: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.padding(top = 86.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(modifier = Modifier .blur(radius = 2.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_super_big_gas),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier
                        .padding(start = 36.dp)
                        .blur(radius = 6.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_blur_circle),
                    contentDescription = null,
                    tint = Color.Unspecified
                )

                Icon(
                    modifier = Modifier
                        .padding(bottom = 45.dp)
                        .blur(radius = 8.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_blur_gas),
                    contentDescription = null,
                    tint = Color.Unspecified
                )

            }
            LazyColumn {
                item {
                    GasTopbar(
                        isHomeScreen = true,
                        navigateToNotification = navigateToAlarm
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
                    ) {
                        items(uiState.missionList) { mission ->
                            HomeMissionItem(missionData = mission)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .gasComponentDesign()
                            .noRippleClickable { navigateToFeed(LocalDate.now()) }
                            .padding(vertical = 24.dp, horizontal = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "가족 스토리",
                            fontSize = 14.sp,
                            style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "최근 업데이트: 2시간 전",
                            fontSize = 9.sp,
                            style = KONKUKHACKATHONTEAM3Theme.typography.regularStyle,
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            uiState.recentFeedList.forEach { homeRecentFeedData ->
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = homeRecentFeedData.nickname,
                                        fontSize = 9.sp,
                                        style = KONKUKHACKATHONTEAM3Theme.typography.regularStyle
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(homeRecentFeedData.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "촬영된 이미지",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    HomeRankingComponent(
                        navigateToRanking = navigateToRanking,
                        rankings = uiState.rankingList,
                        modifier = Modifier
                            .gasComponentDesign()
                            .padding(vertical = 24.dp, horizontal = 20.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    GasHomeCalendar(navigateToCalendar = navigateToCalendar)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    HomeAddFamilyComponent(
                        onClick = navigateToAddFamily,
                        familyList = uiState.familyList
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp)
                .size(45.dp)
                .background(color = Color(0xFFFF8514), shape = CircleShape)
                .noRippleClickable(onClick = navigateToRecordWrite),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_floating_plus),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomerScreen() {
    KONKUKHACKATHONTEAM3Theme {
        HomerScreen(
            navigateToRanking = {},
            navigateToRecordWrite = {},
            navigateToCalendar = {},
            navigateToAddFamily = {},
            navigateToAlarm = {},
            navigateToFeed = {}
        )
    }
}