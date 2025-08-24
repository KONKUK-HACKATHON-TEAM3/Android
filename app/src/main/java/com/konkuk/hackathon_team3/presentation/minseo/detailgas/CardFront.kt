package com.konkuk.hackathon_team3.presentation.minseo.detailgas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding

@Composable
fun CardFront(
    onCancel: () -> Unit,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .roundedBackgroundWithPadding(
                backgroundColor = Color.Green,
                cornerRadius = 10.dp,
                padding = PaddingValues(20.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("X", modifier = Modifier.noRippleClickable(onClick = onCancel))
            Text("뒤집기", modifier = Modifier.noRippleClickable(onClick = onFlip))
        }
    }
}