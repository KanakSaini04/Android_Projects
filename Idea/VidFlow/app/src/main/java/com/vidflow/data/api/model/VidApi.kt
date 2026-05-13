package com.vidflow.data.api

import com.vidflow.data.api.model.VideoInfoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VidApi {
    @GET("info")
    suspend fun getVideoInfo(@Query("url") url: String): VideoInfoResponse
}