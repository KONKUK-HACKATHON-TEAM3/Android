package com.konkuk.hackathon_team3.data.service

import com.konkuk.hackathon_team3.data.dto.response.ApiResponse
import com.konkuk.hackathon_team3.data.dto.response.HomeResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {
    @GET("/api/home")
    suspend fun getHome(
        @Query("memberId") memberId: Long
    ): HomeResponseDto
}