package com.konkuk.hackathon_team3.presentation.minseok.home

import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.presentation.model.HomeFamilyData
import com.konkuk.hackathon_team3.presentation.model.HomeRecentFeedData
import com.konkuk.hackathon_team3.presentation.model.MissionData
import com.konkuk.hackathon_team3.presentation.model.RankingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val missionList: List<MissionData> = listOf(
        MissionData(
            title = "오늘의 사진",
            point = 300,
            description = "현재 모습을 찍어 올려보세요.",
            isCleared = true
        ),
        MissionData(
            title = "오늘의 사진",
            point = 300,
            description = "현재 모습을 찍어 올려보세요.",
            isCleared = false
        ),
        MissionData(
            title = "오늘의 사진",
            point = 300,
            description = "현재 모습을 찍어 올려보세요.",
            isCleared = true
        )
    ),
//    emptyList<MissionData>(),
    val recentFeedList: List<HomeRecentFeedData> = listOf(
        HomeRecentFeedData(
            nickname = "신민석",
            imageUrl = ""
        ),
        HomeRecentFeedData(
            nickname = "송민서",
            imageUrl = ""
        ),
        HomeRecentFeedData(
            nickname = "서아영",
            imageUrl = ""
        ),
        HomeRecentFeedData(
            nickname = "김창균",
            imageUrl = ""
        ),
    ),
    val rankingList: List<RankingData> = listOf(
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
    ),

    val familyList: List<HomeFamilyData> = listOf(
        HomeFamilyData(
            nickname = "신민석",
            profileEnum = ""
        ),
        HomeFamilyData(
            nickname = "송민서",
            profileEnum = ""
        ),
        HomeFamilyData(
            nickname = "서아영",
            profileEnum = ""
        ),
        HomeFamilyData(
            nickname = "김창균",
            profileEnum = ""
        ),
    ),
)


class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

}