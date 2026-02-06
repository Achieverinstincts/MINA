package com.sekyiemmanuel.mina.core.model

import java.time.LocalDate

data class DailyStreakMetric(
    val date: LocalDate,
    val streak: Int,
)

