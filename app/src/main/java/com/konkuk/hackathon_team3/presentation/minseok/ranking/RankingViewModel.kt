package com.konkuk.hackathon_team3.presentation.minseok.ranking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.hackathon_team3.data.mapper.toHomeFamilyData
import com.konkuk.hackathon_team3.data.mapper.toHomeRecentFeedData
import com.konkuk.hackathon_team3.data.mapper.toMissionData
import com.konkuk.hackathon_team3.data.mapper.toRankingData
import com.konkuk.hackathon_team3.data.mapper.toRankingDataList
import com.konkuk.hackathon_team3.data.service.ServicePool
import com.konkuk.hackathon_team3.presentation.model.RankingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RankingUiState(
    val price: String = "심부름 면제권",
    val rankingList: List<RankingData>?=listOf(
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
)


class RankingViewModel : ViewModel() {

    private val rankingService by lazy { ServicePool.rankingService }


    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    init {
        loadRanking()
    }

    fun loadRanking(memberId: Long = 1) {
        viewModelScope.launch {

            try {
                val data = rankingService.getRanking(memberId)

                _uiState.value = _uiState.value.copy(
                    price = data.weeklyPrize,
                    rankingList = data.weeklyRanking.toRankingDataList()
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "loadHome failed: ${e.message}", e)
            }
        }
    }

}