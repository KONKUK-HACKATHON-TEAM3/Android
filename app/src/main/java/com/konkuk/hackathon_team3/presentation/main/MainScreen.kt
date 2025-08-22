package com.konkuk.hackathon_team3.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

@Composable
fun MainRoute(
    navigateToRanking: () -> Unit,
    navigateToGasWriting: () -> Unit,
    modifier: Modifier = Modifier
) {
    MainScreen(
        navigateToRanking = navigateToRanking,
        navigateToRecordWrite = navigateToGasWriting,
        modifier = modifier
    )
}

@Composable
fun MainScreen(
    navigateToRanking: () -> Unit,
    navigateToRecordWrite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = navigateToRanking,
            modifier = modifier
        ) {
            Text("to Ranking")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = navigateToRecordWrite,
            modifier = modifier
        ) {
            Text("to Rankingk")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMainScreen() {
    KONKUKHACKATHONTEAM3Theme {
        MainScreen(
            navigateToRanking = {},
            navigateToRecordWrite = {}
        )
    }
}