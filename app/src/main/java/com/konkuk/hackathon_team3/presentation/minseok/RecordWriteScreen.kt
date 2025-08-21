package com.konkuk.hackathon_team3.presentation.minseok

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

@Composable
fun RecordWriteRoute(
    navigateToMinseo: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecordWriteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updatePermission(isGranted)
    }

    // 권한 확인
    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.updatePermission(true)
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    RecordWriteScreen(
        uiState = uiState,
        navigateToMinseo = navigateToMinseo,
        recordButtonClicked = { viewModel.recordButtonClicked(context = context) },
        playButtonClicked = { viewModel.playRecording() },
        modifier = modifier
    )
}

@Composable
fun RecordWriteScreen(
    uiState: RecordWriteUiState,
    navigateToMinseo: () -> Unit,
    recordButtonClicked: () -> Unit,
    playButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = navigateToMinseo,
        ) {
            Text("to Minseo")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (uiState.isRecording) "녹음 중..." else "녹음하기",
            modifier = Modifier.noRippleClickable(onClick = recordButtonClicked)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (uiState.audioRecord == null) "음성파일 비어있음" else "재생하기",
            modifier = Modifier.noRippleClickable{
                if (uiState.audioRecord != null){
                    playButtonClicked()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMinseokScreen() {
    KONKUKHACKATHONTEAM3Theme {
        RecordWriteScreen(
            navigateToMinseo = {},
            recordButtonClicked = {},
            uiState = RecordWriteUiState(),
            playButtonClicked = {},
        )
    }
}