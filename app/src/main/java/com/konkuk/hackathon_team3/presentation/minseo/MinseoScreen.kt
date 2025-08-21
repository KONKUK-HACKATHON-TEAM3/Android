package com.konkuk.hackathon_team3.presentation.minseo

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

@Composable
fun MinseoRoute(
    navigateToMinseok: () -> Unit,
    modifier: Modifier = Modifier
) {
    MinseoScreen(
        navigateToMinseok = navigateToMinseok,
        modifier = modifier
    )
}

@Composable
fun MinseoScreen(
    navigateToMinseok: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = navigateToMinseok
    ) {
        Text("to Minseok")
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMinseoScreen() {
    KONKUKHACKATHONTEAM3Theme {
        MinseoScreen(
            navigateToMinseok = {}
        )
    }
}