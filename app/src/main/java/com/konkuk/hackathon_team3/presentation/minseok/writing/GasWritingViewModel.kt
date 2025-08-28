package com.konkuk.hackathon_team3.presentation.minseok.writing

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.hackathon_team3.data.service.ClovaServicePool
import com.konkuk.hackathon_team3.data.service.ServicePool
import com.konkuk.hackathon_team3.presentation.util.showCustomToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

data class GasWritingUiState(
    // 게시물 관련
    val imageUri: Uri? = null,
    val textContent: String = "",

    // 실시간 STT 관련
    val isRecording: Boolean = false,
    val hasAudioPermission: Boolean = false,
    val hasCameraPermission: Boolean = false,
    val isProcessingSTT: Boolean = false,
    val sttError: String? = null,
    val sttDebugInfo: String = "", // 디버깅 정보 추가

    val isLoading : Boolean = false
)

class GasWritingViewModel : ViewModel() {
    private val uploadService by lazy { ServicePool.uploadService }

    private val _uiState = MutableStateFlow(GasWritingUiState())
    val uiState: StateFlow<GasWritingUiState> = _uiState.asStateFlow()

    // 🎯 개선된 고정 값들
    companion object{
        private const val CLIENT_ID = "fsg56x4fn7"
        private const val CLIENT_SECRET = "ng7hVU1DwYZ7CwM2TfjAej8nfmyGzkbq0DX6TpE8"
        private const val LANGUAGE = "Kor"

        // 청크 시간을 2초로 증가 (더 정확한 인식)
        private const val CHUNK_DURATION_MS = 2000L

        // 처리 딜레이 감소로 더 빠른 반응
        private const val PROCESSING_DELAY_MS = 10L

        // VAD(Voice Activity Detection)를 위한 임계값
        private const val VOICE_THRESHOLD = 500.0
        private const val SILENCE_THRESHOLD_MS = 1500L // 1.5초 침묵 후 처리

        // 버퍼 배수 증가
        private const val BUFFER_SIZE_MULTIPLIER = 4
    }

    // 실시간 STT용
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    // VAD를 위한 변수 추가
    private var lastVoiceDetectedTime = 0L
    private var currentSilenceDuration = 0L

    // 오디오 설정 (개선)
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    // 버퍼 크기 4배로 증가
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) * BUFFER_SIZE_MULTIPLIER

    private val clovaSpeechApi = ClovaServicePool.clovaSpeechService

    // 권한 관리
    fun updateAudioPermission(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(hasAudioPermission = hasPermission)
    }

    fun updateCameraPermission(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(hasCameraPermission = hasPermission)
    }

    // 이미지 설정
    fun setImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    // 텍스트 수정 (TextField용)
    fun updateTextContent(text: String) {
        _uiState.value = _uiState.value.copy(textContent = text)
    }


    // 🎯 실시간 STT 토글
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleRecording(context: Context) {
        if (_uiState.value.isRecording) {
            stopRealTimeSTT()
        } else {
            startRealTimeSTT(context)
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRealTimeSTT(context: Context) {
        if (!_uiState.value.hasAudioPermission) {
            _uiState.value = _uiState.value.copy(sttError = "마이크 권한이 필요합니다")
            return
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                audioRecord?.startRecording()

                _uiState.value = _uiState.value.copy(
                    isRecording = true,
                    sttError = null,
                    sttDebugInfo = "녹음 시작 - 버퍼크기: $bufferSize"
                )

                startRealTimeProcessing(context)
                Log.d("GasWritingViewModel", "실시간 STT 시작 - 버퍼크기: $bufferSize")
            } else {
                throw IllegalStateException("AudioRecord 초기화 실패")
            }
        } catch (e: Exception) {
            Log.e("GasWritingViewModel", "실시간 STT 시작 실패", e)
            _uiState.value = _uiState.value.copy(
                sttError = "실시간 STT 시작 실패: ${e.message}",
                sttDebugInfo = "오류: ${e.message}"
            )
        }
    }

    private fun startRealTimeProcessing(context: Context) {
        recordingJob = viewModelScope.launch(Dispatchers.IO) {
            // 더 정확한 버퍼 크기 계산
            val samplesPerChunk = (sampleRate * CHUNK_DURATION_MS / 1000).toInt()
            val audioBuffer = ShortArray(samplesPerChunk)
            val audioChunks = mutableListOf<ByteArray>()

            var lastProcessTime = System.currentTimeMillis()
            var totalAudioProcessed = 0L
            var chunkCount = 0

            Log.d("GasWritingViewModel", "청크당 샘플 수: $samplesPerChunk")

            while (_uiState.value.isRecording &&
                audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {

                try {
                    // 오디오 데이터 읽기
                    val shortsRead = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0

                    if (shortsRead > 0) {
                        // ShortArray를 ByteArray로 변환 (Little Endian)
                        val byteArray = ByteArray(shortsRead * 2)
                        for (i in 0 until shortsRead) {
                            val shortValue = audioBuffer[i]
                            byteArray[i * 2] = (shortValue.toInt() and 0xFF).toByte()
                            byteArray[i * 2 + 1] = ((shortValue.toInt() shr 8) and 0xFF).toByte()
                        }

                        // VAD - 음성 활동 감지
                        val hasVoice = isVoicePresent(audioBuffer.sliceArray(0 until shortsRead))

                        if (hasVoice) {
                            lastVoiceDetectedTime = System.currentTimeMillis()
                            currentSilenceDuration = 0
                            audioChunks.add(byteArray)

                            Log.d("GasWritingViewModel", "음성 감지됨 - 진폭 임계값 초과")
                        } else {
                            currentSilenceDuration = System.currentTimeMillis() - lastVoiceDetectedTime

                            // 침묵이 있어도 일정 시간까지는 버퍼에 추가
                            if (currentSilenceDuration < SILENCE_THRESHOLD_MS && audioChunks.isNotEmpty()) {
                                audioChunks.add(byteArray)
                            }
                        }

                        val currentTime = System.currentTimeMillis()
                        val shouldProcess = (currentTime - lastProcessTime >= CHUNK_DURATION_MS && audioChunks.isNotEmpty()) ||
                                (currentSilenceDuration >= SILENCE_THRESHOLD_MS && audioChunks.isNotEmpty())

                        if (shouldProcess) {
                            chunkCount++
                            val audioDataSize = audioChunks.sumOf { it.size }
                            val durationSeconds = audioDataSize.toFloat() / (sampleRate * 2)

                            Log.d("GasWritingViewModel", "청크 #$chunkCount 처리 시작 - 크기: ${audioDataSize}bytes, 길이: ${durationSeconds}초")

                            withContext(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(
                                    isProcessingSTT = true,
                                    sttDebugInfo = "청크 #$chunkCount 처리중 (${String.format("%.1f", durationSeconds)}초)"
                                )
                            }

                            try {
                                // 오디오 청크 결합
                                val combinedAudio = combineAudioChunks(audioChunks.toList())

                                // WAV 파일 생성
                                val tempWavFile = createTempWavFile(context, combinedAudio)

                                // 동기적으로 STT 처리 (순서 보장)
                                val recognizedText = processSTTChunk(tempWavFile)

                                // 텍스트가 비어있지 않고 "Unit"이 아닌 경우만 처리
                                if (recognizedText.isNotBlank() && !recognizedText.contains("Unit")) {
                                    withContext(Dispatchers.Main) {
                                        val currentState = _uiState.value
                                        val newText = if (currentState.textContent.isEmpty()) {
                                            recognizedText
                                        } else {
                                            "${currentState.textContent} $recognizedText"
                                        }

                                        _uiState.value = currentState.copy(
                                            textContent = newText,
                                            sttError = null,
                                            sttDebugInfo = "인식 성공: '$recognizedText'"
                                        )

                                        Log.d("GasWritingViewModel", "텍스트 인식 성공: $recognizedText")
                                    }
                                } else if (recognizedText.contains("Unit")) {
                                    Log.w("GasWritingViewModel", "Unit이 포함된 텍스트 감지: $recognizedText")
                                }

                                audioChunks.clear()
                                lastProcessTime = currentTime
                                totalAudioProcessed += audioDataSize

                            } catch (e: Exception) {
                                Log.e("GasWritingViewModel", "청크 처리 중 오류", e)
                                withContext(Dispatchers.Main) {
                                    _uiState.value = _uiState.value.copy(
                                        sttError = "처리 오류: ${e.message}",
                                        sttDebugInfo = "청크 #$chunkCount 처리 실패"
                                    )
                                }
                            }

                            withContext(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(isProcessingSTT = false)
                            }
                        }
                    }

                    delay(PROCESSING_DELAY_MS)

                } catch (e: Exception) {
                    Log.e("GasWritingViewModel", "오디오 읽기 오류", e)
                    withContext(Dispatchers.Main) {
                        _uiState.value = _uiState.value.copy(
                            sttError = "오디오 읽기 오류: ${e.message}"
                        )
                    }
                    break
                }
            }

            Log.d("GasWritingViewModel", "녹음 종료 - 총 처리된 오디오: ${totalAudioProcessed}bytes, 청크 수: $chunkCount")
        }
    }

    // VAD (Voice Activity Detection) 구현
    private fun isVoicePresent(audioData: ShortArray): Boolean {
        if (audioData.isEmpty()) return false

        // RMS (Root Mean Square) 계산
        val rms = kotlin.math.sqrt(
            audioData.map { it.toDouble() * it.toDouble() }.average()
        )

        // 최대 진폭도 체크
        val maxAmplitude = audioData.map { abs(it.toInt()) }.maxOrNull() ?: 0

        val hasVoice = rms > VOICE_THRESHOLD || maxAmplitude > VOICE_THRESHOLD * 3

        if (hasVoice) {
            Log.d("GasWritingViewModel", "음성 감지 - RMS: ${String.format("%.2f", rms)}, 최대진폭: $maxAmplitude")
        }

        return hasVoice
    }

    // 동기적 STT 처리 (개선)
    private suspend fun processSTTChunk(audioFile: File): String {
        return try {
            val fileSize = audioFile.length()
            Log.d("GasWritingViewModel", "STT 요청 - 파일크기: ${fileSize}bytes")

            val requestBody = audioFile.readBytes().toRequestBody("application/octet-stream".toMediaTypeOrNull())

            val response = withContext(Dispatchers.IO) {
                clovaSpeechApi.speechToText(
                    clientId = CLIENT_ID,
                    clientSecret = CLIENT_SECRET,
                    language = LANGUAGE,
                    audioData = requestBody
                )
            }

            // 결과를 명확하게 String으로 처리
            val result: String = if (response.isSuccessful) {
                response.body()?.let { speechResponse ->
                    // text가 null일 수 있으므로 안전하게 처리
                    val text = speechResponse.text?.trim() ?: ""
                    Log.d("GasWritingViewModel", "STT 응답: '$text'")
                    text
                } ?: ""
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "잘못된 요청 (오디오 형식 확인)"
                    401 -> "인증 실패 (API 키 확인)"
                    413 -> "파일이 너무 큼"
                    429 -> "API 호출 한도 초과"
                    500 -> "서버 오류"
                    else -> "STT 오류 (${response.code()})"
                }

                Log.e("GasWritingViewModel", "STT 오류: $errorMessage - ${response.errorBody()?.string()}")

                // UI 업데이트는 별도로 처리하고 빈 문자열 반환
                viewModelScope.launch(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        sttError = errorMessage,
                        sttDebugInfo = "응답코드: ${response.code()}"
                    )
                }
                ""
            }

            audioFile.delete()
            result

        } catch (e: Exception) {
            Log.e("GasWritingViewModel", "STT 처리 실패", e)

            // UI 업데이트는 별도로 처리
            viewModelScope.launch(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    sttError = "네트워크 오류: ${e.message}",
                    sttDebugInfo = "예외: ${e.javaClass.simpleName}"
                )
            }

            audioFile.delete()
            ""
        }
    }

    private fun stopRealTimeSTT() {
        recordingJob?.cancel()
        recordingJob = null

        audioRecord?.apply {
            try {
                if (recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    stop()
                }
                release()
            } catch (e: Exception) {
                Log.e("GasWritingViewModel", "AudioRecord 중지 오류", e)
            }
        }
        audioRecord = null

        _uiState.value = _uiState.value.copy(
            isRecording = false,
            isProcessingSTT = false,
            sttDebugInfo = "녹음 중지됨"
        )

        Log.d("GasWritingViewModel", "실시간 STT 중지 완료")
    }

    // 유틸리티 함수들 (개선)
    private fun combineAudioChunks(chunks: List<ByteArray>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        chunks.forEach { chunk ->
            outputStream.write(chunk)
        }
        val result = outputStream.toByteArray()
        Log.d("GasWritingViewModel", "오디오 청크 결합 - 청크 수: ${chunks.size}, 총 크기: ${result.size}bytes")
        return result
    }

    private fun createTempWavFile(context: Context, audioData: ByteArray): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(Date())
        val file = File.createTempFile("realtime_${timeStamp}_", ".wav", context.cacheDir)

        try {
            FileOutputStream(file).use { fos ->
                writeWavHeader(fos, audioData.size)
                fos.write(audioData)
            }
            Log.d("GasWritingViewModel", "WAV 파일 생성 완료 - ${file.name}, 크기: ${file.length()}bytes")
        } catch (e: IOException) {
            Log.e("GasWritingViewModel", "WAV 파일 생성 실패", e)
            throw e
        }

        return file
    }

    private fun writeWavHeader(fos: FileOutputStream, audioDataSize: Int) {
        val totalDataLen = audioDataSize + 36
        val channels = 1
        val byteRate = sampleRate * channels * 2

        fos.write("RIFF".toByteArray())
        fos.write(intToByteArray(totalDataLen))
        fos.write("WAVE".toByteArray())
        fos.write("fmt ".toByteArray())
        fos.write(intToByteArray(16))
        fos.write(shortToByteArray(1))
        fos.write(shortToByteArray(channels.toShort()))
        fos.write(intToByteArray(sampleRate))
        fos.write(intToByteArray(byteRate))
        fos.write(shortToByteArray((channels * 2).toShort()))
        fos.write(shortToByteArray(16))
        fos.write("data".toByteArray())
        fos.write(intToByteArray(audioDataSize))
    }

    private fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 24) and 0xFF).toByte()
        )
    }

    private fun shortToByteArray(value: Short): ByteArray {
        return byteArrayOf(
            (value.toInt() and 0xFF).toByte(),
            ((value.toInt() shr 8) and 0xFF).toByte()
        )
    }

    fun uploadFeed(context: Context, callback: () -> Unit) {
        val uri = uiState.value.imageUri ?: return
        val text = uiState.value.textContent

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IOException("Uri InputStream is null")

                val requestFile = inputStream.readBytes()
                    .toRequestBody("image/*".toMediaTypeOrNull())

                val fileName = "image_${System.currentTimeMillis()}.jpg"
                val mediaPart = MultipartBody.Part.createFormData("media", fileName, requestFile)

                val textPart = text.toRequestBody("text/plain".toMediaTypeOrNull())
                val memberIdPart = "2".toRequestBody("text/plain".toMediaTypeOrNull())

                uploadService.postFeed(
                    memberId = memberIdPart,
                    media = mediaPart,
                    text = textPart
                )

                Log.d("Upload", "업로드 성공")

            } catch (e: Exception) {
                Log.e("Upload", "업로드 실패", e)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
                callback()
                showCustomToast(
                    context = context,
                    message = "업로드 완료!"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(sttError = null, sttDebugInfo = "")
    }

    override fun onCleared() {
        super.onCleared()
        stopRealTimeSTT()
    }
}