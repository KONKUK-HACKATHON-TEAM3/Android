package com.konkuk.hackathon_team3.data.service

import com.konkuk.hackathon_team3.data.dto.response.SpeechResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface ClovaSpeechApi {
    @POST("recog/v1/stt")
    suspend fun speechToText(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String,
        @Header("Content-Type") contentType: String = "application/octet-stream",
        @Query("lang") language: String,
        @Body audioData: RequestBody
    ): Response<SpeechResponse>
}