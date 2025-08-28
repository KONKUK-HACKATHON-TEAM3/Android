package com.konkuk.hackathon_team3.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable

@Composable
fun GasTopbar(
    modifier: Modifier = Modifier,
    backButtonClicked: () -> Unit = {},
    isHomeScreen: Boolean = false,
    navigateToNotification: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (!isHomeScreen) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_back_arrow),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterStart)
                    .noRippleClickable(backButtonClicked),
            )
        }
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_app_logo),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.align(Alignment.Center)
        )
        if (isHomeScreen) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_bell),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterEnd)
                    .noRippleClickable(navigateToNotification),
            )
        }
    }
}

@Preview
@Composable
private fun GasTopbarPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        GasTopbar(
            backButtonClicked = {},
            isHomeScreen = true
        )
    }
}