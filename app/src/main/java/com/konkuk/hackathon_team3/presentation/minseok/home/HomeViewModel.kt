package com.konkuk.hackathon_team3.presentation.minseok.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.hackathon_team3.data.mapper.toHomeFamilyData
import com.konkuk.hackathon_team3.data.mapper.toHomeRecentFeedData
import com.konkuk.hackathon_team3.data.mapper.toMissionData
import com.konkuk.hackathon_team3.data.mapper.toRankingData
import com.konkuk.hackathon_team3.data.service.ServicePool
import com.konkuk.hackathon_team3.presentation.model.HomeFamilyData
import com.konkuk.hackathon_team3.presentation.model.HomeRecentFeedData
import com.konkuk.hackathon_team3.presentation.model.MissionData
import com.konkuk.hackathon_team3.presentation.model.RankingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    private val homeService by lazy { ServicePool.homeService }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome() // 초기화 시점에 호출
    }

    fun loadHome(memberId: Long = 1) {
        viewModelScope.launch {
            try {
                val data = homeService.getHome(memberId)

                _uiState.value = _uiState.value.copy(
                    missionList = data.dailyMissionList.map { it.toMissionData() },
                    recentFeedList = data.familyStoryList?.map { it.toHomeRecentFeedData() } ?: emptyList(),
                    rankingList = data.weeklyRanking.toRankingData(),
                    familyList = data.familyList.map { it.toHomeFamilyData() }
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "loadHome failed: ${e.message}", e)
            }
        }
    }
}
