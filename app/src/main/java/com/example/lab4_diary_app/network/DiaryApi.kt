package com.example.lab4_diary_app.network

import retrofit2.http.*

interface DiaryApi {
    @GET("entries")
    suspend fun getEntries(): List<DiaryDto>

    @GET("entries/{id}")
    suspend fun getEntryById(@Path("id") id: String): DiaryDto

    @POST("entries")
    suspend fun createEntry(@Body item: DiaryDto): DiaryDto

    @DELETE("entries/{id}")
    suspend fun deleteEntry(@Path("id") id: String)

    @PUT("entries/{id}")
    suspend fun updateEntry(
        @Path("id") id: String,
        @Body dto: DiaryDto
    ): DiaryDto
}