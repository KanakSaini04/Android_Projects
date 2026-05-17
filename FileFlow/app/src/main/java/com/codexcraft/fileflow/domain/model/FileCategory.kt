package com.codexcraft.fileflow.domain.model

enum class FileCategory {
    PDF, DOCUMENT, IMAGE, VIDEO, AUDIO, ARCHIVE, TEXT, OTHER;

    companion object {
        fun fromMimeType(mimeType: String): FileCategory {
            return when {
                mimeType.contains("pdf") -> PDF
                mimeType.contains("document") || mimeType.contains("word") || 
                mimeType.contains("spreadsheet") || mimeType.contains("presentation") -> DOCUMENT
                mimeType.startsWith("image/") -> IMAGE
                mimeType.startsWith("video/") -> VIDEO
                mimeType.startsWith("audio/") -> AUDIO
                mimeType.contains("zip") || mimeType.contains("rar") || 
                mimeType.contains("7z") || mimeType.contains("tar") -> ARCHIVE
                mimeType.startsWith("text/") -> TEXT
                else -> OTHER
            }
        }
    }
}
