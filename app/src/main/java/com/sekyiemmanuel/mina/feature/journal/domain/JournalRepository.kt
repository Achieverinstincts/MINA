package com.sekyiemmanuel.mina.feature.journal.domain

import com.sekyiemmanuel.mina.core.model.DailyStreakMetric
import java.time.LocalDate

interface JournalRepository {
    suspend fun getDailyStreakMetric(date: LocalDate): DailyStreakMetric
}

