package com.sekyiemmanuel.mina.feature.journal.data

import com.sekyiemmanuel.mina.core.model.DailyStreakMetric
import com.sekyiemmanuel.mina.feature.journal.domain.JournalRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryJournalRepository @Inject constructor() : JournalRepository {
    private val streakByDate = mutableMapOf<LocalDate, Int>()

    override suspend fun getDailyStreakMetric(date: LocalDate): DailyStreakMetric {
        return DailyStreakMetric(
            date = date,
            streak = streakByDate[date] ?: 0,
        )
    }
}

