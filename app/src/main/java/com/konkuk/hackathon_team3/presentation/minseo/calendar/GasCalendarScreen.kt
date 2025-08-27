package com.konkuk.hackathon_team3.presentation.minseo.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.hackathon_team3.presentation.minseo.component.GasCalendar
import com.konkuk.hackathon_team3.presentation.minseo.detailgas.DetailGasAnimationCard
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import java.time.LocalDate

@Composable
fun GasCalendarRoute(
    modifier: Modifier = Modifier
) {
    GasCalendarScreen(
        modifier = modifier
    )
}

@Composable
fun GasCalendarScreen(
    modifier: Modifier = Modifier
) {
    var showDetail by remember { mutableStateOf(false) }
    var pickedDate by remember { mutableStateOf<LocalDate?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        GasCalendar(
            onDateClicked = { date ->
                pickedDate = date
                showDetail = true
            }
        )

        DetailGasAnimationCard(
            visible = showDetail,
            onDismiss = { showDetail = false }
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGasCalendarScreen() {
    KONKUKHACKATHONTEAM3Theme {
        GasCalendarScreen()
    }
}