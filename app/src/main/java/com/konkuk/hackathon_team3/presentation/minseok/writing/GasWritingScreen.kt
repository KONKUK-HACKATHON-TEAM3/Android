package com.konkuk.hackathon_team3.presentation.minseok.writing

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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.pressedEffectClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sin


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
        onClearError = viewModel::clearError,
        onUploadButtonClicked= navigateToRanking,
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
    onImageClick: () -> Unit,
    onRecordingToggle: () -> Unit,
    onTextChange: (String) -> Unit,
    onClearError: () -> Unit,
    onUploadButtonClicked:()->Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester: FocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
    ) {
        GasTopbar(
            backButtonClicked = {}
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(width = 1.dp, color = Color(0xFF997C70), shape = RoundedCornerShape(16.dp))
                .roundedBackgroundWithPadding(backgroundColor = Color(0xFFCCCCCC), cornerRadius = 16.dp)
                .clickable { onImageClick() }
                .aspectRatio(1f), contentAlignment = Alignment.Center
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
                        .clip(RoundedCornerShape(16.dp))
                        .rotate(90f),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF8D8D8D)
                    )
                    Text(
                        "탭해서 사진 촬영",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF8D8D8D)
                    )
                    Text(
                        "카메라로 사진을 찍어보세요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8D8D8D)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(width = 1.dp, color = Color(0xFF997C90), shape = RoundedCornerShape(16.dp))
                .padding(vertical = 20.dp, horizontal = 21.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .noRippleClickable(onRecordingToggle)
                    .height(24.dp)
            ) {
                if (uiState.isRecording) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RecordingAnimation()
                    }
                } else {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_mic),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(19.dp))
            Row {
                BasicTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    value = uiState.textContent,
                    onValueChange = {
                        if (it.codePointCount(0, it.length) <= 150) {
                            onTextChange(it)
                        }
                    },
                    cursorBrush = SolidColor(Color(0xFF997C70)),
                    textStyle = MaterialTheme.typography.labelMedium.copy(color = Color.Black),
                    decorationBox = { innerTextField ->
                        innerTextField()
                        if (uiState.textContent.isEmpty()) {
                            Text(
                                text = "내용을 입력해주세요...",
                                color = Color(0xFFCCCCCC),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.End),
            text = "${uiState.textContent.length}자",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "저장하기",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(width = 1.dp, color = Color(0xFF997C90), shape = RoundedCornerShape(16.dp))
                .roundedBackgroundWithPadding(cornerRadius = 16.dp, padding = PaddingValues(16.dp))
                .pressedEffectClickable(onUploadButtonClicked),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = Modifier.height(16.dp))


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


@Composable
fun RecordingAnimation() {
    val transition = rememberInfiniteTransition()

    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            val phaseOffset = index * (Math.PI.toFloat() / 2)
            val currentPhase = wavePhase + phaseOffset

            val normalizedValue = (sin(currentPhase.toDouble()) + 1) / 2

            val minHeight = if (index == 0 || index == 3) 8.dp else 12.dp
            val maxHeight = if (index == 0 || index == 3) 16.dp else 28.dp

            val currentHeight = minHeight + (maxHeight - minHeight) * normalizedValue.toFloat()

            Box(
                modifier = Modifier.height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                RecordingBar(height = currentHeight)
            }
        }
    }
}

@Composable
fun RecordingBar(height: Dp) {
    Spacer(
        modifier = Modifier
            .size(width = 3.dp, height = height)
            .roundedBackgroundWithPadding(
                backgroundColor = Color(0xFF997C90),
                cornerRadius = 1.5.dp
            )
    )
}


@Preview(showBackground = true)
@Composable
private fun PreviewGasWritingScreen() {
    KONKUKHACKATHONTEAM3Theme {
        GasWritingScreen(
            uiState = GasWritingUiState(
                imageUri = null,
                textContent = "내용을 입력해주세요",
                isRecording = true,
                hasAudioPermission = true,
                hasCameraPermission = true,
                isProcessingSTT = false
            ),
            onImageClick = {},
            onRecordingToggle = {},
            onTextChange = {},
            onClearError = {},
            onUploadButtonClicked = {}
        )
    }
}