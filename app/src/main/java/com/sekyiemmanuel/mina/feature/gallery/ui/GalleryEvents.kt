package com.sekyiemmanuel.mina.feature.gallery.ui

import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryArtwork
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryMood

sealed interface GalleryUiEvent {
    data object OnAppear : GalleryUiEvent
    data object Refresh : GalleryUiEvent
    data class SearchQueryChanged(val query: String) : GalleryUiEvent
    data class TimeFilterChanged(val filter: GalleryTimeFilter) : GalleryUiEvent
    data class MoodFilterChanged(val mood: GalleryMood?) : GalleryUiEvent
    data object ClearFilters : GalleryUiEvent
    data object ToggleFiltersSheet : GalleryUiEvent
    data object DismissFiltersSheet : GalleryUiEvent
    data class ArtworkTapped(val artwork: GalleryArtwork) : GalleryUiEvent
    data object DismissArtworkDetail : GalleryUiEvent
    data object ShareSelectedArtwork : GalleryUiEvent
    data object SaveSelectedArtwork : GalleryUiEvent
    data object RegenerateSelectedArtwork : GalleryUiEvent
    data class DeleteArtwork(val artworkId: String) : GalleryUiEvent
}
