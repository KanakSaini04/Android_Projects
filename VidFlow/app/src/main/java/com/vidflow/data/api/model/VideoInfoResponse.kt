package com.vidflow.data.api.model

data class VideoInfoResponse(
    val title: String,
    val thumbnail: String,
    val formats: List<Format>
)

data class Format(
    val quality: String,
    val downloadUrl: String,
    val size: String
)