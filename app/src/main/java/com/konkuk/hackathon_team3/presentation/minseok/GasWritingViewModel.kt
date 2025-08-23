package com.konkuk.hackathon_team3.presentation.minseok

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.hackathon_team3.data.service.ClovaServicePool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GasWritingUiState(
    val pageState: Int = 1,
    val imageRecord: Uri? = null,
    val audioRecord: File? = null,
    val enrollButtonEnabled: Boolean = false,
    val textRecord: String = "",

    // 기존 녹음 기능
    val isRecording: Boolean = false,
    val hasAudioPermission: Boolean = false,
    val recordingError: String? = null,

    // 실시간 STT 관련 필드
    val isRealTimeSTT: Boolean = false,
    val partialText: String = "",
    val finalText: String = "",
    val realTimeFullText: String = "",
    val sttError: String? = null,
    val clientId: String = "fsg56x4fn7",
    val clientSecret: String = "ng7hVU1DwYZ7CwM2TfjAej8nfmyGzkbq0DX6TpE8",
    val selectedLanguage: String = "Kor",

    // STT 상태 표시
    val isProcessingSTT: Boolean = false,
    val chunkCount: Int = 0
)

class GasWritingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GasWritingUiState())
    val uiState: StateFlow<GasWritingUiState> = _uiState.asStateFlow()

    // 기존 녹음용 (파일 저장용)
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var mediaPlayer: MediaPlayer? = null

    // 실시간 STT용 AudioRecord
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    // 오디오 설정
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) * 2

    private val clovaSpeechApi = ClovaServicePool.clovaSpeechService

    // API 설정 함수들
    fun updateClientId(clientId: String) {
        _uiState.value = _uiState.value.copy(clientId = clientId)
    }

    fun updateClientSecret(clientSecret: String) {
        _uiState.value = _uiState.value.copy(clientSecret = clientSecret)
    }

    fun updateLanguage(language: String) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
    }

    fun updatePermission(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(hasAudioPermission = hasPermission)
    }

    fun clearText() {
        _uiState.value = _uiState.value.copy(
            partialText = "",
            finalText = "",
            realTimeFullText = "",
            textRecord = ""
        )
    }

    // 🎯 기존 녹음 기능 + 실시간 STT 통합
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun recordButtonClicked(context: Context) {
        if (_uiState.value.isRecording) {
            stopRecording()
            stopRealTimeSTT() // 녹음 중지할 때 실시간 STT도 중지
        } else {
            startRecording(context)
            startRealTimeSTT(context) // 녹음 시작할 때 실시간 STT도 시작
        }
    }

    private fun startRecording(context: Context) {
        Log.d("GasWritingViewModel", "파일 녹음 시작")

        if (!_uiState.value.hasAudioPermission) {
            _uiState.value = _uiState.value.copy(recordingError = "녹음 권한이 필요합니다")
            return
        }

        try {
            outputFile = createOutputFile(context)

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }

            _uiState.value = _uiState.value.copy(
                isRecording = true,
                recordingError = null,
                sttError = null
            )

            Log.d("GasWritingViewModel", "파일 녹음 시작 성공")

        } catch (e: Exception) {
            Log.e("GasWritingViewModel", "파일 녹음 시작 실패", e)
            _uiState.value = _uiState.value.copy(
                recordingError = "녹음 시작 실패: ${e.message}"
            )
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
            mediaRecorder = null

            _uiState.value = _uiState.value.copy(
                isRecording = false,
                audioRecord = outputFile,
                recordingError = null
            )

            Log.d("GasWritingViewModel", "파일 녹음 중지 성공")

        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isRecording = false,
                recordingError = "녹음 중지 실패: ${e.message}"
            )
        }
    }

    // 🎯 실시간 STT 시작 (녹음과 동시에)
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRealTimeSTT(context: Context) {
        // API 키 체크 (없으면 그냥 녹음만)
        if (_uiState.value.clientId.isEmpty() || _uiState.value.clientSecret.isEmpty()) {
            Log.d("GasWritingViewModel", "API 키가 없어서 실시간 STT는 건너뛰고 녹음만 진행")
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
                startRealTimeProcessing(context)

                // 실시간 STT 텍스트 초기화
                _uiState.value = _uiState.value.copy(
                    finalText = "",
                    realTimeFullText = "",
                    partialText = "",
                    chunkCount = 0,
                    isRealTimeSTT = true
                )

                Log.d("GasWritingViewModel", "실시간 STT 동시 시작됨")
            }
        } catch (e: Exception) {
            Log.e("GasWritingViewModel", "실시간 STT 시작 실패 (녹음은 계속)", e)
            _uiState.value = _uiState.value.copy(
                sttError = "실시간 STT 시작 실패: ${e.message}"
            )
        }
    }

    private fun startRealTimeProcessing(context: Context) {
        recordingJob = viewModelScope.launch(Dispatchers.IO) {
            val chunkDurationMs = 2000L // 2초마다 처리
            val bufferSizePerChunk = (sampleRate * 2 * chunkDurationMs / 1000).toInt() // 16bit = 2bytes
            val audioBuffer = ShortArray(bufferSizePerChunk / 2)
            val audioChunks = mutableListOf<ByteArray>()

            var lastProcessTime = System.currentTimeMillis()
            var chunkCounter = 0

            Log.d("GasWritingViewModel", "실시간 처리 루프 시작")

            while (_uiState.value.isRecording &&
                audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {

                try {
                    // 오디오 데이터 읽기
                    val bytesRead = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0

                    if (bytesRead > 0) {
                        // Short 배열을 Byte 배열로 변환 (Little Endian)
                        val byteArray = ByteArray(bytesRead * 2)
                        for (i in 0 until bytesRead) {
                            val shortValue = audioBuffer[i]
                            byteArray[i * 2] = (shortValue.toInt() and 0xFF).toByte()
                            byteArray[i * 2 + 1] = ((shortValue.toInt() shr 8) and 0xFF).toByte()
                        }
                        audioChunks.add(byteArray)

                        // 2초마다 STT 처리
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastProcessTime >= chunkDurationMs && audioChunks.isNotEmpty()) {

                            chunkCounter++
                            Log.d("GasWritingViewModel", "청크 #$chunkCounter 처리 시작")

                            withContext(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(
                                    isProcessingSTT = true,
                                    chunkCount = chunkCounter
                                )
                            }

                            try {
                                // 오디오 청크들을 하나로 합치기
                                val combinedAudio = combineAudioChunks(audioChunks.toList())

                                // WAV 파일 생성
                                val tempWavFile = createTempWavFile(context, combinedAudio)

                                // STT 요청 (비동기)
                                launch {
                                    processSTTChunk(tempWavFile, chunkCounter)
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

                    delay(50) // CPU 사용량 조절

                } catch (e: Exception) {
                    Log.e("GasWritingViewModel", "오디오 읽기 오류", e)
                    break
                }
            }

            Log.d("GasWritingViewModel", "실시간 처리 루프 종료")
        }
    }

    private suspend fun processSTTChunk(audioFile: File, chunkNumber: Int) {
        try {
            Log.d("GasWritingViewModel", "청크 #$chunkNumber STT 요청 시작")

            val requestBody = audioFile.readBytes().toRequestBody("application/octet-stream".toMediaTypeOrNull())

            val response = withContext(Dispatchers.IO) {
                clovaSpeechApi.speechToText(
                    clientId = _uiState.value.clientId,
                    clientSecret = _uiState.value.clientSecret,
                    language = _uiState.value.selectedLanguage,
                    audioData = requestBody
                )
            }

            if (response.isSuccessful) {
                response.body()?.let { speechResponse ->
                    val recognizedText = speechResponse.text.trim()

                    if (recognizedText.isNotEmpty()) {
                        Log.d("GasWritingViewModel", "청크 #$chunkNumber 인식 결과: $recognizedText")

                        withContext(Dispatchers.Main) {
                            val currentState = _uiState.value
                            val newFinalText = if (currentState.finalText.isEmpty()) {
                                recognizedText
                            } else {
                                "${currentState.finalText} $recognizedText"
                            }

                            _uiState.value = currentState.copy(
                                finalText = newFinalText,
                                realTimeFullText = newFinalText,
                                textRecord = newFinalText,
                                partialText = "", // 확정되었으므로 부분 텍스트 클리어
                                sttError = null
                            )
                        }
                    } else {
                        Log.d("GasWritingViewModel", "청크 #$chunkNumber: 빈 결과")
                    }
                }
            } else {
                Log.e("GasWritingViewModel", "청크 #$chunkNumber STT API 오류: ${response.code()}")

                withContext(Dispatchers.Main) {
                    val errorMessage = when (response.code()) {
                        400 -> "잘못된 요청 (음성 파일 또는 언어 설정 오류)"
                        401 -> "인증 실패 (API 키 확인 필요)"
                        413 -> "파일이 너무 큼 (최대 3MB, 60초)"
                        429 -> "API 호출 한도 초과"
                        500 -> "서버 오류"
                        else -> "STT 오류 (${response.code()})"
                    }

                    _uiState.value = _uiState.value.copy(
                        sttError = errorMessage
                    )
                }
            }

            // 임시 파일 삭제
            audioFile.delete()

        } catch (e: Exception) {
            Log.e("GasWritingViewModel", "청크 #$chunkNumber STT 처리 실패", e)

            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    sttError = "네트워크 오류: ${e.message}"
                )
            }

            audioFile.delete()
        }
    }

    private fun stopRealTimeSTT() {
        Log.d("GasWritingViewModel", "실시간 STT 중지 시작")

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
            isRealTimeSTT = false,
            isProcessingSTT = false,
            partialText = ""
        )

        Log.d("GasWritingViewModel", "실시간 STT 중지 완료")
    }

    // 🎯 기존 재생 기능
    fun playRecording() {
        outputFile?.let { file ->
            try {
                stopPlaying() // 기존 재생 중이면 먼저 중지

                mediaPlayer = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()

                    setOnCompletionListener {
                        Log.d("GasWritingViewModel", "재생 완료")
                    }
                }
                Log.d("GasWritingViewModel", "재생 시작: ${file.name}")
            } catch (e: Exception) {
                Log.e("GasWritingViewModel", "재생 실패", e)
                _uiState.value = _uiState.value.copy(
                    recordingError = "재생 실패: ${e.message}"
                )
            }
        }
    }

    private fun stopPlaying() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
            mediaPlayer = null
            Log.d("GasWritingViewModel", "재생 중지")
        } catch (e: Exception) {
            Log.e("GasWritingViewModel", "재생 중지 실패", e)
        }
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
        val byteRate = sampleRate * channels * 2 // 16bit = 2 bytes

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

    private fun createOutputFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "AUDIO_${timeStamp}_",
            ".mp4",
            storageDir
        )
    }

    // 에러 클리어
    fun clearErrors() {
        _uiState.value = _uiState.value.copy(
            recordingError = null,
            sttError = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopRealTimeSTT()
        mediaRecorder?.release()
        mediaRecorder = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}