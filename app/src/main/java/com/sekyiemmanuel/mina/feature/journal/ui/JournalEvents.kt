package com.sekyiemmanuel.mina.feature.journal.ui

import java.time.LocalDate

sealed interface JournalUiEvent {
    data object DateClicked : JournalUiEvent
    data class DateSelected(val date: LocalDate) : JournalUiEvent
    data object SettingsClicked : JournalUiEvent
}

sealed interface JournalNavEvent {
    data object ShowDatePicker : JournalNavEvent
    data object NavigateToSettings : JournalNavEvent
}

