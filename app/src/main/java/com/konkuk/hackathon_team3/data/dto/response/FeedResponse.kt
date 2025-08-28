package com.konkuk.hackathon_team3.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class FeedResponseDto(
    val feedList: List<FeedDto>
)

@Serializable
data class FeedDto(
    val feedId: Long,
    val profile: String,
    val nickname: String,
    val text: String,
    val imageUrl: String,
    val tag: String,
    val likeCount: Int,
    val likeStatus: Boolean
)

@Serializable
data class CalendarDto(
    val dateList: List<String>
)