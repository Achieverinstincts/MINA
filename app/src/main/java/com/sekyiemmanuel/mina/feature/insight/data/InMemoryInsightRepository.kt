package com.sekyiemmanuel.mina.feature.insight.data

import com.sekyiemmanuel.mina.feature.insight.domain.AIAnalysis
import com.sekyiemmanuel.mina.feature.insight.domain.BehaviourPatterns
import com.sekyiemmanuel.mina.feature.insight.domain.InsightPeriod
import com.sekyiemmanuel.mina.feature.insight.domain.InsightRepository
import com.sekyiemmanuel.mina.feature.insight.domain.InsightSnapshot
import com.sekyiemmanuel.mina.feature.insight.domain.JournalStats
import com.sekyiemmanuel.mina.feature.insight.domain.MoodDataPoint
import com.sekyiemmanuel.mina.feature.insight.domain.MoodDistribution
import com.sekyiemmanuel.mina.feature.insight.domain.MoodLevel
import com.sekyiemmanuel.mina.feature.insight.domain.StreakInfo
import com.sekyiemmanuel.mina.feature.insight.domain.TopicStat
import com.sekyiemmanuel.mina.feature.insight.domain.TrendDirection
import com.sekyiemmanuel.mina.feature.insight.domain.WritingActivityPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.max

class InMemoryInsightRepository @Inject constructor() : InsightRepository {

    private data class SeedEntry(
        val id: String,
        val createdAt: LocalDateTime,
        val mood: MoodLevel?,
        val words: Int,
        val tags: List<String>,
    )

    override suspend fun getInsights(period: InsightPeriod): InsightSnapshot {
        val today = LocalDate.now()
        val entries = seedEntries(today)
        val startDate = today.minusDays(period.dayCount.toLong() - 1L)
        val periodEntries = entries.filter { entry ->
            val date = entry.createdAt.toLocalDate()
            !date.isBefore(startDate) && !date.isAfter(today)
        }
        return buildSnapshot(entries, periodEntries, period)
    }

    override suspend fun generateAnalysis(
        snapshot: InsightSnapshot,
        trendDirection: TrendDirection,
    ): AIAnalysis {
        val moodSummary = snapshot.moodDistribution.maxByOrNull { it.count }?.mood?.label ?: "untracked"
        val topTheme = snapshot.topTopics.firstOrNull()?.topic ?: "reflection"
        val trendLabel = when (trendDirection) {
            TrendDirection.IMPROVING -> "improving"
            TrendDirection.DECLINING -> "declining"
            TrendDirection.STABLE -> "stable"
        }

        val summary = buildString {
            append("You logged ${snapshot.stats.entriesThisPeriod} entries in ${snapshot.period.fullLabel.lowercase()}. ")
            append("Your writing pace is strongest on ${snapshot.behaviourPatterns.mostProductiveDay} and your streak is ${snapshot.streakInfo.currentStreak} days.")
        }
        val moodInsight = "Your dominant mood was $moodSummary with a $trendLabel trend, often around themes like $topTheme."
        val patterns = listOf(
            "Most writing happens in the ${snapshot.behaviourPatterns.mostProductiveTime.lowercase()}.",
            "You average ${"%.1f".format(snapshot.behaviourPatterns.averageEntriesPerDay)} entries per day in this window.",
            "Mood tends to be highest on ${snapshot.behaviourPatterns.bestMoodDay}.",
        )
        val suggestions = listOf(
            "Protect one short daily journaling slot to keep momentum.",
            "Reuse prompts from high-mood days like ${snapshot.behaviourPatterns.bestMoodDay}.",
            "Tag entries consistently so future insights can be more specific.",
        )

        return AIAnalysis(
            summary = summary,
            moodInsight = moodInsight,
            patterns = patterns,
            suggestions = suggestions,
            generatedAt = LocalDateTime.now(),
        )
    }

    private fun buildSnapshot(
        allEntries: List<SeedEntry>,
        periodEntries: List<SeedEntry>,
        period: InsightPeriod,
    ): InsightSnapshot {
        val moodData = buildMoodData(periodEntries, period)
        val moodDistribution = buildMoodDistribution(periodEntries)
        val stats = buildStats(allEntries, periodEntries)
        val streakInfo = buildStreakInfo(allEntries)
        val topTopics = buildTopTopics(periodEntries)
        val writingActivity = buildWritingActivity(periodEntries, period)
        val patterns = buildBehaviourPatterns(periodEntries, period)

        return InsightSnapshot(
            period = period,
            moodData = moodData,
            moodDistribution = moodDistribution,
            stats = stats,
            streakInfo = streakInfo,
            topTopics = topTopics,
            writingActivity = writingActivity,
            behaviourPatterns = patterns,
        )
    }

    private fun buildMoodData(
        entries: List<SeedEntry>,
        period: InsightPeriod,
    ): List<MoodDataPoint> {
        val today = LocalDate.now()
        val startDate = today.minusDays(period.dayCount.toLong() - 1L)
        val moodByDate = entries
            .filter { it.mood != null }
            .groupBy { it.createdAt.toLocalDate() }

        return generateSequence(startDate) { date ->
            if (date.isBefore(today)) {
                date.plusDays(1)
            } else {
                null
            }
        }.mapNotNull { date ->
            val dayEntries = moodByDate[date].orEmpty()
            if (dayEntries.isEmpty()) {
                null
            } else {
                val avg = dayEntries.mapNotNull { it.mood?.numericValue }.average()
                MoodDataPoint(
                    date = date,
                    value = avg,
                    mood = avg.toMoodLevel(),
                )
            }
        }.toList()
    }

    private fun buildMoodDistribution(entries: List<SeedEntry>): List<MoodDistribution> {
        val moodedEntries = entries.filter { it.mood != null }
        val total = max(moodedEntries.size, 1)
        val counts = moodedEntries.groupingBy { it.mood!! }.eachCount()
        return MoodLevel.entries.map { mood ->
            val count = counts[mood] ?: 0
            MoodDistribution(
                mood = mood,
                count = count,
                percentage = count.toDouble() / total.toDouble(),
            )
        }
    }

    private fun buildStats(
        allEntries: List<SeedEntry>,
        periodEntries: List<SeedEntry>,
    ): JournalStats {
        val periodWords = periodEntries.sumOf { it.words }
        val allWords = allEntries.sumOf { it.words }

        return JournalStats(
            totalEntries = allEntries.size,
            totalWords = allWords,
            averageWordsPerEntry = if (periodEntries.isNotEmpty()) periodWords / periodEntries.size else 0,
            entriesThisPeriod = periodEntries.size,
            longestEntry = periodEntries.maxOfOrNull { it.words } ?: 0,
            shortestEntry = periodEntries.minOfOrNull { it.words } ?: 0,
            entriesWithMood = periodEntries.count { it.mood != null },
        )
    }

    private fun buildStreakInfo(entries: List<SeedEntry>): StreakInfo {
        val days = entries.map { it.createdAt.toLocalDate() }.toSet()
        val sortedDays = days.sorted()
        var longest = 0
        var currentWindow = 0
        var previous: LocalDate? = null

        sortedDays.forEach { day ->
            if (previous == null || previous?.plusDays(1) == day) {
                currentWindow += 1
            } else {
                longest = max(longest, currentWindow)
                currentWindow = 1
            }
            previous = day
        }
        longest = max(longest, currentWindow)

        var current = 0
        var cursor = LocalDate.now()
        while (days.contains(cursor)) {
            current += 1
            cursor = cursor.minusDays(1)
        }

        return StreakInfo(
            currentStreak = current,
            longestStreak = longest,
            totalDaysJournaled = days.size,
            lastEntryDate = sortedDays.lastOrNull(),
        )
    }

    private fun buildTopTopics(entries: List<SeedEntry>): List<TopicStat> {
        val counts = entries
            .flatMap { it.tags }
            .groupingBy { it }
            .eachCount()
            .toList()
            .sortedByDescending { it.second }
            .take(6)
        val total = max(counts.sumOf { it.second }, 1)

        return counts.map { (topic, count) ->
            TopicStat(
                topic = topic,
                count = count,
                percentage = count.toDouble() / total.toDouble(),
            )
        }
    }

    private fun buildWritingActivity(
        entries: List<SeedEntry>,
        period: InsightPeriod,
    ): List<WritingActivityPoint> {
        val today = LocalDate.now()
        val startDate = today.minusDays(period.dayCount.toLong() - 1L)
        val grouped = entries.groupBy { it.createdAt.toLocalDate() }

        return generateSequence(startDate) { date ->
            if (date.isBefore(today)) {
                date.plusDays(1)
            } else {
                null
            }
        }.map { date ->
            val dayEntries = grouped[date].orEmpty()
            WritingActivityPoint(
                date = date,
                entryCount = dayEntries.size,
                wordCount = dayEntries.sumOf { it.words },
            )
        }.toList()
    }

    private fun buildBehaviourPatterns(
        entries: List<SeedEntry>,
        period: InsightPeriod,
    ): BehaviourPatterns {
        val dayOfWeekDistribution = MutableList(7) { 0 }
        val dayOfWeekMood = MutableList(7) { mutableListOf<Double>() }
        val timeOfDayDistribution = MutableList(4) { 0 }

        entries.forEach { entry ->
            val weekdayIndex = entry.createdAt.dayOfWeek.toMondayIndex()
            dayOfWeekDistribution[weekdayIndex] = dayOfWeekDistribution[weekdayIndex] + 1
            entry.mood?.let { dayOfWeekMood[weekdayIndex].add(it.numericValue) }

            val hour = entry.createdAt.hour
            val bucket = when {
                hour in 5..11 -> 0
                hour in 12..16 -> 1
                hour in 17..20 -> 2
                else -> 3
            }
            timeOfDayDistribution[bucket] = timeOfDayDistribution[bucket] + 1
        }

        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val timeNames = listOf("Morning", "Afternoon", "Evening", "Night")
        val bestDayIndex = dayOfWeekDistribution.indices.maxByOrNull { dayOfWeekDistribution[it] } ?: 0
        val timeIndex = timeOfDayDistribution.indices.maxByOrNull { timeOfDayDistribution[it] } ?: 0
        val bestMoodIndex = dayOfWeekMood.indices
            .filter { dayOfWeekMood[it].isNotEmpty() }
            .maxByOrNull { dayOfWeekMood[it].average() }
        val worstMoodIndex = dayOfWeekMood.indices
            .filter { dayOfWeekMood[it].isNotEmpty() }
            .minByOrNull { dayOfWeekMood[it].average() }

        return BehaviourPatterns(
            mostProductiveDay = dayNames[bestDayIndex],
            mostProductiveTime = timeNames[timeIndex],
            averageEntriesPerDay = entries.size.toDouble() / period.dayCount.toDouble(),
            bestMoodDay = bestMoodIndex?.let { dayNames[it] } ?: "-",
            worstMoodDay = worstMoodIndex?.let { dayNames[it] } ?: "-",
            dayOfWeekDistribution = dayOfWeekDistribution,
            timeOfDayDistribution = timeOfDayDistribution,
        )
    }

    private fun seedEntries(today: LocalDate): List<SeedEntry> {
        val topics = listOf(
            "Work",
            "Relationships",
            "Health",
            "Creativity",
            "Goals",
            "Gratitude",
            "Anxiety",
            "Travel",
        )
        val moods = listOf(MoodLevel.GOOD, MoodLevel.GREAT, MoodLevel.OKAY, MoodLevel.LOW, MoodLevel.GOOD)

        val generated = mutableListOf<SeedEntry>()
        var idIndex = 0
        for (offset in 0..180) {
            val date = today.minusDays(offset.toLong())
            val entryCount = when {
                offset % 9 == 0 -> 2
                offset % 5 == 0 -> 1
                offset % 2 == 0 -> 1
                else -> 0
            }

            repeat(entryCount) { index ->
                idIndex += 1
                val mood = moods[(offset + index) % moods.size]
                val words = 140 + ((offset * 17 + index * 31) % 360)
                val firstTag = topics[(offset + index) % topics.size]
                val secondTag = topics[(offset + index + 3) % topics.size]
                val hour = when ((offset + index) % 4) {
                    0 -> 8
                    1 -> 13
                    2 -> 18
                    else -> 22
                }
                generated += SeedEntry(
                    id = "insight-entry-$idIndex",
                    createdAt = date.atTime(hour, 10 + ((offset + index) % 49)),
                    mood = mood,
                    words = words,
                    tags = listOf(firstTag, secondTag),
                )
            }
        }
        return generated.sortedByDescending { it.createdAt }
    }

    private fun DayOfWeek.toMondayIndex(): Int {
        return when (this) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
        }
    }

    private fun Double.toMoodLevel(): MoodLevel {
        return when {
            this >= 4.5 -> MoodLevel.GREAT
            this >= 3.5 -> MoodLevel.GOOD
            this >= 2.5 -> MoodLevel.OKAY
            this >= 1.5 -> MoodLevel.LOW
            else -> MoodLevel.BAD
        }
    }
}
