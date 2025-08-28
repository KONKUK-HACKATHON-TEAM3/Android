package com.konkuk.hackathon_team3.presentation.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

object AppGradients {
    val Orange = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.00f to Color(0xFFFF9952),
            0.49f to Color(0xFFFFBC70),
            1.00f to Color(0xFFFF9952)
        )
    )
}

@Stable
fun Modifier.gradientBorder(
    width: Dp,
    brush: Brush,
    shape: Shape
): Modifier = this.border(BorderStroke(width, brush), shape)

@Stable
fun Modifier.roundedBackgroundWithPadding(
    backgroundBrush: Brush,
    cornerRadius: Dp,
    padding: PaddingValues
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(backgroundBrush, RoundedCornerShape(cornerRadius))
    .padding(padding)
