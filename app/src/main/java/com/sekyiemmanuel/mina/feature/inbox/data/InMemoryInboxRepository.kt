package com.sekyiemmanuel.mina.feature.inbox.data

import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItem
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItemType
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class InMemoryInboxRepository @Inject constructor() : InboxRepository {
    override suspend fun getItems(): List<InboxItem> {
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        return listOf(
            InboxItem(
                id = "inbox-001",
                type = InboxItemType.VOICE_NOTE,
                transcription = "Need to remember to call mom tomorrow and pick up groceries after work.",
                createdAt = now.minusMinutes(10),
                isProcessed = false,
                isArchived = false,
            ),
            InboxItem(
                id = "inbox-002",
                type = InboxItemType.PHOTO,
                previewText = "Photo from the coffee shop corner table.",
                createdAt = now.minusHours(2),
                isProcessed = false,
                isArchived = false,
            ),
            InboxItem(
                id = "inbox-003",
                type = InboxItemType.SCAN,
                transcription = "Meeting notes: Q4 goals review, budget alignment, and hiring milestones.",
                createdAt = now.minusDays(1).plusHours(1),
                isProcessed = false,
                isArchived = false,
            ),
            InboxItem(
                id = "inbox-004",
                type = InboxItemType.VOICE_NOTE,
                transcription = "Idea: add a widget that shows today mood trend and streak summary.",
                createdAt = now.minusDays(1).minusHours(3),
                isProcessed = true,
                isArchived = false,
                processedEntryId = "entry-voice-004",
            ),
            InboxItem(
                id = "inbox-005",
                type = InboxItemType.FILE,
                previewText = "Quick capture from a PDF highlight.",
                createdAt = now.minusDays(5),
                isProcessed = false,
                isArchived = true,
            ),
        )
    }
}
