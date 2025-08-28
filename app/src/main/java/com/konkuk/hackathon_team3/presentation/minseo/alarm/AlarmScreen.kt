package com.konkuk.hackathon_team3.presentation.minseo.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle
import com.konkuk.hackathon_team3.ui.theme.regularStyle
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AlarmRoute(
    navigateToFeed: (LocalDate) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlarmScreen(
        navigateToFeed = navigateToFeed,
        popBackStack = popBackStack,
        modifier = modifier,
        uiState = uiState
    )
}

@Composable
fun AlarmScreen(
    navigateToFeed: (LocalDate) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: AlarmUiState = AlarmUiState()
) {
    var now by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            now = LocalTime.now()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        GasTopbar(
            backButtonClicked = popBackStack,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = uiState.alarmList,
                key = { it.message + it.time.toString() }
            ) { alarm ->
                Row(
                    modifier = Modifier
                        .noRippleClickable { navigateToFeed(LocalDate.now()) }
                        .gasComponentDesign()
                        .fillMaxWidth()
                        .padding(21.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = alarm.message,
                        fontSize = 10.sp,
                        style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
                    )

                    Text(
                        text = alarm.time.toRelative(now),
                        fontSize = 10.sp,
                        style = KONKUKHACKATHONTEAM3Theme.typography.regularStyle
                    )
                }
            }
        }

    }
}

private fun LocalTime.toRelative(now: LocalTime = LocalTime.now()): String {
    var minutes = Duration.between(this, now).toMinutes()
    if (minutes < 0) minutes += 24 * 60
    return if (minutes < 60) "${minutes}분 전" else "${minutes / 60}시간 전"
}

@Preview(showBackground = true)
@Composable
private fun PreviewAlarmScreen() {
    KONKUKHACKATHONTEAM3Theme {
        AlarmScreen(
            navigateToFeed = {},
            popBackStack = {}
        )
    }
}