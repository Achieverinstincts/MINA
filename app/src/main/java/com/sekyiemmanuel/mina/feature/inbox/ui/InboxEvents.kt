package com.sekyiemmanuel.mina.feature.inbox.ui

import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItem

sealed interface InboxUiEvent {
    data object OnAppear : InboxUiEvent
    data object Refresh : InboxUiEvent
    data class FilterChanged(val filter: InboxFilter) : InboxUiEvent
    data class ItemTapped(val item: InboxItem) : InboxUiEvent
    data object DismissItemDetail : InboxUiEvent
    data object ShowCaptureOptions : InboxUiEvent
    data object HideCaptureOptions : InboxUiEvent
    data object StartVoiceRecording : InboxUiEvent
    data object StopVoiceRecording : InboxUiEvent
    data object CapturePhoto : InboxUiEvent
    data object ScanDocument : InboxUiEvent
    data class ConvertToEntry(val itemId: String) : InboxUiEvent
    data class ArchiveItem(val itemId: String) : InboxUiEvent
    data class UnarchiveItem(val itemId: String) : InboxUiEvent
    data class DeleteItem(val itemId: String) : InboxUiEvent
}
