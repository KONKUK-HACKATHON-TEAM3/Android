package com.konkuk.hackathon_team3.data.service

import com.konkuk.hackathon_team3.data.dto.response.FeedResponseDto
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate

interface FeedService {
    @GET("api/feeds")
    suspend fun getFeed(
        @Query("memberId") memberId: Long,
        @Query("date") date: LocalDate
    ): FeedResponseDto
}