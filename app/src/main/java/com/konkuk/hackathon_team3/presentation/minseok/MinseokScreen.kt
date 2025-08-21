package com.konkuk.hackathon_team3.presentation.minseok

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

@Composable
fun MinseokRoute(
    navigateToMinseo: () -> Unit,
    modifier: Modifier = Modifier
) {
    MinseokScreen(
        navigateToMinseo = navigateToMinseo,
        modifier = modifier
    )
}

@Composable
fun MinseokScreen(
    navigateToMinseo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = navigateToMinseo,
        modifier = modifier
    ) {
        Text("to Minseo")
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMinseokScreen() {
    KONKUKHACKATHONTEAM3Theme {
        MinseokScreen(
            navigateToMinseo = {}
        )
    }
}