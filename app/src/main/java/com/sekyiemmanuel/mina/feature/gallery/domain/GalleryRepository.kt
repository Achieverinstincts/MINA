package com.sekyiemmanuel.mina.feature.gallery.domain

import java.time.LocalDateTime

enum class GalleryMood(
    val emoji: String,
    val label: String,
) {
    GREAT(emoji = "😄", label = "Great"),
    GOOD(emoji = "🙂", label = "Good"),
    OKAY(emoji = "😐", label = "Okay"),
    LOW(emoji = "😔", label = "Low"),
    BAD(emoji = "😞", label = "Bad"),
}

enum class GalleryGenerationStatus(
    val label: String,
) {
    COMPLETED("Completed"),
    GENERATING("Generating"),
    PENDING("Pending"),
    FAILED("Failed"),
}

data class GalleryArtwork(
    val id: String,
    val entryId: String,
    val entryTitle: String,
    val entryDate: LocalDateTime,
    val mood: GalleryMood?,
    val artStyle: String,
    val aspectRatio: Float,
    val status: GalleryGenerationStatus,
)

interface GalleryRepository {
    suspend fun getArtworks(): List<GalleryArtwork>
}
