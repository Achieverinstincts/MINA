package com.sekyiemmanuel.mina.feature.insight.domain

import java.time.LocalDate
import java.time.LocalDateTime

enum class InsightPeriod(
    val label: String,
    val fullLabel: String,
    val dayCount: Int,
) {
    WEEK(label = "7D", fullLabel = "Past 7 Days", dayCount = 7),
    MONTH(label = "30D", fullLabel = "Past 30 Days", dayCount = 30),
    THREE_MONTHS(label = "90D", fullLabel = "Past 90 Days", dayCount = 90),
    YEAR(label = "1Y", fullLabel = "Past Year", dayCount = 365),
}

enum class TrendDirection {
    IMPROVING,
    DECLINING,
    STABLE,
}

enum class MoodLevel(
    val label: String,
    val numericValue: Double,
) {
    GREAT(label = "Great", numericValue = 5.0),
    GOOD(label = "Good", numericValue = 4.0),
    OKAY(label = "Okay", numericValue = 3.0),
    LOW(label = "Low", numericValue = 2.0),
    BAD(label = "Bad", numericValue = 1.0),
}

data class MoodDataPoint(
    val date: LocalDate,
    val value: Double,
    val mood: MoodLevel,
)

data class MoodDistribution(
    val mood: MoodLevel,
    val count: Int,
    val percentage: Double,
)

data class WritingActivityPoint(
    val date: LocalDate,
    val entryCount: Int,
    val wordCount: Int,
)

data class JournalStats(
    val totalEntries: Int = 0,
    val totalWords: Int = 0,
    val averageWordsPerEntry: Int = 0,
    val entriesThisPeriod: Int = 0,
    val longestEntry: Int = 0,
    val shortestEntry: Int = 0,
    val entriesWithMood: Int = 0,
)

data class TopicStat(
    val topic: String,
    val count: Int,
    val percentage: Double,
)

data class StreakInfo(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalDaysJournaled: Int = 0,
    val lastEntryDate: LocalDate? = null,
)

data class BehaviourPatterns(
    val mostProductiveDay: String = "-",
    val mostProductiveTime: String = "-",
    val averageEntriesPerDay: Double = 0.0,
    val bestMoodDay: String = "-",
    val worstMoodDay: String = "-",
    val dayOfWeekDistribution: List<Int> = List(7) { 0 },
    val timeOfDayDistribution: List<Int> = List(4) { 0 },
)

data class AIAnalysis(
    val summary: String,
    val moodInsight: String,
    val patterns: List<String>,
    val suggestions: List<String>,
    val generatedAt: LocalDateTime,
)

data class InsightSnapshot(
    val period: InsightPeriod,
    val moodData: List<MoodDataPoint>,
    val moodDistribution: List<MoodDistribution>,
    val stats: JournalStats,
    val streakInfo: StreakInfo,
    val topTopics: List<TopicStat>,
    val writingActivity: List<WritingActivityPoint>,
    val behaviourPatterns: BehaviourPatterns,
)

interface InsightRepository {
    suspend fun getInsights(period: InsightPeriod): InsightSnapshot

    suspend fun generateAnalysis(snapshot: InsightSnapshot, trendDirection: TrendDirection): AIAnalysis
}
