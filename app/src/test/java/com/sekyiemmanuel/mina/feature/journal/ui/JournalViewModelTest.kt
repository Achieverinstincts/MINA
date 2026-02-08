package com.sekyiemmanuel.mina.feature.journal.ui

import com.sekyiemmanuel.mina.core.model.DailyStreakMetric
import com.sekyiemmanuel.mina.feature.journal.domain.JournalRepository
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
        assertEquals("Start logging your meals...", state.emptyStateMessage)
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
    fun settingsClick_emitsNavigateToSettingsEvent() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = JournalViewModel(FakeJournalRepository())

        val event = async { viewModel.navEvents.first() }
        viewModel.onEvent(JournalUiEvent.SettingsClicked)

        assertEquals(JournalNavEvent.NavigateToSettings, event.await())
    }
}

private class FakeJournalRepository : JournalRepository {
    override suspend fun getDailyStreakMetric(date: LocalDate): DailyStreakMetric {
        return DailyStreakMetric(date = date, streak = 0)
    }
}

