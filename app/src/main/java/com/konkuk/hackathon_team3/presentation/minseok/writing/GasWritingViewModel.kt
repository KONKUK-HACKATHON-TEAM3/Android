package com.konkuk.hackathon_team3.presentation.minseok.writing

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.hackathon_team3.data.mapper.toRankingDataList
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
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    val isLoading : Boolean = false
)

class GasWritingViewModel : ViewModel() {
    private val uploadService by lazy { ServicePool.uploadService }


    private val _uiState = MutableStateFlow(GasWritingUiState())
    val uiState: StateFlow<GasWritingUiState> = _uiState.asStateFlow()

    // 🎯 고정 값들
    companion object{
        private const val CLIENT_ID = "fsg56x4fn7"
        private const val CLIENT_SECRET = "ng7hVU1DwYZ7CwM2TfjAej8nfmyGzkbq0DX6TpE8"
        private const val LANGUAGE = "Kor"

        private const val CHUNK_DURATION_MS = 600L

        private const val PROCESSING_DELAY_MS = 20L
    }

    // 실시간 STT용만 남김
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    // 오디오 설정
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) * 2

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

    fun clearText() {
        _uiState.value = _uiState.value.copy(textContent = "")
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
                    sttError = null
                )

                startRealTimeProcessing(context)
                Log.d("GasWritingViewModel", "실시간 STT 시작")
            }
        } catch (e: Exception) {
            Log.e("GasWritingViewModel", "실시간 STT 시작 실패", e)
            _uiState.value = _uiState.value.copy(
                sttError = "실시간 STT 시작 실패: ${e.message}"
            )
        }
    }

    private fun startRealTimeProcessing(context: Context) {
        recordingJob = viewModelScope.launch(Dispatchers.IO) {
            val bufferSizePerChunk = (sampleRate * 2 * CHUNK_DURATION_MS / 1000).toInt()
            val audioBuffer = ShortArray(bufferSizePerChunk / 2)
            val audioChunks = mutableListOf<ByteArray>()

            var lastProcessTime = System.currentTimeMillis()

            while (_uiState.value.isRecording &&
                audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {

                try {
                    val bytesRead = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0

                    if (bytesRead > 0) {
                        val byteArray = ByteArray(bytesRead * 2)
                        for (i in 0 until bytesRead) {
                            val shortValue = audioBuffer[i]
                            byteArray[i * 2] = (shortValue.toInt() and 0xFF).toByte()
                            byteArray[i * 2 + 1] = ((shortValue.toInt() shr 8) and 0xFF).toByte()
                        }
                        audioChunks.add(byteArray)

                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastProcessTime >= CHUNK_DURATION_MS && audioChunks.isNotEmpty()) {

                            withContext(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(isProcessingSTT = true)
                            }

                            try {
                                val combinedAudio = combineAudioChunks(audioChunks.toList())
                                val tempWavFile = createTempWavFile(context, combinedAudio)

                                launch {
                                    processSTTChunk(tempWavFile)
                                }

                                audioChunks.clear()
                                lastProcessTime = currentTime

                            } catch (e: Exception) {
                                Log.e("GasWritingViewModel", "청크 처리 중 오류", e)
                            }

                            withContext(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(isProcessingSTT = false)
                            }
                        }
                    }

                    delay(PROCESSING_DELAY_MS)

                } catch (e: Exception) {
                    Log.e("GasWritingViewModel", "오디오 읽기 오류", e)
                    break
                }
            }
        }
    }

    private suspend fun processSTTChunk(audioFile: File) {
        try {
            val requestBody = audioFile.readBytes().toRequestBody("application/octet-stream".toMediaTypeOrNull())

            val response = withContext(Dispatchers.IO) {
                clovaSpeechApi.speechToText(
                    clientId = CLIENT_ID,
                    clientSecret = CLIENT_SECRET,
                    language = LANGUAGE,
                    audioData = requestBody
                )
            }

            if (response.isSuccessful) {
                response.body()?.let { speechResponse ->
                    val recognizedText = speechResponse.text.trim()

                    if (recognizedText.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            val currentState = _uiState.value
                            val newText = if (currentState.textContent.isEmpty()) {
                                recognizedText
                            } else {
                                "${currentState.textContent} $recognizedText"
                            }

                            _uiState.value = currentState.copy(
                                textContent = newText,
                                sttError = null
                            )
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    val errorMessage = when (response.code()) {
                        400 -> "잘못된 요청"
                        401 -> "인증 실패"
                        413 -> "파일이 너무 큼"
                        429 -> "API 호출 한도 초과"
                        500 -> "서버 오류"
                        else -> "STT 오류"
                    }

                    _uiState.value = _uiState.value.copy(sttError = errorMessage)
                }
            }

            audioFile.delete()

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(sttError = "네트워크 오류")
            }
            audioFile.delete()
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
            isProcessingSTT = false
        )
    }

    // 유틸리티 함수들
    private fun combineAudioChunks(chunks: List<ByteArray>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        chunks.forEach { chunk ->
            outputStream.write(chunk)
        }
        return outputStream.toByteArray()
    }

    private fun createTempWavFile(context: Context, audioData: ByteArray): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(Date())
        val file = File.createTempFile("realtime_${timeStamp}_", ".wav", context.cacheDir)

        try {
            FileOutputStream(file).use { fos ->
                writeWavHeader(fos, audioData.size)
                fos.write(audioData)
            }
        } catch (e: IOException) {
            Log.e("GasWritingViewModel", "WAV 파일 생성 실패", e)
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

    fun uploadFeed(context: Context,callback:()->Unit) {
        val uri = uiState.value.imageUri ?: return
        val text = uiState.value.textContent

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // 🔥 File 대신 InputStream 사용
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IOException("Uri InputStream is null")

                val requestFile = inputStream.readBytes()
                    .toRequestBody("image/*".toMediaTypeOrNull())

                val fileName = "image_${System.currentTimeMillis()}.jpg"
                val mediaPart = MultipartBody.Part.createFormData("media", fileName, requestFile)

                val textPart = text.toRequestBody("text/plain".toMediaTypeOrNull())
                val memberIdPart = "1".toRequestBody("text/plain".toMediaTypeOrNull())

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
        _uiState.value = _uiState.value.copy(sttError = null)
    }

    override fun onCleared() {
        super.onCleared()
        stopRealTimeSTT()
    }
}
