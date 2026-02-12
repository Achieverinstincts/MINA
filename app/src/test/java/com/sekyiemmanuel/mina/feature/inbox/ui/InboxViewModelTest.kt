package com.sekyiemmanuel.mina.feature.inbox.ui

import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItem
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItemType
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxRepository
import com.sekyiemmanuel.mina.feature.journal.ui.MainDispatcherRule
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InboxViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onAppear_loadsItems() = runTest {
        val viewModel = InboxViewModel(FakeInboxRepository())

        viewModel.onEvent(InboxUiEvent.OnAppear)
        advanceUntilIdle()

        assertEquals(4, viewModel.uiState.value.items.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun filterByVoice_returnsOnlyVoiceNotes() = runTest {
        val state = InboxUiState(
            items = FakeInboxRepository.sampleItems(),
            filter = InboxFilter.VOICE_NOTES,
        )

        assertEquals(2, state.filteredItems.size)
        assertTrue(state.filteredItems.all { it.type == InboxItemType.VOICE_NOTE })
    }

    @Test
    fun groupItemsByDate_placesTodayFirst() {
        val today = LocalDate.of(2026, 2, 12)
        val grouped = groupItemsByDate(
            items = FakeInboxRepository.sampleItems(),
            today = today,
        )

        assertEquals("Today", grouped.first().title)
    }

    @Test
    fun convertToEntry_marksItemProcessed() = runTest {
        val viewModel = InboxViewModel(FakeInboxRepository())
        viewModel.onEvent(InboxUiEvent.OnAppear)
        advanceUntilIdle()

        val targetId = viewModel.uiState.value.items.first().id
        viewModel.onEvent(InboxUiEvent.ConvertToEntry(targetId))

        val updated = viewModel.uiState.value.items.first { it.id == targetId }
        assertTrue(updated.isProcessed)
        assertTrue(!updated.processedEntryId.isNullOrBlank())
    }
}

private class FakeInboxRepository : InboxRepository {
    override suspend fun getItems(): List<InboxItem> = sampleItems()

    companion object {
        fun sampleItems(): List<InboxItem> {
            return listOf(
                InboxItem(
                    id = "1",
                    type = InboxItemType.VOICE_NOTE,
                    transcription = "Voice reflection",
                    createdAt = LocalDateTime.of(2026, 2, 12, 9, 0),
                ),
                InboxItem(
                    id = "2",
                    type = InboxItemType.PHOTO,
                    previewText = "Photo note",
                    createdAt = LocalDateTime.of(2026, 2, 12, 7, 0),
                ),
                InboxItem(
                    id = "3",
                    type = InboxItemType.SCAN,
                    transcription = "Scanned memo",
                    createdAt = LocalDateTime.of(2026, 2, 11, 22, 0),
                ),
                InboxItem(
                    id = "4",
                    type = InboxItemType.VOICE_NOTE,
                    transcription = "Older voice note",
                    createdAt = LocalDateTime.of(2026, 1, 12, 10, 0),
                ),
            )
        }
    }
}
