package com.konkuk.hackathon_team3.presentation.minseok.writing

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.main.GasTopbar
import com.konkuk.hackathon_team3.presentation.util.gasComponentDesign
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.presentation.util.pressedEffectClickable
import com.konkuk.hackathon_team3.presentation.util.roundedBackgroundWithPadding
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import java.io.File
import java.io.FileOutputStream


@Composable
fun GasWritingRoute(
    popBackStack:()->Unit,
    navigateToHome: () -> Unit,
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

                    val savedFile = saveBitmapToFile(context, bitmap, uri = uri)
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
        popBackStack=popBackStack,
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
        onUploadButtonClicked = {
            viewModel.uploadFeed(context = context) {
                navigateToHome()
            }
        },
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


private fun saveBitmapToFile(context: Context, bitmap: Bitmap, uri: Uri): File {
    val rotatedBitmap = rotateBitmapIfRequired(context, bitmap, uri)

    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "photo_${System.currentTimeMillis()}.jpg"
    )

    FileOutputStream(file).use { out ->
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }

    return file
}

private fun rotateBitmapIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val exif = inputStream?.use { ExifInterface(it) }

        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotate(270f)
            else -> bitmap
        }
    } catch (e: Exception) {
        Log.e("ImageRotation", "회전 보정 실패", e)
        bitmap
    } as Bitmap
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

@Composable
fun GasWritingScreen(
    popBackStack:()->Unit,
    uiState: GasWritingUiState,
    onImageClick: () -> Unit,
    onRecordingToggle: () -> Unit,
    onTextChange: (String) -> Unit,
    onClearError: () -> Unit,
    onUploadButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester: FocusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            GasTopbar(
                backButtonClicked = popBackStack
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(width = 2.dp, color = Color.White.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
                    .roundedBackgroundWithPadding(
                        backgroundColor = Color(0xFFCCCCCC).copy(alpha = 0.2f),
                        cornerRadius = 16.dp
                    )
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
                            .clip(RoundedCornerShape(16.dp)),
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
                    .gasComponentDesign()
                    .padding(vertical = 20.dp, horizontal = 21.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .noRippleClickable(onRecordingToggle),
                    contentAlignment = Alignment.Center
                ) {

                    if (uiState.isRecording) {
                        RecordingAnimation()
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_mic),
                            contentDescription = null,
                            tint = Color.Unspecified,
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
                    .gasComponentDesign()
                    .padding(16.dp)
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
        if (uiState.isLoading){
            UploadLoadingAnimation()
        }
    }
}


@Composable
fun RecordingAnimation() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("Recording.json")
    )
    val lottieAnimatable = rememberLottieAnimatable()

    LaunchedEffect(composition) {
        lottieAnimatable.animate(
            composition = composition,
            clipSpec = LottieClipSpec.Frame(0, 1200),
            initialProgress = 0f,
            iteration = LottieConstants.IterateForever
        )
    }

    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun UploadLoadingAnimation() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("Uploading.json")
    )
    val lottieAnimatable = rememberLottieAnimatable()

    LaunchedEffect(composition) {
        lottieAnimatable.animate(
            composition = composition,
            clipSpec = LottieClipSpec.Frame(0, 1200),
            initialProgress = 0f,
            iteration = LottieConstants.IterateForever
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().background(color = Color.White.copy(0.3f)),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center),
            contentScale = ContentScale.FillWidth
        )
    }
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
            onUploadButtonClicked = {},
            popBackStack={}
        )
    }
}