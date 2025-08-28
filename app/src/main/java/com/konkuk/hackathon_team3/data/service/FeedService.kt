package com.konkuk.hackathon_team3.data.service

import com.konkuk.hackathon_team3.data.dto.response.CalendarDto
import com.konkuk.hackathon_team3.data.dto.response.FeedResponseDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface FeedService {
    @GET("api/feeds")
    suspend fun getFeed(
        @Query("memberId") memberId: Long,
        @Query("date") date: LocalDate
    ): FeedResponseDto

    @POST("api/feeds/{feedId}")
    suspend fun postLike(
        @Path("feedId") feedId: Long,
        @Query("memberId") memberId: Long,
    )

    @DELETE("api/feeds/{feedId}")
    suspend fun deleteLike(
        @Path("feedId") feedId: Long,
        @Query("memberId") memberId: Long,
    )

    @GET("api/feeds/dates")
    suspend fun getDates(
        @Query("yearMonth") yearMonth: String
    ): CalendarDto

}