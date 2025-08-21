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
    navigateToMinseo: () -> Unit,
    navigateToRecordWrite: () -> Unit,
    modifier: Modifier = Modifier
) {
    MainScreen(
        navigateToMinseo = navigateToMinseo,
        navigateToRecordWrite = navigateToRecordWrite,
        modifier = modifier
    )
}

@Composable
fun MainScreen(
    navigateToMinseo: () -> Unit,
    navigateToRecordWrite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = navigateToMinseo,
            modifier = modifier
        ) {
            Text("to Minseo")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = navigateToRecordWrite,
            modifier = modifier
        ) {
            Text("to Minseok")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMainScreen() {
    KONKUKHACKATHONTEAM3Theme {
        MainScreen(
            navigateToMinseo = {},
            navigateToRecordWrite = {}
        )
    }
}