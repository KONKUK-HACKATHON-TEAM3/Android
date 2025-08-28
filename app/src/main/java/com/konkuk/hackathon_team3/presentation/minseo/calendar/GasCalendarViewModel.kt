package com.konkuk.hackathon_team3.presentation.minseo.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.hackathon_team3.data.mapper.toLocalDates
import com.konkuk.hackathon_team3.data.service.ServicePool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val markedDates: List<LocalDate> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now()
)

class GasCalendarViewModel : ViewModel() {
    private val feedService by lazy { ServicePool.feedService }

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun setSelectedDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun loadMarkedDates(yearMonth: YearMonth) {
        viewModelScope.launch {
            try {
                val ym = "%04d-%02d".format(yearMonth.year, yearMonth.monthValue)
                val dates = feedService.getDates(ym).toLocalDates()
                _uiState.value = _uiState.value.copy(markedDates = dates)
            } catch (e: Exception) {
                Log.e("GasCalendarVM", "getDates failed: ${e.message}", e)
                _uiState.value = _uiState.value.copy(markedDates = emptyList())
            }
        }
    }
}