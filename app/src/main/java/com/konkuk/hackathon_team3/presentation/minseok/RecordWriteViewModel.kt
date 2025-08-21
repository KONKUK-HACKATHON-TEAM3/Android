package com.konkuk.hackathon_team3.presentation.minseok

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.data.service.ServicePool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

data class RecordWriteUiState(
    val pageState: Int = 1,
    val imageRecord: Uri? = null,
    val audioRecord: File? = null,
    val enrollButtonEnabled: Boolean = false,
    val  textRecord: String = "",
    )


class RecordWriteViewModel : ViewModel() {
    private val exampleService by lazy { ServicePool.exampleService }

    private val _uiState = MutableStateFlow(RecordWriteUiState())
    val uiState: StateFlow<RecordWriteUiState> = _uiState.asStateFlow()


}