package com.konkuk.hackathon_team3.presentation.minseo.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.minseo.component.GasCalendar
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import java.time.LocalDate
import androidx.compose.runtime.getValue
import java.time.YearMonth

@Composable
fun GasCalendarRoute(
    navigateToFeed: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GasCalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GasCalendarScreen(
        navigateToFeed = navigateToFeed,
        markedDates = uiState.markedDates,
        onMonthChanged = viewModel::loadMarkedDates,
        selectedDate = uiState.selectedDate,
        onSelectedDateChange = viewModel::setSelectedDate,
        modifier = modifier
    )
}

@Composable
fun GasCalendarScreen(
    navigateToFeed: (LocalDate) -> Unit,
    markedDates: List<LocalDate>,
    onMonthChanged: (YearMonth) -> Unit,
    selectedDate: LocalDate,
    onSelectedDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        GasTopbar(backButtonClicked = {}, modifier = Modifier.padding(vertical = 10.dp))

        GasCalendar(
            markedDates = markedDates,
            selectedDate = selectedDate,
            onMonthChanged = onMonthChanged,
            onDateClicked = { date ->
                onSelectedDateChange(date)
                navigateToFeed(date)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGasCalendarScreen() {
    KONKUKHACKATHONTEAM3Theme {
        GasCalendarScreen(
            markedDates = emptyList(),
            onMonthChanged = {},
            navigateToFeed = {},
            selectedDate = LocalDate.now(),
            onSelectedDateChange = {}
        )
    }
}