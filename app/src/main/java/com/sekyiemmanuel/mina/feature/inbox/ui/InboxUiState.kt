package com.sekyiemmanuel.mina.feature.inbox.ui

import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItem
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItemType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class InboxFilter(
    val label: String,
) {
    ALL("All"),
    VOICE_NOTES("Voice"),
    PHOTOS("Photos"),
    SCANS("Scans"),
    ARCHIVED("Archived"),
}

data class InboxDateSection(
    val title: String,
    val items: List<InboxItem>,
)

data class InboxUiState(
    val items: List<InboxItem> = emptyList(),
    val isLoading: Boolean = false,
    val filter: InboxFilter = InboxFilter.ALL,
    val selectedItem: InboxItem? = null,
    val isRecording: Boolean = false,
    val recordingDurationSeconds: Int = 0,
    val isShowingCaptureOptions: Boolean = false,
) {
    val filteredItems: List<InboxItem>
        get() = filterItems(items = items, filter = filter)

    val unprocessedCount: Int
        get() = items.count { !it.isProcessed && !it.isArchived }

    val groupedItems: List<InboxDateSection>
        get() = groupItemsByDate(filteredItems)

    val emptyStateTitle: String
        get() = when (filter) {
            InboxFilter.ALL -> "Inbox Empty"
            InboxFilter.VOICE_NOTES -> "No Voice Notes"
            InboxFilter.PHOTOS -> "No Photos"
            InboxFilter.SCANS -> "No Scans"
            InboxFilter.ARCHIVED -> "No Archived Items"
        }

    val emptyStateMessage: String
        get() = when (filter) {
            InboxFilter.ALL -> "Tap Quick Capture to save voice notes, photos, or scans."
            InboxFilter.VOICE_NOTES -> "Voice recordings will appear here."
            InboxFilter.PHOTOS -> "Captured photos will appear here."
            InboxFilter.SCANS -> "Scanned documents will appear here."
            InboxFilter.ARCHIVED -> "Archived items will appear here."
        }
}

internal fun filterItems(
    items: List<InboxItem>,
    filter: InboxFilter,
): List<InboxItem> {
    return items
        .asSequence()
        .filter { item ->
            when (filter) {
                InboxFilter.ALL -> !item.isArchived
                InboxFilter.VOICE_NOTES -> item.type == InboxItemType.VOICE_NOTE && !item.isArchived
                InboxFilter.PHOTOS -> item.type == InboxItemType.PHOTO && !item.isArchived
                InboxFilter.SCANS -> item.type == InboxItemType.SCAN && !item.isArchived
                InboxFilter.ARCHIVED -> item.isArchived
            }
        }
        .sortedByDescending { it.createdAt }
        .toList()
}

internal fun groupItemsByDate(
    items: List<InboxItem>,
    today: LocalDate = LocalDate.now(),
): List<InboxDateSection> {
    val grouped = items.groupBy { item ->
        val date = item.createdAt.toLocalDate()
        when {
            date == today -> "Today"
            date == today.minusDays(1) -> "Yesterday"
            else -> date.format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.US))
        }
    }

    return grouped
        .toList()
        .sortedWith(compareByDescending<Pair<String, List<InboxItem>>> { (_, sectionItems) ->
            sectionItems.maxOf { it.createdAt }
        })
        .map { (title, sectionItems) ->
            InboxDateSection(
                title = title,
                items = sectionItems.sortedByDescending { it.createdAt },
            )
        }
}

internal fun InboxUiState.countForFilter(filter: InboxFilter): Int {
    return filterItems(items = items, filter = filter).size
}

private val TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.US)

internal fun InboxItem.displayPreview(): String {
    val explicitPreview = previewText?.trim()
    if (!explicitPreview.isNullOrEmpty()) {
        return explicitPreview
    }
    val transcript = transcription?.trim()
    if (!transcript.isNullOrEmpty()) {
        return if (transcript.length <= 100) transcript else transcript.take(100) + "..."
    }
    return "Tap to process"
}

internal fun InboxItem.formattedTime(): String = createdAt.format(TIME_FORMATTER)

internal fun InboxItem.statusLabel(): String {
    return when {
        isProcessed -> "Processed"
        transcription != null -> "Ready"
        else -> "Processing..."
    }
}

internal fun InboxItem.statusIndicatorKey(): String {
    return when {
        isProcessed -> "processed"
        transcription != null -> "ready"
        else -> "processing"
    }
}

internal fun formatRecordingDuration(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%d:%02d", mins, secs)
}

internal fun InboxItem.formattedFullDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.US)
    return createdAt.format(formatter)
}
