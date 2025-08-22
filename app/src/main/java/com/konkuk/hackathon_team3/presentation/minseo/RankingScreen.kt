package com.konkuk.hackathon_team3.presentation.minseo

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

@Composable
fun RankingRoute(
    navigateToRecordWrite: () -> Unit,
    modifier: Modifier = Modifier
) {
    RankingScreen(
        navigateToRecordWrite = navigateToRecordWrite,
        modifier = modifier
    )
}

@Composable
fun RankingScreen(
    navigateToRecordWrite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = navigateToRecordWrite
    ) {
        Text("to Rankingk")
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRankingScreen() {
    KONKUKHACKATHONTEAM3Theme {
        RankingScreen(
            navigateToRecordWrite = {}
        )
    }
}