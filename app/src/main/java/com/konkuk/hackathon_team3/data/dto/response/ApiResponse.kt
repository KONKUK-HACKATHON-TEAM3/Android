package com.konkuk.hackathon_team3.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val code: Int? = null,
    val message: String? = null,
    val data: T? = null,
)