package com.konkuk.hackathon_team3.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class HomeResponseDto(
    val dailyMissionList: List<DailyMissionDto>,
    val familyStoryList: List<FamilyStoryDto>?,
    val weeklyRanking: List<WeeklyRankingDto>,
    val familyList: List<FamilyMemberDto>
)

@Serializable

data class DailyMissionDto(
    val name: String,        // 미션 이름
    val point: Int,          // 미션 포인트
    val description: String, // 미션 설명
    val clearStatus: Boolean // 미션 완수 여부
)

@Serializable

data class FamilyStoryDto(
    val nickname: String,
    val imageUrl: String,
    val createdAt: String
)

@Serializable

data class WeeklyRankingDto(
    val nickname: String,
    val score: Int
)

@Serializable

data class FamilyMemberDto(
    val nickname: String,
    val profile: String // Enum 문자열
)