package com.sekyiemmanuel.mina.feature.insight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekyiemmanuel.mina.feature.insight.domain.InsightPeriod
import com.sekyiemmanuel.mina.feature.insight.domain.InsightRepository
import com.sekyiemmanuel.mina.feature.insight.domain.InsightSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class InsightViewModel @Inject constructor(
    private val repository: InsightRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightUiState(isLoading = true))
    val uiState: StateFlow<InsightUiState> = _uiState.asStateFlow()

    private var hasLoadedOnce = false
    private var latestSnapshot: InsightSnapshot? = null

    fun onEvent(event: InsightUiEvent) {
        when (event) {
            InsightUiEvent.OnAppear -> onAppear()
            InsightUiEvent.Refresh -> loadInsights(uiState.value.selectedPeriod)
            is InsightUiEvent.PeriodChanged -> loadInsights(event.period)
            InsightUiEvent.GenerateAnalysis -> generateAnalysis()
        }
    }

    private fun onAppear() {
        if (hasLoadedOnce) {
            return
        }
        hasLoadedOnce = true
        loadInsights(uiState.value.selectedPeriod)
    }

    private fun loadInsights(period: InsightPeriod) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    selectedPeriod = period,
                    isLoading = true,
                    errorMessage = null,
                )
            }
            runCatching {
                repository.getInsights(period)
            }.onSuccess { snapshot ->
                latestSnapshot = snapshot
                _uiState.update {
                    it.copy(
                        selectedPeriod = snapshot.period,
                        isLoading = false,
                        hasLoadedOnce = true,
                        moodData = snapshot.moodData,
                        moodDistribution = snapshot.moodDistribution,
                        stats = snapshot.stats,
                        streakInfo = snapshot.streakInfo,
                        topTopics = snapshot.topTopics,
                        writingActivity = snapshot.writingActivity,
                        behaviourPatterns = snapshot.behaviourPatterns,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasLoadedOnce = true,
                        errorMessage = throwable.message ?: "Unable to load insights.",
                    )
                }
            }
        }
    }

    private fun generateAnalysis() {
        val snapshot = latestSnapshot ?: return
        if (uiState.value.isGeneratingAnalysis) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingAnalysis = true, analysisError = null) }
            runCatching {
                repository.generateAnalysis(
                    snapshot = snapshot,
                    trendDirection = uiState.value.moodTrendDirection,
                )
            }.onSuccess { analysis ->
                _uiState.update {
                    it.copy(
                        isGeneratingAnalysis = false,
                        aiAnalysis = analysis,
                        analysisError = null,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isGeneratingAnalysis = false,
                        analysisError = throwable.message ?: "Could not generate analysis.",
                    )
                }
            }
        }
    }
}
