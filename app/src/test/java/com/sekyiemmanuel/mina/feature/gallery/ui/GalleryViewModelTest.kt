package com.sekyiemmanuel.mina.feature.gallery.ui

import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryArtwork
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryGenerationStatus
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryMood
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryRepository
import com.sekyiemmanuel.mina.feature.journal.ui.MainDispatcherRule
import java.time.LocalDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onAppear_loadsArtworks() = runTest {
        val viewModel = GalleryViewModel(FakeGalleryRepository())

        viewModel.onEvent(GalleryUiEvent.OnAppear)
        advanceUntilIdle()

        assertEquals(4, viewModel.uiState.value.artworks.size)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun filters_applyTimeMoodAndSearch() = runTest {
        val now = LocalDateTime.of(2026, 2, 10, 12, 0)
        val filtered = filterArtworks(
            source = FakeGalleryRepository.sampleData(),
            timeFilter = GalleryTimeFilter.WEEK,
            moodFilter = GalleryMood.GOOD,
            searchQuery = "gratitude",
            now = now,
        )

        assertEquals(1, filtered.size)
        assertEquals("Gratitude list", filtered.first().entryTitle)
    }

    @Test
    fun clearFilters_resetsFilterState() = runTest {
        val viewModel = GalleryViewModel(FakeGalleryRepository())
        viewModel.onEvent(GalleryUiEvent.TimeFilterChanged(GalleryTimeFilter.MONTH))
        viewModel.onEvent(GalleryUiEvent.MoodFilterChanged(GalleryMood.GREAT))
        viewModel.onEvent(GalleryUiEvent.SearchQueryChanged("focus"))

        viewModel.onEvent(GalleryUiEvent.ClearFilters)

        val state = viewModel.uiState.value
        assertEquals(GalleryTimeFilter.ALL, state.timeFilter)
        assertEquals(null, state.moodFilter)
        assertTrue(state.searchQuery.isEmpty())
    }
}

private class FakeGalleryRepository : GalleryRepository {
    override suspend fun getArtworks(): List<GalleryArtwork> = sampleData()

    companion object {
        fun sampleData(): List<GalleryArtwork> {
            return listOf(
                GalleryArtwork(
                    id = "1",
                    entryId = "1",
                    entryTitle = "Morning focus session",
                    entryDate = LocalDateTime.of(2026, 2, 10, 8, 0),
                    mood = GalleryMood.GREAT,
                    artStyle = "watercolor",
                    aspectRatio = 1.2f,
                    status = GalleryGenerationStatus.COMPLETED,
                ),
                GalleryArtwork(
                    id = "2",
                    entryId = "2",
                    entryTitle = "Gratitude list",
                    entryDate = LocalDateTime.of(2026, 2, 9, 18, 0),
                    mood = GalleryMood.GOOD,
                    artStyle = "dreamy",
                    aspectRatio = 1.3f,
                    status = GalleryGenerationStatus.COMPLETED,
                ),
                GalleryArtwork(
                    id = "3",
                    entryId = "3",
                    entryTitle = "Long-term reflection",
                    entryDate = LocalDateTime.of(2025, 11, 20, 20, 0),
                    mood = GalleryMood.GOOD,
                    artStyle = "abstract",
                    aspectRatio = 1.1f,
                    status = GalleryGenerationStatus.COMPLETED,
                ),
                GalleryArtwork(
                    id = "4",
                    entryId = "4",
                    entryTitle = "Processing stress",
                    entryDate = LocalDateTime.of(2026, 2, 8, 22, 0),
                    mood = GalleryMood.LOW,
                    artStyle = "minimalist",
                    aspectRatio = 1.0f,
                    status = GalleryGenerationStatus.GENERATING,
                ),
            )
        }
    }
}
