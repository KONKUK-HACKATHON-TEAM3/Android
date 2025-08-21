package com.konkuk.hackathon_team3.presentation.minseok

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.data.service.ServicePool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RecordWriteUiState(
    val pageState: Int = 1,
    val imageRecord: Uri? = null,
    val audioRecord: File? = null,
    val enrollButtonEnabled: Boolean = false,
    val textRecord: String = "",

    // 음성 녹음 관련 변수들
    val isRecording: Boolean = false,
    val hasAudioPermission: Boolean = false,
    val recordingError: String? = null,
)


class RecordWriteViewModel : ViewModel() {
    private val exampleService by lazy { ServicePool.exampleService }

    private val _uiState = MutableStateFlow(RecordWriteUiState())
    val uiState: StateFlow<RecordWriteUiState> = _uiState.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    private var mediaPlayer: MediaPlayer? = null



    fun recordButtonClicked(context: Context) {
        if (_uiState.value.isRecording) {
            // 녹음 중지
            stopRecording()
        } else {
            // 녹음 시작
            startRecording(context)
        }
    }

    fun updatePermission(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(
            hasAudioPermission = hasPermission
        )
    }

    private fun startRecording(context: Context) {
        Log.d("RecordWriteViewModel", "startRecording called")

        // 권한 체크
        if (!_uiState.value.hasAudioPermission) {
            _uiState.value = _uiState.value.copy(
                recordingError = "녹음 권한이 필요합니다"
            )
            return
        }

        try {
            // 파일 생성
            outputFile = createOutputFile(context)
            Log.d("RecordWriteViewModel", "Output file created: ${outputFile?.absolutePath}")

            // MediaRecorder 설정
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }

            Log.d("RecordWriteViewModel", "Recording started successfully")

            // UI 상태 업데이트
            _uiState.value = _uiState.value.copy(
                isRecording = true,
                recordingError = null
            )
        } catch (e: Exception) {
            Log.e("RecordWriteViewModel", "Recording failed", e)
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

            // UI 상태 업데이트
            _uiState.value = _uiState.value.copy(
                isRecording = false,
                audioRecord = outputFile,
                recordingError = null
            )

            Log.d("RecordWriteViewModel", "audioFile")
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isRecording = false,
                recordingError = "녹음 중지 실패: ${e.message}"
            )
        }
    }

    private fun createOutputFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile(
            "AUDIO_${timeStamp}_",
            ".m4a",
            storageDir
        )
    }

    fun playRecording() {
        outputFile?.let { file ->
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()

                    setOnCompletionListener {
                        Log.d("RecordWriteViewModel", "재생 완료")
                    }
                }
                Log.d("RecordWriteViewModel", "재생 시작: ${file.name}")
            } catch (e: Exception) {
                Log.e("RecordWriteViewModel", "재생 실패", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}