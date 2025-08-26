package com.konkuk.hackathon_team3.presentation.minseo.detailgas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import kotlinx.coroutines.launch

@Composable
fun DetailGasAnimationCard(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val manualRotation = remember { Animatable(0f) }
    var manualAtExit by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(visible) {
        if (visible) {
            manualRotation.snapTo(0f)
            manualAtExit = 0f
        }
    }

    val cameraDistance = with(LocalDensity.current) { 12.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600)) +
                    expandIn(expandFrom = Alignment.Center, animationSpec = tween(600)),
            exit = shrinkOut(shrinkTowards = Alignment.Center, animationSpec = tween(600)) +
                    fadeOut(tween(600))
        ) {
            val enterExitRotation by transition.animateFloat(
                transitionSpec = { tween(600, easing = FastOutSlowInEasing) },
                label = "enterExitRotation"
            ) { state ->
                if (state == EnterExitState.Visible) {
                    180f
                } else {
                    val raw = -manualAtExit
                    ((raw % 360f) + 360f) % 360f
                }
            }

            val totalRotation = (enterExitRotation + manualRotation.value).let {
                ((it % 360f) + 360f) % 360f
            }
            val showBack = totalRotation in 90f..270f

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 50.dp)
                    .graphicsLayer {
                        rotationY = totalRotation
                        this.cameraDistance = cameraDistance
                        scaleX = if (showBack) -1f else 1f
                    },
                contentAlignment = Alignment.Center
            ) {
                val onFlip: () -> Unit = {
                    scope.launch {
                        val target = if (manualRotation.value < 90f) 180f else 0f
                        manualRotation.animateTo(
                            target,
                            animationSpec = tween(500, easing = FastOutSlowInEasing)
                        )
                    }
                }
                val onClose: () -> Unit = {
                    manualAtExit = manualRotation.value
                    onDismiss()
                }

                if (showBack) {
                    CardBack(onCancel = onClose, onFlip = onFlip)
                } else {
                    CardFront(onCancel = onClose, onFlip = onFlip)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAnimation() {
    var expanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val manualRotation = remember { Animatable(0f) }
    var manualAtExit by remember { mutableFloatStateOf(0f) }

    val cameraDistance = with(LocalDensity.current) { 12.dp.toPx() }

    KONKUKHACKATHONTEAM3Theme {
        Box(Modifier.fillMaxSize()) {
            Button(
                onClick = {
                    scope.launch {
                        manualRotation.snapTo(0f)
                        manualAtExit = 0f
                        expanded = true
                    }
                }
            ) { Text("생존 신고 내역") }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(600)) +
                            expandIn(expandFrom = Alignment.Center, animationSpec = tween(600)),
                    exit = shrinkOut(shrinkTowards = Alignment.Center, animationSpec = tween(600)) +
                            fadeOut(tween(600))
                ) {
                    val enterExitRotation by transition.animateFloat(
                        transitionSpec = { tween(600, easing = FastOutSlowInEasing) },
                        label = "enterExitRotation"
                    ) { state ->
                        if (state == EnterExitState.Visible) {
                            180f
                        } else {
                            val raw = -manualAtExit
                            ((raw % 360f) + 360f) % 360f
                        }
                    }

                    val totalRotation = (enterExitRotation + manualRotation.value).let {
                        ((it % 360f) + 360f) % 360f
                    }
                    val showBack = totalRotation in 90f..270f

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationY = totalRotation
                                this.cameraDistance = cameraDistance
                                scaleX = if (showBack) -1f else 1f
                            }
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        val onFlip: () -> Unit = {
                            scope.launch {
                                val target = if (manualRotation.value < 90f) 180f else 0f
                                manualRotation.animateTo(
                                    target,
                                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                                )
                            }
                        }

                        val onClose: () -> Unit = {
                            scope.launch {
                                manualAtExit = manualRotation.value
                                expanded = false
                            }
                        }

                        if (showBack) {
                            CardBack(onCancel = onClose, onFlip = onFlip)
                        } else {
                            CardFront(onCancel = onClose, onFlip = onFlip)
                        }
                    }
                }
            }
        }
    }
}
