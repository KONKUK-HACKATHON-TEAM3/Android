package com.konkuk.hackathon_team3.data.service

import com.konkuk.hackathon_team3.data.dto.response.WeeklyRankingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RankingService {
    @GET("/api/ranking")
    suspend fun getRanking(
        @Query("memberId") memberId: Long
    ): WeeklyRankingResponseDto
}