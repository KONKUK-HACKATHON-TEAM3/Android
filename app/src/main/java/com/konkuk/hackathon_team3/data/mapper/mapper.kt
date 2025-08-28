package com.konkuk.hackathon_team3.data.mapper

import com.konkuk.hackathon_team3.data.dto.response.DailyMissionDto
import com.konkuk.hackathon_team3.data.dto.response.FamilyMemberDto
import com.konkuk.hackathon_team3.data.dto.response.FamilyStoryDto
import com.konkuk.hackathon_team3.data.dto.response.FeedDto
import com.konkuk.hackathon_team3.data.dto.response.FeedResponseDto
import com.konkuk.hackathon_team3.data.dto.response.HomeResponseDto
import com.konkuk.hackathon_team3.data.dto.response.WeeklyRankingDto
import com.konkuk.hackathon_team3.data.dto.response.WeeklyRankingItemResponseDto
import com.konkuk.hackathon_team3.data.dto.response.WeeklyRankingResponseDto
import com.konkuk.hackathon_team3.presentation.model.FeedData
import com.konkuk.hackathon_team3.presentation.model.HomeFamilyData
import com.konkuk.hackathon_team3.presentation.model.HomeRecentFeedData
import com.konkuk.hackathon_team3.presentation.model.MissionData
import com.konkuk.hackathon_team3.presentation.model.ProfileType
import com.konkuk.hackathon_team3.presentation.model.RankingData

fun FeedDto.toFeedData(): FeedData =
    FeedData(
        feedId = feedId,
        profile = profile.toProfileType(),
        nickname = nickname,
        text = text.takeIf { it.isNotBlank() },
        imageUrl = imageUrl,
        tag = tag,
        likeCount = likeCount,
        likeStatus = likeStatus
    )

// 리스트 매핑
fun FeedResponseDto.toFeedDataList(): List<FeedData> =
    feedList.map { it.toFeedData() }

// DailyMissionDto -> MissionData
fun DailyMissionDto.toMissionData(): MissionData =
    MissionData(
        title = name,
        point = point,
        description = description,
        isCleared = clearStatus
    )

// FamilyMemberDto -> HomeFamilyData
fun FamilyMemberDto.toHomeFamilyData(): HomeFamilyData =
    HomeFamilyData(
        nickname = nickname,
        profileEnum = profile
    )

// FamilyStoryDto -> HomeRecentFeedData
fun FamilyStoryDto.toHomeRecentFeedData(): HomeRecentFeedData =
    HomeRecentFeedData(
        nickname = nickname,
        imageUrl = imageUrl
    )

// WeeklyRankingDto -> RankingData
fun List<WeeklyRankingDto>.toRankingData(): List<RankingData> =
    mapIndexed { index, weeklyRankingDto ->
        RankingData(
            rank = index + 1,
            nickname = weeklyRankingDto.nickname,
            point = weeklyRankingDto.score
        )
    }


// HomeResponse 전체 -> UI 모델 모음
fun HomeResponseDto.toUiModels(): Triple<List<MissionData>, List<HomeFamilyData>, List<HomeRecentFeedData>> {
    val missions = dailyMissionList.map { it.toMissionData() }
    val families = familyList.map { it.toHomeFamilyData() }
    val feeds = familyStoryList?.map { it.toHomeRecentFeedData() } ?: emptyList()
    return Triple(missions, families, feeds)
}

fun List<WeeklyRankingItemResponseDto>.toRankingDataList(): List<RankingData> {
    return this.mapIndexed { index, item ->
        RankingData(
            rank = index + 1,
            nickname = item.nickname,
            point = item.score
        )
    }
}

private fun String.toProfileType(): ProfileType = when (trim().uppercase()) {
    "FATHER", "DAD", "아빠" -> ProfileType.FATHER
    "MOTHER", "MOM", "엄마" -> ProfileType.MOTHER
    "GRANDFATHER", "할아버지" -> ProfileType.GRANDFATHER
    "GRANDMOTHER", "할머니" -> ProfileType.GRANDMOTHER
    "BOY", "소년" -> ProfileType.BOY
    "GIRL", "소녀" -> ProfileType.GIRL
    "MAN_1" -> ProfileType.MAN_1
    "WOMAN_1" -> ProfileType.WOMAN_1
    "MAN_2" -> ProfileType.MAN_2
    "WOMAN_2" -> ProfileType.WOMAN_2
    else -> ProfileType.MAN_1
}