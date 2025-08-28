package com.konkuk.hackathon_team3.presentation.minseo.calendar

import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.data.service.ServicePool

class GasCalendarViewModel : ViewModel() {
    private val exampleService by lazy { ServicePool.exampleService }
}