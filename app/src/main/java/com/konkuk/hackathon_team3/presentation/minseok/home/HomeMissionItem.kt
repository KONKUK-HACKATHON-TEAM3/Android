package com.konkuk.hackathon_team3.presentation.minseok.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.model.MissionData
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding

@Composable
fun HomeMissionItem(missionData: MissionData, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .width(156.dp)
            .border(width = 2.dp, color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .background(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = missionData.title)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${missionData.point}P",
                modifier = Modifier
                    .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
                    .roundedBackgroundWithPadding(
                        cornerRadius = 4.dp,
                        padding = PaddingValues(horizontal = 7.dp, vertical = 5.dp)
                    ),
                style = TextStyle(
                    fontSize = 9.sp,
                    lineHeight = 9.sp
                )
            )

        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = missionData.description, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = ImageVector.vectorResource(if (missionData.isCleared) R.drawable.ic_mission_stamp else R.drawable.ic_mission_no_stamp),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .align(alignment = Alignment.End)
                .size(50.dp)
                .rotate(-22f)
        )
    }
}

@Preview
@Composable
private fun HomeMissionItemPreview() {
    Column {
        HomeMissionItem(
            missionData = MissionData(
                title = "오늘의 사진",
                point = 300,
                description = "현재 모습을 찍어 올려보세요.",
                isCleared = true
            )
        )

        HomeMissionItem(
            missionData = MissionData(
                title = "오늘의 사진",
                point = 300,
                description = "현재 모습을 찍어 올려보세요.",
                isCleared = false
            )
        )
    }
}