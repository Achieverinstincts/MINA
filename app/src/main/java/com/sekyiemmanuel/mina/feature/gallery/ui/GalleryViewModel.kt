package com.sekyiemmanuel.mina.feature.gallery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: GalleryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState(isLoading = true))
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    private var hasLoadedOnce = false

    fun onEvent(event: GalleryUiEvent) {
        when (event) {
            GalleryUiEvent.OnAppear -> onAppear()
            GalleryUiEvent.Refresh -> loadArtworks()
            is GalleryUiEvent.SearchQueryChanged -> _uiState.update { it.copy(searchQuery = event.query) }
            is GalleryUiEvent.TimeFilterChanged -> _uiState.update { it.copy(timeFilter = event.filter) }
            is GalleryUiEvent.MoodFilterChanged -> _uiState.update { it.copy(moodFilter = event.mood) }
            GalleryUiEvent.ClearFilters -> {
                _uiState.update {
                    it.copy(
                        timeFilter = GalleryTimeFilter.ALL,
                        moodFilter = null,
                        searchQuery = "",
                    )
                }
            }
            GalleryUiEvent.ToggleFiltersSheet -> {
                _uiState.update { it.copy(isShowingFilters = !it.isShowingFilters) }
            }
            GalleryUiEvent.DismissFiltersSheet -> _uiState.update { it.copy(isShowingFilters = false) }
            is GalleryUiEvent.ArtworkTapped -> _uiState.update { it.copy(selectedArtwork = event.artwork) }
            GalleryUiEvent.DismissArtworkDetail -> _uiState.update { it.copy(selectedArtwork = null) }
            GalleryUiEvent.ShareSelectedArtwork -> Unit
            GalleryUiEvent.SaveSelectedArtwork -> Unit
            GalleryUiEvent.RegenerateSelectedArtwork -> Unit
            is GalleryUiEvent.DeleteArtwork -> {
                _uiState.update { current ->
                    current.copy(
                        artworks = current.artworks.filterNot { it.id == event.artworkId },
                        selectedArtwork = current.selectedArtwork?.takeIf { it.id != event.artworkId },
                    )
                }
            }
        }
    }

    private fun onAppear() {
        if (hasLoadedOnce) {
            return
        }
        hasLoadedOnce = true
        loadArtworks()
    }

    private fun loadArtworks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val artworks = repository.getArtworks()
                .sortedByDescending { it.entryDate }
            _uiState.update {
                it.copy(
                    artworks = artworks,
                    isLoading = false,
                )
            }
        }
    }
}
