package com.konkuk.hackathon_team3.presentation.minseok.ranking

import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.presentation.model.RankingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

}