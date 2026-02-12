package com.sekyiemmanuel.mina.feature.insight.ui

import com.sekyiemmanuel.mina.feature.insight.domain.AIAnalysis
import com.sekyiemmanuel.mina.feature.insight.domain.BehaviourPatterns
import com.sekyiemmanuel.mina.feature.insight.domain.InsightPeriod
import com.sekyiemmanuel.mina.feature.insight.domain.InsightRepository
import com.sekyiemmanuel.mina.feature.insight.domain.InsightSnapshot
import com.sekyiemmanuel.mina.feature.insight.domain.JournalStats
import com.sekyiemmanuel.mina.feature.insight.domain.MoodDataPoint
import com.sekyiemmanuel.mina.feature.insight.domain.MoodDistribution
import com.sekyiemmanuel.mina.feature.insight.domain.MoodLevel
import com.sekyiemmanuel.mina.feature.insight.domain.StreakInfo
import com.sekyiemmanuel.mina.feature.insight.domain.TopicStat
import com.sekyiemmanuel.mina.feature.insight.domain.TrendDirection
import com.sekyiemmanuel.mina.feature.insight.domain.WritingActivityPoint
import com.sekyiemmanuel.mina.feature.journal.ui.MainDispatcherRule
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InsightViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onAppear_loadsInsightsForDefaultPeriod() = runTest {
        val viewModel = InsightViewModel(FakeInsightRepository())

        viewModel.onEvent(InsightUiEvent.OnAppear)
        advanceUntilIdle()

        assertEquals(InsightPeriod.MONTH, viewModel.uiState.value.selectedPeriod)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.hasLoadedOnce)
        assertEquals(30, viewModel.uiState.value.writingActivity.size)
    }

    @Test
    fun periodChanged_switchesDataset() = runTest {
        val viewModel = InsightViewModel(FakeInsightRepository())

        viewModel.onEvent(InsightUiEvent.PeriodChanged(InsightPeriod.WEEK))
        advanceUntilIdle()

        assertEquals(InsightPeriod.WEEK, viewModel.uiState.value.selectedPeriod)
        assertEquals(7, viewModel.uiState.value.writingActivity.size)
        assertEquals(7, viewModel.uiState.value.stats.entriesThisPeriod)
    }

    @Test
    fun generateAnalysis_setsAnalysisState() = runTest {
        val viewModel = InsightViewModel(FakeInsightRepository())
        viewModel.onEvent(InsightUiEvent.OnAppear)
        advanceUntilIdle()

        viewModel.onEvent(InsightUiEvent.GenerateAnalysis)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.aiAnalysis)
        assertFalse(viewModel.uiState.value.isGeneratingAnalysis)
    }
}

private class FakeInsightRepository : InsightRepository {
    override suspend fun getInsights(period: InsightPeriod): InsightSnapshot {
        val today = LocalDate.of(2026, 2, 12)
        val days = period.dayCount
        return InsightSnapshot(
            period = period,
            moodData = List(days) { index ->
                MoodDataPoint(
                    date = today.minusDays((days - index).toLong()),
                    value = 3.5,
                    mood = MoodLevel.GOOD,
                )
            },
            moodDistribution = listOf(
                MoodDistribution(mood = MoodLevel.GOOD, count = days, percentage = 1.0),
            ),
            stats = JournalStats(
                totalEntries = 200,
                totalWords = 25000,
                averageWordsPerEntry = 125,
                entriesThisPeriod = days,
                longestEntry = 500,
                shortestEntry = 80,
                entriesWithMood = days,
            ),
            streakInfo = StreakInfo(
                currentStreak = 6,
                longestStreak = 20,
                totalDaysJournaled = 42,
                lastEntryDate = today,
            ),
            topTopics = listOf(
                TopicStat(topic = "Work", count = 10, percentage = 0.5),
                TopicStat(topic = "Health", count = 6, percentage = 0.3),
            ),
            writingActivity = List(days) { index ->
                WritingActivityPoint(
                    date = today.minusDays((days - index).toLong()),
                    entryCount = 1,
                    wordCount = 130,
                )
            },
            behaviourPatterns = BehaviourPatterns(
                mostProductiveDay = "Tue",
                mostProductiveTime = "Evening",
                averageEntriesPerDay = 1.0,
                bestMoodDay = "Sat",
                worstMoodDay = "Mon",
                dayOfWeekDistribution = listOf(1, 2, 1, 1, 1, 0, 1),
                timeOfDayDistribution = listOf(1, 1, 4, 1),
            ),
        )
    }

    override suspend fun generateAnalysis(
        snapshot: InsightSnapshot,
        trendDirection: TrendDirection,
    ): AIAnalysis {
        return AIAnalysis(
            summary = "Steady journaling rhythm and consistent mood tracking.",
            moodInsight = "Mood remains stable with slight positive drift.",
            patterns = listOf("Evening writing pattern"),
            suggestions = listOf("Keep the same writing time"),
            generatedAt = LocalDateTime.of(2026, 2, 12, 10, 0),
        )
    }
}
