package com.sekyiemmanuel.mina.feature.inbox.domain

import java.time.LocalDateTime

enum class InboxItemType(
    val label: String,
) {
    VOICE_NOTE("Voice Note"),
    PHOTO("Photo"),
    SCAN("Scan"),
    FILE("File"),
}

data class InboxItem(
    val id: String,
    val type: InboxItemType,
    val transcription: String? = null,
    val previewText: String? = null,
    val createdAt: LocalDateTime,
    val isProcessed: Boolean = false,
    val isArchived: Boolean = false,
    val processedEntryId: String? = null,
)

interface InboxRepository {
    suspend fun getItems(): List<InboxItem>
}
