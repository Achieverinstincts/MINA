package com.sekyiemmanuel.mina.feature.journal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekyiemmanuel.mina.feature.journal.domain.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: JournalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        JournalUiState(
            selectedDate = LocalDate.now(),
        ),
    )
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<JournalNavEvent>(extraBufferCapacity = 1)
    val navEvents: SharedFlow<JournalNavEvent> = _navEvents.asSharedFlow()

    init {
        refreshDailyStreak(LocalDate.now())
    }

    fun onEvent(event: JournalUiEvent) {
        when (event) {
            JournalUiEvent.DateClicked -> emitNavEvent(JournalNavEvent.ShowDatePicker)
            is JournalUiEvent.DateSelected -> onDateSelected(event.date)
            JournalUiEvent.InboxClicked -> emitNavEvent(JournalNavEvent.NavigateToInbox)
            JournalUiEvent.GalleryClicked -> emitNavEvent(JournalNavEvent.NavigateToGallery)
            JournalUiEvent.SettingsClicked -> emitNavEvent(JournalNavEvent.NavigateToSettings)
        }
    }

    private fun onDateSelected(selectedDate: LocalDate) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedDate = selectedDate,
                dateLabel = formatDateLabel(selectedDate, LocalDate.now()),
            )
        }
        refreshDailyStreak(selectedDate)
    }

    private fun refreshDailyStreak(date: LocalDate) {
        viewModelScope.launch {
            val metric = repository.getDailyStreakMetric(date)
            _uiState.update { currentState ->
                currentState.copy(streakCount = metric.streak)
            }
        }
    }

    private fun emitNavEvent(event: JournalNavEvent) {
        viewModelScope.launch {
            _navEvents.emit(event)
        }
    }

    companion object {
        private val DATE_LABEL_FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)

        internal fun formatDateLabel(date: LocalDate, today: LocalDate): String {
            return if (date == today) {
                "Today"
            } else {
                date.format(DATE_LABEL_FORMATTER)
            }
        }
    }
}
