package com.konkuk.hackathon_team3.presentation.minseok

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import java.io.File
import java.io.FileOutputStream


@Composable
fun GasWritingRoute(
    navigateToRanking: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GasWritingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        Log.d("Camera", "사진 촬영 결과: $success")
        if (success) {
            val uri = uiState.imageUri
            if (uri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    // 새 파일로 저장
                    val savedFile = saveBitmapToFile(context, bitmap)
                    val newUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        savedFile
                    )

                    Log.d("Camera", "새 Uri 생성 완료: $newUri")
                    viewModel.setImageUri(newUri)

                } catch (e: Exception) {
                    Log.e("Camera", "사진 불러오기 실패", e)
                }
            }
        } else {
            Log.d("Camera", "사진 촬영 실패")
            viewModel.setImageUri(null)
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateAudioPermission(isGranted)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateCameraPermission(isGranted)
        if (isGranted) {
            val uri = createImageUri(context)
            viewModel.setImageUri(uri)
            cameraLauncher.launch(uri)
        }
    }

    LaunchedEffect(Unit) {
        val hasAudioPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val hasCameraPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.updateAudioPermission(hasAudioPermission)
        viewModel.updateCameraPermission(hasCameraPermission)
    }

    GasWritingScreen(
        uiState = uiState,
        navigateToRanking = navigateToRanking,
        onImageClick = {
            if (uiState.hasCameraPermission) {
                val uri = createImageUri(context)
                viewModel.setImageUri(uri)
                cameraLauncher.launch(uri)
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onRecordingToggle = {
            if (uiState.hasAudioPermission) {
                viewModel.toggleRecording(context)
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        onTextChange = viewModel::updateTextContent,
        onClearText = viewModel::clearText,
        onClearError = viewModel::clearError,
        modifier = modifier
    )
}

fun createImageUri(context: Context): Uri {
    val imageFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "temp_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )
}
private fun saveBitmapToFile(context: Context, bitmap: Bitmap): File {
    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "photo_${System.currentTimeMillis()}.jpg"
    )
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file
}

@Composable
fun GasWritingScreen(
    uiState: GasWritingUiState,
    navigateToRanking: () -> Unit,
    onImageClick: () -> Unit,
    onRecordingToggle: () -> Unit,
    onTextChange: (String) -> Unit,
    onClearText: () -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 상단 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "새 게시물",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    navigateToRanking()
                }
            ) {
                Text("완료")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clickable { onImageClick() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(uiState.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "촬영된 이미지",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    FloatingActionButton(
                        onClick = onImageClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "재촬영", tint = Color.White)
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "탭해서 사진 촬영",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "카메라로 사진을 찍어보세요",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 🎯 텍스트 입력 영역 (실시간 STT + TextField)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "내용 작성",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 실시간 STT 상태
                        if (uiState.isProcessingSTT) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                "변환중",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (uiState.textContent.isNotEmpty()) {
                            TextButton(onClick = onClearText) {
                                Text("지우기")
                            }
                        }
                    }
                }

                // TextField (수정 가능한 텍스트)
                OutlinedTextField(
                    value = uiState.textContent,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = {
                        Text(
                            if (uiState.isRecording) "🎤 말씀해주세요..." else "텍스트를 입력하거나 음성 버튼을 눌러 말해보세요",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    minLines = 8,
                    maxLines = 12
                )
            }
        }

        // 🎯 하단 컨트롤
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 실시간 음성 인식 버튼
            FloatingActionButton(
                onClick = onRecordingToggle,
                containerColor = if (uiState.isRecording) Color.Red else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(if (uiState.isRecording) R.drawable.ic_puase else R.drawable.ic_record),
                    contentDescription = if (uiState.isRecording) "녹음 중지" else "음성 인식 시작",
                    tint = Color.White
                )
            }

            // 상태 텍스트
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        !uiState.hasAudioPermission -> "마이크 권한 필요"
                        uiState.isRecording -> "🔴 실시간 음성 인식 중..."
                        else -> "음성 버튼을 눌러 말해보세요"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (uiState.isRecording) FontWeight.Bold else FontWeight.Normal,
                    color = if (uiState.isRecording) Color.Red else MaterialTheme.colorScheme.onSurface
                )

                if (uiState.isRecording && uiState.textContent.isNotEmpty()) {
                    Text(
                        "${uiState.textContent.length}자",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 에러 메시지
        uiState.sttError?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(onClick = onClearError) {
                        Text("닫기")
                    }
                }
            }
        }
    }
}

// 임시 이미지 URI 생성 (메모리에서만 사용)
//private fun createImageUri(context: Context): Uri {
//    // 앱 전용 임시 디렉토리 사용 (앱 삭제시 자동 정리됨)
//    val tempFile = File(context.cacheDir, "temp_camera_${System.currentTimeMillis()}.jpg")
//    Log.d("Camera", "임시 이미지 파일: ${tempFile.absolutePath}")
//
//    return androidx.core.content.FileProvider.getUriForFile(
//        context,
//        "${context.packageName}.provider",
//        tempFile
//    )
//}


@Preview(showBackground = true)
@Composable
private fun PreviewGasWritingScreen() {
    KONKUKHACKATHONTEAM3Theme {
        GasWritingScreen(
            uiState = GasWritingUiState(
                imageUri = null,
                textContent = "안녕하세요, 실시간 음성인식으로 작성된 텍스트입니다. 이 텍스트는 수정할 수 있어요!",
                isRecording = true,
                hasAudioPermission = true,
                hasCameraPermission = true,
                isProcessingSTT = false
            ),
            navigateToRanking = {},
            onImageClick = {},
            onRecordingToggle = {},
            onTextChange = {},
            onClearText = {},
            onClearError = {}
        )
    }
}