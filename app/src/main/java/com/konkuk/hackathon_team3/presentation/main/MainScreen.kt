package com.konkuk.hackathon_team3.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.konkuk.hackathon_team3.presentation.minseo.detailgas.DetailGasAnimationCard
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
    var showDetailGas by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier,
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
                Text("to RecordWrite")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { showDetailGas = true }
            ) {
                Text( text = "생존 신고 내역")
            }
        }

        DetailGasAnimationCard(
            visible = showDetailGas,
            onDismiss = { showDetailGas = false }
        )
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