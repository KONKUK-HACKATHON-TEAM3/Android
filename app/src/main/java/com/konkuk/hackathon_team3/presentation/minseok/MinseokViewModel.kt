package com.konkuk.hackathon_team3.presentation.minseok

import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.data.service.ServicePool

class MinseokViewModel : ViewModel() {
    private val exampleService by lazy { ServicePool.exampleService }
}