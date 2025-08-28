package com.konkuk.hackathon_team3.presentation.model

data class FeedData(
    val feedId: Long,
    val profile: ProfileType,
    val nickname: String,
    val text: String?,
    val imageUrl: String,
    val tag: String,
    val likeCount: Int,
    val likeStatus: Boolean
)