package com.konkuk.hackathon_team3.data.service

import com.konkuk.hackathon_team3.data.dto.request.ExampleRequestDto
import com.konkuk.hackathon_team3.data.dto.response.ApiResponse
import com.konkuk.hackathon_team3.data.dto.response.ExampleResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ExampleService {
    @GET("api/v1/data")
    suspend fun getExampleData(): ApiResponse<ExampleResponseDto>

    @POST("api/v1/data")
    suspend fun postExampleData(
        @Body exampleRequestDto: ExampleRequestDto,
    ): ApiResponse<Unit>
}