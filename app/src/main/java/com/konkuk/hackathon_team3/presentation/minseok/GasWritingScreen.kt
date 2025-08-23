package com.konkuk.hackathon_team3.presentation.minseok

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.konkuk.hackathon_team3.presentation.util.noRippleClickable
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme

@Composable
fun GasWritingRoute(
    navigateToRanking: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GasWritingViewModel = viewModel()
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

    GasWritingScreen(
        uiState = uiState,
        navigateToRanking = navigateToRanking,
        recordButtonClicked = { viewModel.recordButtonClicked(context = context) },
        playButtonClicked = { viewModel.playRecording() },
        // 새로 추가된 함수들
        updateClientId = viewModel::updateClientId,
        updateClientSecret = viewModel::updateClientSecret,
        updateLanguage = viewModel::updateLanguage,
        clearText = viewModel::clearText,
        clearErrors = viewModel::clearErrors,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasWritingScreen(
    uiState: GasWritingUiState,
    navigateToRanking: () -> Unit,
    recordButtonClicked: () -> Unit,
    playButtonClicked: () -> Unit,
    // 새로 추가된 파라미터들
    updateClientId: (String) -> Unit,
    updateClientSecret: (String) -> Unit,
    updateLanguage: (String) -> Unit,
    clearText: () -> Unit,
    clearErrors: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // 🎯 상단: 기존 랭킹 버튼
        Button(
            onClick = navigateToRanking,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("to Ranking")
        }

        // 🎯 권한 상태 알림
        if (!uiState.hasAudioPermission) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚠️ 마이크 권한이 필요합니다",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 🎯 API 키 설정 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "🔧 실시간 STT 설정 (선택사항)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = uiState.clientId,
                    onValueChange = updateClientId,
                    label = { Text("Client ID") },
                    placeholder = { Text("네이버 클라우드 Client ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.clientSecret,
                    onValueChange = updateClientSecret,
                    label = { Text("Client Secret") },
                    placeholder = { Text("네이버 클라우드 Client Secret") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 언어 선택 드롭다운
                var expanded by remember { mutableStateOf(false) }
                val languages = mapOf(
                    "Kor" to "🇰🇷 한국어",
                    "Eng" to "🇺🇸 영어",
                    "Jpn" to "🇯🇵 일본어",
                    "Chn" to "🇨🇳 중국어"
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = languages[uiState.selectedLanguage] ?: "🇰🇷 한국어",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("언어") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        languages.forEach { (code, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    updateLanguage(code)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    "💡 API 키를 입력하면 녹음 중 실시간 텍스트 변환이 가능합니다",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        // 🎯 녹음 상태 표시 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isRecording)
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                else
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 녹음 버튼 (기존 스타일 유지하되 강조)
                Text(
                    text = if (uiState.isRecording) "🔴 녹음 중..." else "🎤 녹음하기",
                    modifier = Modifier.noRippleClickable(onClick = recordButtonClicked),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isRecording) Color.Red else MaterialTheme.colorScheme.primary
                )

                // 실시간 STT 상태
                if (uiState.isRecording) {
                    if (uiState.clientId.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "🟢 실시간 STT 작동 중",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Medium
                            )

                            if (uiState.chunkCount > 0) {
                                Text(
                                    "| 청크 #${uiState.chunkCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (uiState.isProcessingSTT) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        Text(
                            "📁 파일 녹음만 진행 중 (실시간 STT 비활성)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }

        // 🎯 재생 버튼 (기존 스타일 유지)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.audioRecord != null)
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = if (uiState.audioRecord == null) "📂 음성파일 비어있음" else "▶️ 재생하기",
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable {
                        if (uiState.audioRecord != null) {
                            playButtonClicked()
                        }
                    }
                    .padding(20.dp),
                style = MaterialTheme.typography.titleMedium,
                color = if (uiState.audioRecord != null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (uiState.audioRecord != null) FontWeight.Medium else FontWeight.Normal
            )
        }

        // 🎯 실시간 텍스트 결과 표시
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 400.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📝 실시간 텍스트",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 처리 상태 표시
                        if (uiState.isProcessingSTT) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "변환중",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (uiState.realTimeFullText.isNotEmpty()) {
                            TextButton(
                                onClick = clearText,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("지우기", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 텍스트 영역
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        when {
                            // 아무것도 없을 때
                            uiState.realTimeFullText.isEmpty() && !uiState.isRecording -> {
                                Text(
                                    "🎤 녹음을 시작하면 여기에 실시간으로 텍스트가 나타납니다\n\n💡 실시간 변환을 위해서는 위에서 API 키를 설정해주세요",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                )
                            }

                            // 녹음 중인데 API 키 없음
                            uiState.isRecording && uiState.realTimeFullText.isEmpty() && uiState.clientId.isEmpty() -> {
                                Text(
                                    "🔑 실시간 텍스트 변환을 보려면 위에서 API 키를 입력하세요\n\n현재는 파일 녹음만 진행 중입니다",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                )
                            }

                            // 녹음 중인데 아직 텍스트 없음
                            uiState.isRecording && uiState.realTimeFullText.isEmpty() && uiState.clientId.isNotEmpty() && !uiState.isProcessingSTT -> {
                                Text(
                                    "🎤 음성 인식 대기 중...\n말씀해주시면 2초마다 텍스트로 변환됩니다",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                                )
                            }

                            // 텍스트가 있을 때
                            else -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // 확정된 텍스트 (검은색)
                                    if (uiState.finalText.isNotEmpty()) {
                                        Text(
                                            text = uiState.finalText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                                        )
                                    }

                                    // 실시간 변환 중인 텍스트 (회색, 이탤릭)
                                    if (uiState.partialText.isNotEmpty()) {
                                        Text(
                                            text = uiState.partialText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontStyle = FontStyle.Italic,
                                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                                        )
                                    }

                                    // 변환 중 표시
                                    if (uiState.isProcessingSTT && uiState.realTimeFullText.isNotEmpty()) {
                                        Text(
                                            "⏳ 변환 중...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 하단 상태 정보
                if (uiState.isRecording && uiState.clientId.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🔴 실시간 STT 활성",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            "언어: ${when(uiState.selectedLanguage) {
                                "Kor" -> "🇰🇷 한국어"
                                "Eng" -> "🇺🇸 영어"
                                "Jpn" -> "🇯🇵 일본어"
                                "Chn" -> "🇨🇳 중국어"
                                else -> "🇰🇷 한국어"
                            }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 🎯 에러 메시지들
        // STT 에러
        uiState.sttError?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("❌", style = MaterialTheme.typography.titleMedium)

                    Text(
                        text = "STT 오류: $error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = clearErrors,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("닫기", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // 녹음 에러
        uiState.recordingError?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚠️", style = MaterialTheme.typography.titleMedium)

                    Text(
                        text = "녹음 오류: $error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = clearErrors,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("닫기", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // 🎯 도움말 카드 (하단)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "💡 사용 방법",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "1️⃣ API 키 입력 (선택사항)\n" +
                            "2️⃣ '녹음하기' 터치하여 녹음 시작\n" +
                            "3️⃣ 말씀하시면 실시간으로 텍스트 변환\n" +
                            "4️⃣ '녹음 중...' 터치하여 중지\n" +
                            "5️⃣ '재생하기'로 녹음 파일 확인",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PreviewGasWritingScreen() {
    KONKUKHACKATHONTEAM3Theme {
        GasWritingScreen(
            navigateToRanking = {},
            recordButtonClicked = {},
            playButtonClicked = {},
            updateClientId = {},
            updateClientSecret = {},
            updateLanguage = {},
            clearText = {},
            clearErrors = {},
            uiState = GasWritingUiState(
                hasAudioPermission = true,
                isRecording = true,
                clientId = "sample_client_id",
                finalText = "안녕하세요, 실시간 음성인식 테스트입니다.",
                partialText = "현재 변환 중인 텍스트...",
                chunkCount = 3,
                isProcessingSTT = true,
                audioRecord = null
            )
        )
    }
}