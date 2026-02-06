package com.sekyiemmanuel.mina.feature.journal.ui

import com.sekyiemmanuel.mina.core.model.DailyStreakMetric
import com.sekyiemmanuel.mina.feature.journal.domain.JournalRepository
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialState_hasTodayLabel_zeroStreak_andCustomEmptyMessage() = runTest {
        val viewModel = JournalViewModel(FakeJournalRepository())

        val state = viewModel.uiState.value

        assertEquals("Today", state.dateLabel)
        assertEquals(0, state.streakCount)
        assertEquals("Upload your mind...", state.emptyStateMessage)
    }

    @Test
    fun selectingNonCurrentDate_updatesDateLabel() = runTest {
        val viewModel = JournalViewModel(FakeJournalRepository())
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        viewModel.onEvent(JournalUiEvent.DateSelected(yesterday))
        advanceUntilIdle()

        assertEquals(
            JournalViewModel.formatDateLabel(yesterday, today),
            viewModel.uiState.value.dateLabel,
        )
    }

    @Test
    fun selectingToday_afterOtherDate_resetsDateLabelToToday() = runTest {
        val viewModel = JournalViewModel(FakeJournalRepository())
        val today = LocalDate.now()

        viewModel.onEvent(JournalUiEvent.DateSelected(today.minusDays(1)))
        viewModel.onEvent(JournalUiEvent.DateSelected(today))
        advanceUntilIdle()

        assertEquals("Today", viewModel.uiState.value.dateLabel)
    }

    @Test
    fun settingsClick_emitsNavigateToSettingsEvent() = runTest {
        val viewModel = JournalViewModel(FakeJournalRepository())

        viewModel.onEvent(JournalUiEvent.SettingsClicked)
        val event = viewModel.navEvents.first()

        assertEquals(JournalNavEvent.NavigateToSettings, event)
    }
}

private class FakeJournalRepository : JournalRepository {
    override suspend fun getDailyStreakMetric(date: LocalDate): DailyStreakMetric {
        return DailyStreakMetric(date = date, streak = 0)
    }
}

