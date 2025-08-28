package com.konkuk.hackathon_team3.presentation.minseo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import com.konkuk.hackathon_team3.ui.theme.boldStyle
import com.konkuk.hackathon_team3.ui.theme.regularStyle
import java.time.LocalDate
import java.time.YearMonth

enum class DayOfWeekType(val description: String) {
    SUN("일"), MON("월"), TUE("화"), WED("수"), THU("목"), FRI("금"), SAT("토");

    companion object {
        val sundayFirst = listOf(SUN, MON, TUE, WED, THU, FRI, SAT)
    }
}

private object CalendarLogic {
    fun startOfWeekSunday(date: LocalDate): LocalDate {
        val offset = date.dayOfWeek.value % 7
        return date.minusDays(offset.toLong())
    }

    fun monthGridDates(visibleMonth: YearMonth): List<LocalDate> {
        val first = visibleMonth.atDay(1)
        val startDow = first.dayOfWeek.value % 7
        val daysInMonth = visibleMonth.lengthOfMonth()
        val totalCells = ((startDow + daysInMonth + 6) / 7) * 7
        val gridStart = first.minusDays(startDow.toLong())
        return (0 until totalCells).map { gridStart.plusDays(it.toLong()) }
    }

    fun weekDatesFromSunday(startSunday: LocalDate): List<LocalDate> =
        (0..6).map { startSunday.plusDays(it.toLong()) }
}

@Composable
fun GasCalendar(
    modifier: Modifier = Modifier,
    onDateClicked: (LocalDate) -> Unit = {}
) {
    val today = LocalDate.now()
    var selected by remember { mutableStateOf(today) }
    var visibleMonth by remember { mutableStateOf(YearMonth.from(today)) }

    val monthLabel = "${visibleMonth.year}년 ${visibleMonth.monthValue}월"
    val monthDates = remember(visibleMonth) { CalendarLogic.monthGridDates(visibleMonth) }

    Column(
        modifier = modifier
            .gasComponentDesign()
            .padding(vertical = 24.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_left_arrow_16),
                contentDescription = "이전 달",
                tint = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .noRippleClickable { visibleMonth = visibleMonth.minusMonths(1) }
            )
            Text(
                text = monthLabel,
                fontSize = 14.sp,
                style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_right_arrow_16),
                contentDescription = "다음 달",
                tint = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .noRippleClickable { visibleMonth = visibleMonth.plusMonths(1) }
            )
        }
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DayOfWeekType.sundayFirst.forEach {
                Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = it.description,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))

        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            monthDates.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { date ->
                        val inMonth = YearMonth.from(date) == visibleMonth
                        val isSelected = date == selected
                        val isToday = date == today

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFFFF8514) else Color.Transparent)
                                .noRippleClickable {
                                    selected = date
                                    onDateClicked(date)
                                }
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = when {
                                    isSelected -> Color.White
                                    !inMonth -> Color(0xFF777777)
                                    isToday -> Color(0xFFFF6E00)
                                    else -> Color.Black
                                },
                                fontSize = 13.sp,
                                style = if (isSelected) KONKUKHACKATHONTEAM3Theme.typography.boldStyle else KONKUKHACKATHONTEAM3Theme.typography.regularStyle
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun GasHomeCalendar(
    modifier: Modifier = Modifier,
    navigateToCalendar: () -> Unit
) {
    val today = LocalDate.now()
    var selected by remember { mutableStateOf(today) }

    val weekStart = remember(today) { CalendarLogic.startOfWeekSunday(today) }
    val weekDates = remember(weekStart) { CalendarLogic.weekDatesFromSunday(weekStart) }
    val monthLabel = "${today.year}년 ${today.monthValue}월"

    Column(
        modifier = modifier
            .gasComponentDesign()
            .padding(top = 20.dp, bottom = 14.dp)
            .noRippleClickable(navigateToCalendar),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "가족 캘린더",
            fontSize = 14.sp,
            style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle,
            color = Color.Black
        )
        Spacer(Modifier.height(4.dp))

        Text(
            text = monthLabel,
            fontSize = 9.sp,
            style = KONKUKHACKATHONTEAM3Theme.typography.regularStyle,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DayOfWeekType.sundayFirst.forEach {
                Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = it.description,
                        color = Color.Black,
                        fontSize = 10.sp,
                        style = KONKUKHACKATHONTEAM3Theme.typography.boldStyle,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDates.forEach { date ->
                val isSelected = date == selected
                val isToday = date == today

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .noRippleClickable { selected = date }
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = when {
                            isSelected -> Color.Black
                            isToday -> Color.Black
                            else -> Color.Black
                        },
                        fontSize = 13.sp,
                        style = if (isSelected) KONKUKHACKATHONTEAM3Theme.typography.boldStyle else KONKUKHACKATHONTEAM3Theme.typography.regularStyle
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGasCalendar() {
    KONKUKHACKATHONTEAM3Theme {
        GasCalendar()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGasHomeCalendar() {
    KONKUKHACKATHONTEAM3Theme {
        GasHomeCalendar(
            navigateToCalendar = {}
        )
    }
}