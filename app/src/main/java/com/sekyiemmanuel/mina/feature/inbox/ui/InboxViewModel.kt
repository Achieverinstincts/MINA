package com.sekyiemmanuel.mina.feature.inbox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItem
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItemType
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val repository: InboxRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InboxUiState(isLoading = true))
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    private var hasLoadedOnce = false
    private var recordingJob: Job? = null

    fun onEvent(event: InboxUiEvent) {
        when (event) {
            InboxUiEvent.OnAppear -> onAppear()
            InboxUiEvent.Refresh -> loadItems()
            is InboxUiEvent.FilterChanged -> _uiState.update { it.copy(filter = event.filter) }
            is InboxUiEvent.ItemTapped -> _uiState.update { it.copy(selectedItem = event.item) }
            InboxUiEvent.DismissItemDetail -> _uiState.update { it.copy(selectedItem = null) }
            InboxUiEvent.ShowCaptureOptions -> _uiState.update { it.copy(isShowingCaptureOptions = true) }
            InboxUiEvent.HideCaptureOptions -> _uiState.update { it.copy(isShowingCaptureOptions = false) }
            InboxUiEvent.StartVoiceRecording -> startVoiceRecording()
            InboxUiEvent.StopVoiceRecording -> stopVoiceRecording()
            InboxUiEvent.CapturePhoto -> capturePhoto()
            InboxUiEvent.ScanDocument -> scanDocument()
            is InboxUiEvent.ConvertToEntry -> markAsProcessed(event.itemId)
            is InboxUiEvent.ArchiveItem -> archive(event.itemId)
            is InboxUiEvent.UnarchiveItem -> unarchive(event.itemId)
            is InboxUiEvent.DeleteItem -> delete(event.itemId)
        }
    }

    private fun onAppear() {
        if (hasLoadedOnce) return
        hasLoadedOnce = true
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val items = repository.getItems().sortedByDescending { it.createdAt }
            _uiState.update { current ->
                current.copy(
                    items = items,
                    isLoading = false,
                )
            }
        }
    }

    private fun startVoiceRecording() {
        recordingJob?.cancel()
        _uiState.update {
            it.copy(
                isRecording = true,
                recordingDurationSeconds = 0,
                isShowingCaptureOptions = false,
            )
        }
        recordingJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                _uiState.update { current ->
                    if (!current.isRecording) current
                    else current.copy(recordingDurationSeconds = current.recordingDurationSeconds + 1)
                }
            }
        }
    }

    private fun stopVoiceRecording() {
        recordingJob?.cancel()
        recordingJob = null
        _uiState.update { it.copy(isRecording = false) }
        prependInboxItem(
            type = InboxItemType.VOICE_NOTE,
            transcription = "Voice note captured and ready to review.",
            preview = null,
        )
    }

    private fun capturePhoto() {
        _uiState.update { it.copy(isShowingCaptureOptions = false) }
        prependInboxItem(
            type = InboxItemType.PHOTO,
            transcription = null,
            preview = "New captured photo",
        )
    }

    private fun scanDocument() {
        _uiState.update { it.copy(isShowingCaptureOptions = false) }
        prependInboxItem(
            type = InboxItemType.SCAN,
            transcription = "Scanned text recognized and ready for your journal.",
            preview = null,
        )
    }

    private fun prependInboxItem(
        type: InboxItemType,
        transcription: String?,
        preview: String?,
    ) {
        val newItem = InboxItem(
            id = "inbox-${UUID.randomUUID()}",
            type = type,
            transcription = transcription,
            previewText = preview,
            createdAt = LocalDateTime.now(),
            isProcessed = false,
            isArchived = false,
        )
        _uiState.update { current ->
            current.copy(items = listOf(newItem) + current.items)
        }
    }

    private fun markAsProcessed(itemId: String) {
        _uiState.update { current ->
            current.copy(
                items = current.items.map { item ->
                    if (item.id == itemId) {
                        item.copy(
                            isProcessed = true,
                            processedEntryId = item.processedEntryId ?: "entry-${UUID.randomUUID()}",
                        )
                    } else item
                },
                selectedItem = current.selectedItem?.let { selected ->
                    if (selected.id == itemId) {
                        selected.copy(
                            isProcessed = true,
                            processedEntryId = selected.processedEntryId ?: "entry-${UUID.randomUUID()}",
                        )
                    } else selected
                },
            )
        }
    }

    private fun archive(itemId: String) {
        _uiState.update { current ->
            current.copy(
                items = current.items.map { item ->
                    if (item.id == itemId) item.copy(isArchived = true) else item
                },
                selectedItem = null,
            )
        }
    }

    private fun unarchive(itemId: String) {
        _uiState.update { current ->
            current.copy(
                items = current.items.map { item ->
                    if (item.id == itemId) item.copy(isArchived = false) else item
                },
            )
        }
    }

    private fun delete(itemId: String) {
        _uiState.update { current ->
            current.copy(
                items = current.items.filterNot { it.id == itemId },
                selectedItem = current.selectedItem?.takeIf { it.id != itemId },
            )
        }
    }

    override fun onCleared() {
        recordingJob?.cancel()
        super.onCleared()
    }
}
