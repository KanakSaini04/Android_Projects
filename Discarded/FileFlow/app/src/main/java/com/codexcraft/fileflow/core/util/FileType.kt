package com.codexcraft.fileflow.core.util

object FileType {
    fun isPdf(mime: String?) = mime == "application/pdf"
    fun isImage(mime: String?) = mime?.startsWith("image/") == true
    fun isVideo(mime: String?) = mime?.startsWith("video/") == true
    fun isAudio(mime: String?) = mime?.startsWith("audio/") == true
    fun isText(mime: String?) =
        mime == "text/plain" || mime == "text/markdown" || mime == "application/json"
}
