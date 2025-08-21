package com.konkuk.hackathon_team3.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExampleResponseDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
)