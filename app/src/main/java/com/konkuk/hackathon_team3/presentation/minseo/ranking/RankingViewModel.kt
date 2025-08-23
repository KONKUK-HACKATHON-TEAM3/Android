package com.konkuk.hackathon_team3.presentation.minseo.ranking

import androidx.lifecycle.ViewModel
import com.konkuk.hackathon_team3.data.service.ServicePool

class RankingViewModel : ViewModel() {
    private val exampleService by lazy { ServicePool.exampleService }
}