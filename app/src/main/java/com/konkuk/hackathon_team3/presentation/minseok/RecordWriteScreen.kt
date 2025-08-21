package com.konkuk.hackathon_team3.presentation.minseok

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

@Composable
fun RecordWriteRoute(
    navigateToMinseo: () -> Unit,
    modifier: Modifier = Modifier
) {
    RecordWriteScreen(
        navigateToMinseo = navigateToMinseo,
        recordButtonClicked = {},
        modifier = modifier
    )
}

@Composable
fun RecordWriteScreen(
    navigateToMinseo: () -> Unit,
    recordButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recordButtonText by remember { mutableStateOf("녹음버튼") }
    val recordButtonEnabled by remember { mutableStateOf(true) }

    Column(modifier = modifier) {
        Button(
            onClick = navigateToMinseo,
        ) {
            Text("to Minseo")
        }
        Text(text = "녹음버튼")
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMinseokScreen() {
    KONKUKHACKATHONTEAM3Theme {
        RecordWriteScreen(
            navigateToMinseo = {},
            recordButtonClicked = {},
        )
    }
}