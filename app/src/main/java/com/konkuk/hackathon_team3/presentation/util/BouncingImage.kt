package com.konkuk.hackathon_team3.presentation.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BouncingImage(
    resId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val infiniteTransition = rememberInfiniteTransition()

    val offsetY = infiniteTransition.animateValue(
        initialValue = 0.dp,
        targetValue = 10.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    ).value

    Image(
        painter = painterResource(id = resId),
        contentDescription = contentDescription,
        modifier = modifier
            .offset(y = offsetY),
        contentScale = ContentScale.Fit
    )
}