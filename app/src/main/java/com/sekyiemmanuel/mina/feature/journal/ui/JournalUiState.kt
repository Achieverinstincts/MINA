package com.sekyiemmanuel.mina.feature.journal.ui

import java.time.LocalDate

data class JournalUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val dateLabel: String = "Today",
    val streakCount: Int = 0,
    val emptyStateMessage: String = "Start logging your meals...",
)
