package com.sekyiemmanuel.mina.feature.gallery.data

import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryArtwork
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryGenerationStatus
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryMood
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class InMemoryGalleryRepository @Inject constructor() : GalleryRepository {

    override suspend fun getArtworks(): List<GalleryArtwork> {
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        return listOf(
            GalleryArtwork(
                id = "art-001",
                entryId = "entry-001",
                entryTitle = "Morning pages before sunrise",
                entryDate = now.minusHours(4),
                mood = GalleryMood.GREAT,
                artStyle = "watercolor",
                aspectRatio = 1.34f,
                status = GalleryGenerationStatus.COMPLETED,
            ),
            GalleryArtwork(
                id = "art-002",
                entryId = "entry-002",
                entryTitle = "A calm walk after work",
                entryDate = now.minusDays(1),
                mood = GalleryMood.GOOD,
                artStyle = "dreamy",
                aspectRatio = 1.12f,
                status = GalleryGenerationStatus.COMPLETED,
            ),
            GalleryArtwork(
                id = "art-003",
                entryId = "entry-003",
                entryTitle = "Untangling stress from today",
                entryDate = now.minusDays(2),
                mood = GalleryMood.LOW,
                artStyle = "abstract",
                aspectRatio = 1.40f,
                status = GalleryGenerationStatus.GENERATING,
            ),
            GalleryArtwork(
                id = "art-004",
                entryId = "entry-004",
                entryTitle = "Five things I am grateful for",
                entryDate = now.minusDays(3),
                mood = GalleryMood.GOOD,
                artStyle = "minimalist",
                aspectRatio = 1.05f,
                status = GalleryGenerationStatus.COMPLETED,
            ),
            GalleryArtwork(
                id = "art-005",
                entryId = "entry-005",
                entryTitle = "Small wins from this week",
                entryDate = now.minusDays(6),
                mood = GalleryMood.OKAY,
                artStyle = "impressionist",
                aspectRatio = 1.26f,
                status = GalleryGenerationStatus.COMPLETED,
            ),
            GalleryArtwork(
                id = "art-006",
                entryId = "entry-006",
                entryTitle = "Late night thoughts",
                entryDate = now.minusDays(12),
                mood = GalleryMood.BAD,
                artStyle = "dreamy",
                aspectRatio = 1.45f,
                status = GalleryGenerationStatus.FAILED,
            ),
            GalleryArtwork(
                id = "art-007",
                entryId = "entry-007",
                entryTitle = "Future self letter",
                entryDate = now.minusDays(18),
                mood = GalleryMood.GREAT,
                artStyle = "watercolor",
                aspectRatio = 1.21f,
                status = GalleryGenerationStatus.COMPLETED,
            ),
            GalleryArtwork(
                id = "art-008",
                entryId = "entry-008",
                entryTitle = "Weekend reset routine",
                entryDate = now.minusMonths(2),
                mood = GalleryMood.OKAY,
                artStyle = "minimalist",
                aspectRatio = 1.18f,
                status = GalleryGenerationStatus.PENDING,
            ),
            GalleryArtwork(
                id = "art-009",
                entryId = "entry-009",
                entryTitle = "What I learned this quarter",
                entryDate = now.minusMonths(5),
                mood = GalleryMood.GOOD,
                artStyle = "abstract",
                aspectRatio = 1.33f,
                status = GalleryGenerationStatus.COMPLETED,
            ),
            GalleryArtwork(
                id = "art-010",
                entryId = "entry-010",
                entryTitle = "Beginning again",
                entryDate = now.minusYears(1).plusDays(8),
                mood = null,
                artStyle = "dreamy",
                aspectRatio = 1.27f,
                status = GalleryGenerationStatus.COMPLETED,
            ),
        )
    }
}
