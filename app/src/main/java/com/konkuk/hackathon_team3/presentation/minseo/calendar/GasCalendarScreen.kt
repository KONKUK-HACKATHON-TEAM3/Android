package com.konkuk.hackathon_team3.presentation.minseo.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.minseo.component.GasCalendar
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import java.time.LocalDate

@Composable
fun GasCalendarRoute(
    popBackStack:()->Unit,
    navigateToFeed: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    GasCalendarScreen(
        popBackStack=popBackStack,
        navigateToFeed = navigateToFeed,
        modifier = modifier
    )
}

@Composable
fun GasCalendarScreen(
    popBackStack:()->Unit,
    navigateToFeed: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        GasTopbar(backButtonClicked = popBackStack, modifier = Modifier.padding(vertical = 10.dp))

        GasCalendar(
            onDateClicked = { date ->
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
            navigateToFeed = {},
            popBackStack={}
        )
    }
}