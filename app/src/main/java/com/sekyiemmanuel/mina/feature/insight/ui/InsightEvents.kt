package com.sekyiemmanuel.mina.feature.insight.ui

import com.sekyiemmanuel.mina.feature.insight.domain.InsightPeriod

sealed interface InsightUiEvent {
    data object OnAppear : InsightUiEvent
    data object Refresh : InsightUiEvent
    data class PeriodChanged(val period: InsightPeriod) : InsightUiEvent
    data object GenerateAnalysis : InsightUiEvent
}
