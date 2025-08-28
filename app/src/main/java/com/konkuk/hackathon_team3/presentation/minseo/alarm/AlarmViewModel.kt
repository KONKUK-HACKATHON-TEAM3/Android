package com.konkuk.hackathon_team3.presentation.minseo.alarm

import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.presentation.model.AlarmData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime

data class AlarmUiState(
    val alarmList: List<AlarmData> = listOf(
        AlarmData(
            message = "아빠님이 새로운 스토리를 공유했어요.",
            time = LocalTime.parse("08:30:20")
        ),
        AlarmData(
            message = "이번 주 순위가 바뀌었어요. 지금 바로 확인해보세요.",
            time = LocalTime.parse("14:30:20")
        )
    ),
)

class AlarmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AlarmUiState())
    val uiState: StateFlow<AlarmUiState> = _uiState.asStateFlow()
}