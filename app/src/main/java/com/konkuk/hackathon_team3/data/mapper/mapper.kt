package com.konkuk.hackathon_team3.data.mapper

import com.konkuk.hackathon_team3.data.dto.response.DailyMissionDto
import com.konkuk.hackathon_team3.data.dto.response.FamilyMemberDto
import com.konkuk.hackathon_team3.data.dto.response.FamilyStoryDto
import com.konkuk.hackathon_team3.data.dto.response.HomeResponseDto
import com.konkuk.hackathon_team3.data.dto.response.WeeklyRankingDto
import com.konkuk.hackathon_team3.data.dto.response.WeeklyRankingItemResponseDto
import com.konkuk.hackathon_team3.data.dto.response.WeeklyRankingResponseDto
import com.konkuk.hackathon_team3.presentation.model.HomeFamilyData
import com.konkuk.hackathon_team3.presentation.model.HomeRecentFeedData
import com.konkuk.hackathon_team3.presentation.model.MissionData
import com.konkuk.hackathon_team3.presentation.model.RankingData

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