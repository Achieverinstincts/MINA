package com.sekyiemmanuel.mina.feature.gallery.ui

import androidx.compose.ui.graphics.Color
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryArtwork
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryMood
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class GalleryTimeFilter(
    val label: String,
) {
    ALL("All Time"),
    WEEK("This Week"),
    MONTH("This Month"),
    YEAR("This Year"),
}

data class GalleryUiState(
    val artworks: List<GalleryArtwork> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val timeFilter: GalleryTimeFilter = GalleryTimeFilter.ALL,
    val moodFilter: GalleryMood? = null,
    val isShowingFilters: Boolean = false,
    val selectedArtwork: GalleryArtwork? = null,
) {
    val filteredArtworks: List<GalleryArtwork>
        get() = filterArtworks(artworks, timeFilter, moodFilter, searchQuery)

    val artworkCount: Int
        get() = artworks.size

    val artStyles: List<String>
        get() = artworks.map { it.artStyle }.distinct().sorted()

    val hasActiveFilters: Boolean
        get() = timeFilter != GalleryTimeFilter.ALL || moodFilter != null || searchQuery.isNotBlank()

    val emptyStateTitle: String
        get() = when {
            searchQuery.isNotBlank() -> "No Results"
            hasActiveFilters -> "No Matching Artwork"
            else -> "Your Gallery is Empty"
        }

    val emptyStateMessage: String
        get() = when {
            searchQuery.isNotBlank() -> "Try a different search term."
            hasActiveFilters -> "Try adjusting your filters."
            else -> "Artwork inspired by your journal entries will appear here."
        }
}

internal fun filterArtworks(
    source: List<GalleryArtwork>,
    timeFilter: GalleryTimeFilter,
    moodFilter: GalleryMood?,
    searchQuery: String,
    now: LocalDateTime = LocalDateTime.now(),
): List<GalleryArtwork> {
    val dateThreshold = when (timeFilter) {
        GalleryTimeFilter.ALL -> null
        GalleryTimeFilter.WEEK -> now.minusDays(7)
        GalleryTimeFilter.MONTH -> now.minusMonths(1)
        GalleryTimeFilter.YEAR -> now.minusYears(1)
    }

    val query = searchQuery.trim()
    return source
        .asSequence()
        .filter { artwork -> dateThreshold == null || artwork.entryDate >= dateThreshold }
        .filter { artwork -> moodFilter == null || artwork.mood == moodFilter }
        .filter { artwork ->
            query.isEmpty() || artwork.entryTitle.contains(query, ignoreCase = true)
        }
        .sortedByDescending { it.entryDate }
        .toList()
}

internal fun GalleryArtwork.placeholderColor(): Color = when (mood) {
    GalleryMood.GREAT -> Color(0xFFFFE4D6)
    GalleryMood.GOOD -> Color(0xFFE8F5E9)
    GalleryMood.OKAY -> Color(0xFFFFF8E1)
    GalleryMood.LOW -> Color(0xFFE3F2FD)
    GalleryMood.BAD -> Color(0xFFF3E5F5)
    null -> Color(0xFFF5F5F5)
}

private val SHORT_DATE = DateTimeFormatter.ofPattern("MMM d", Locale.US)
private val TIME_ONLY = DateTimeFormatter.ofPattern("h:mm a", Locale.US)

internal fun GalleryArtwork.formattedDateLabel(today: LocalDate = LocalDate.now()): String {
    val date = entryDate.toLocalDate()
    return when {
        date == today -> "Today"
        date == today.minusDays(1) -> "Yesterday"
        else -> date.format(SHORT_DATE)
    }
}

internal fun GalleryArtwork.formattedDateTime(): String {
    return entryDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a", Locale.US))
}

internal fun GalleryArtwork.timeOfDayLabel(): String = entryDate.format(TIME_ONLY)
