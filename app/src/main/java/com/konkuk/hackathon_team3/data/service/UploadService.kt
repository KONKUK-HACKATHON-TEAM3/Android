package com.konkuk.hackathon_team3.data.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {

    @Multipart
    @POST("/api/feeds")
    suspend fun postFeed(
        @Part("memberId") memberId: RequestBody,
        @Part media: MultipartBody.Part,
        @Part("text") text: RequestBody?
    )
}