package com.konkuk.hackathon_team3.presentation.minseok.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.minseo.component.GasHomeCalendar
import com.konkuk.hackathon_team3.presentation.minseok.HomeRankingComponent
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

@Composable
fun MainRoute(
    navigateToRanking: () -> Unit,
    navigateToGasWriting: () -> Unit,
    navigateToAddFamily: () -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToAlarm: () -> Unit,
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
        isRefreshing = uiState.isLoading, // 추가
        onRefresh = { viewModel.loadHome() }
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
    modifier: Modifier = Modifier,
    uiState: HomeUiState = HomeUiState(),
    isRefreshing: Boolean = false, // 수정
    onRefresh: () -> Unit = {}
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyColumn {
            item {
                GasTopbar(
                    isHomeScreen = true,
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
                    items(
                        items = uiState.missionList
                    ) { mission ->
                        HomeMissionItem(
                            missionData = mission,
                        )
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
                        .padding(vertical = 24.dp, horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "가족 스토리")
                    Text(text = "최근 업데이트: 2시간 전")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        uiState.recentFeedList.forEach { homeRecentFeedData ->
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = homeRecentFeedData.nickname)
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
                GasHomeCalendar()
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                HomeAddFamilyComponent(
                    familyList = uiState.familyList
                )
                Spacer(modifier = Modifier.height(50.dp))
            }
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
            navigateToAlarm = {}
        )
    }
}