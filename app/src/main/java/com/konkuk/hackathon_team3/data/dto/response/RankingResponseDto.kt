package com.konkuk.hackathon_team3.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyRankingResponseDto(
    val weeklyPrize: String,
    val weeklyRanking: List<WeeklyRankingItemResponseDto>
)

@Serializable
data class WeeklyRankingItemResponseDto(
    val nickname: String,
    val score: Int
)