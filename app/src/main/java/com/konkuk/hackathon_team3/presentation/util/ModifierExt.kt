package com.konkuk.hackathon_team3.presentation.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit = {}
): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

fun Modifier.roundedBackgroundWithPadding(
    backgroundColor: Color = Color.Unspecified,
    cornerRadius: Dp = 0.dp,
    padding: PaddingValues = PaddingValues(0.dp)
): Modifier {
    return this
        .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
        .padding(padding)
}

fun Modifier.addFocusCleaner(focusManager: FocusManager, doOnClear: () -> Unit = {}): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            doOnClear()
            focusManager.clearFocus()
        })
    }
}

inline fun Modifier.pressedEffectClickable(
    crossinline onClick: () -> Unit = {}
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    this
        .graphicsLayer {
            alpha = if (isPressed) 0.6f else 1f
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            onClick()
        }
}

fun Modifier.advancedImePadding() =
    composed {
        var consumePadding by remember { mutableIntStateOf(0) }
        onGloballyPositioned { coordinates ->
            consumePadding = coordinates.findRootCoordinates().size.height -
                    (coordinates.positionInWindow().y + coordinates.size.height).toInt()
        }
            .consumeWindowInsets(
                PaddingValues(bottom = with(LocalDensity.current) { consumePadding.toDp() })
            )
            .imePadding()
    }

fun Modifier.gasComponentDesign(): Modifier {
    return this
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
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
}
