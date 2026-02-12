package com.sekyiemmanuel.mina.feature.insight.ui

import androidx.compose.ui.graphics.Color
import com.sekyiemmanuel.mina.feature.insight.domain.AIAnalysis
import com.sekyiemmanuel.mina.feature.insight.domain.BehaviourPatterns
import com.sekyiemmanuel.mina.feature.insight.domain.InsightPeriod
import com.sekyiemmanuel.mina.feature.insight.domain.JournalStats
import com.sekyiemmanuel.mina.feature.insight.domain.MoodDataPoint
import com.sekyiemmanuel.mina.feature.insight.domain.MoodDistribution
import com.sekyiemmanuel.mina.feature.insight.domain.StreakInfo
import com.sekyiemmanuel.mina.feature.insight.domain.TopicStat
import com.sekyiemmanuel.mina.feature.insight.domain.TrendDirection
import com.sekyiemmanuel.mina.feature.insight.domain.WritingActivityPoint

data class InsightUiState(
    val selectedPeriod: InsightPeriod = InsightPeriod.MONTH,
    val isLoading: Boolean = false,
    val hasLoadedOnce: Boolean = false,
    val errorMessage: String? = null,
    val moodData: List<MoodDataPoint> = emptyList(),
    val moodDistribution: List<MoodDistribution> = emptyList(),
    val stats: JournalStats = JournalStats(),
    val streakInfo: StreakInfo = StreakInfo(),
    val topTopics: List<TopicStat> = emptyList(),
    val writingActivity: List<WritingActivityPoint> = emptyList(),
    val behaviourPatterns: BehaviourPatterns = BehaviourPatterns(),
    val aiAnalysis: AIAnalysis? = null,
    val isGeneratingAnalysis: Boolean = false,
    val analysisError: String? = null,
) {
    val averageMood: Double
        get() = if (moodData.isNotEmpty()) moodData.map { it.value }.average() else 0.0

    val moodTrend: String
        get() = when {
            averageMood >= 4.5 -> "Excellent"
            averageMood >= 3.5 -> "Good"
            averageMood >= 2.5 -> "Neutral"
            averageMood >= 1.5 -> "Low"
            averageMood > 0 -> "Challenging"
            else -> "No Data"
        }

    val moodTrendDirection: TrendDirection
        get() {
            if (moodData.size < 4) {
                return TrendDirection.STABLE
            }
            val midpoint = moodData.size / 2
            val firstHalf = moodData.take(midpoint)
            val secondHalf = moodData.drop(midpoint)
            val firstAverage = firstHalf.map { it.value }.average()
            val secondAverage = secondHalf.map { it.value }.average()
            val delta = secondAverage - firstAverage
            return when {
                delta > 0.25 -> TrendDirection.IMPROVING
                delta < -0.25 -> TrendDirection.DECLINING
                else -> TrendDirection.STABLE
            }
        }

    val trendColor: Color
        get() = when (moodTrendDirection) {
            TrendDirection.IMPROVING -> Color(0xFF3CB371)
            TrendDirection.DECLINING -> Color(0xFFE45555)
            TrendDirection.STABLE -> Color(0xFFE3A543)
        }
}
