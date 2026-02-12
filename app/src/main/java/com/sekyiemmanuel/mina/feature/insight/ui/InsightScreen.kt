package com.sekyiemmanuel.mina.feature.insight.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sekyiemmanuel.mina.core.ui.theme.AccentFlame
import com.sekyiemmanuel.mina.core.ui.theme.CanvasBackground
import com.sekyiemmanuel.mina.feature.insight.domain.InsightPeriod
import com.sekyiemmanuel.mina.feature.insight.domain.MoodLevel
import com.sekyiemmanuel.mina.feature.insight.domain.TrendDirection
import kotlin.math.max

@Composable
fun InsightRoute(
    modifier: Modifier = Modifier,
    viewModel: InsightViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(InsightUiEvent.OnAppear)
    }

    InsightScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
fun InsightScreen(
    uiState: InsightUiState,
    onEvent: (InsightUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading && !uiState.hasLoadedOnce) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CanvasBackground)
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = AccentFlame)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Analyzing your journal...", color = Color(0xFF807B86))
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TopRow(onRefresh = { onEvent(InsightUiEvent.Refresh) })
        PeriodSelector(
            selectedPeriod = uiState.selectedPeriod,
            onPeriodSelected = { onEvent(InsightUiEvent.PeriodChanged(it)) },
        )

        if (uiState.errorMessage != null && uiState.stats.totalEntries == 0) {
            ErrorCard(
                message = uiState.errorMessage,
                onRetry = { onEvent(InsightUiEvent.Refresh) },
            )
        } else if (uiState.stats.totalEntries == 0) {
            EmptyCard()
        } else {
            AnalysisCard(
                uiState = uiState,
                onGenerate = { onEvent(InsightUiEvent.GenerateAnalysis) },
            )
            OverviewCard(uiState = uiState)
            MoodCard(uiState = uiState)
            ActivityCard(uiState = uiState)
            PatternCard(uiState = uiState)
            TopicsCard(uiState = uiState)
        }
        Spacer(modifier = Modifier.height(84.dp))
    }
}

@Composable
private fun TopRow(onRefresh: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Insight",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onRefresh) {
            Icon(Icons.Filled.Refresh, contentDescription = "Refresh insights")
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: InsightPeriod,
    onPeriodSelected: (InsightPeriod) -> Unit,
) {
    Surface(color = Color.White, shape = RoundedCornerShape(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            InsightPeriod.entries.forEach { period ->
                val selected = selectedPeriod == period
                Surface(
                    onClick = { onPeriodSelected(period) },
                    color = if (selected) AccentFlame else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 9.dp)) {
                        Text(
                            text = period.label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisCard(
    uiState: InsightUiState,
    onGenerate: () -> Unit,
) {
    Surface(color = Color(0xFFFFF8EE), shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color(0xFF9A56CF))
                Text("AI Analysis", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            }
            when {
                uiState.isGeneratingAnalysis -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = AccentFlame)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing your patterns...", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7F7986))
                    }
                }

                uiState.aiAnalysis != null -> {
                    Text(uiState.aiAnalysis.summary, style = MaterialTheme.typography.bodyMedium)
                    Text(uiState.aiAnalysis.moodInsight, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7F7986))
                    uiState.aiAnalysis.suggestions.take(2).forEach {
                        Text("- $it", style = MaterialTheme.typography.bodySmall, color = Color(0xFF625D68))
                    }
                }

                else -> {
                    Text(
                        "Generate a personalized summary of your mood and writing habits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7F7986),
                    )
                    Button(onClick = onGenerate, modifier = Modifier.fillMaxWidth()) {
                        Text("Generate Analysis")
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(uiState: InsightUiState) {
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = AccentFlame,
                        modifier = Modifier.size(24.dp),
                    )
                    Column {
                        Text("${uiState.streakInfo.currentStreak} day streak", fontWeight = FontWeight.SemiBold)
                        Text("Best ${uiState.streakInfo.longestStreak} days", color = Color(0xFF817B87), style = MaterialTheme.typography.bodySmall)
                    }
                }
                Text(uiState.moodTrend, color = uiState.trendColor, fontWeight = FontWeight.SemiBold)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Metric(label = "Entries", value = uiState.stats.entriesThisPeriod.toString())
                Metric(label = "Avg Words", value = uiState.stats.averageWordsPerEntry.toString())
                Metric(label = "Total Days", value = uiState.streakInfo.totalDaysJournaled.toString())
            }
        }
    }
}

@Composable
private fun MoodCard(uiState: InsightUiState) {
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Mood Trend", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                val trendIcon = when (uiState.moodTrendDirection) {
                    TrendDirection.IMPROVING -> Icons.Filled.TrendingUp
                    TrendDirection.DECLINING -> Icons.Filled.TrendingDown
                    TrendDirection.STABLE -> Icons.Filled.TrendingFlat
                }
                Icon(trendIcon, contentDescription = null, tint = uiState.trendColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (uiState.averageMood > 0) "%.1f".format(uiState.averageMood) else "-",
                    fontWeight = FontWeight.Bold,
                    color = uiState.trendColor,
                )
            }
            uiState.moodDistribution.forEach { item ->
                MetricBar(
                    label = item.mood.label,
                    value = item.count.toString(),
                    progress = item.percentage.toFloat(),
                    color = moodColor(item.mood),
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(uiState: InsightUiState) {
    val counts = condensedCounts(uiState)
    val maxCount = max(counts.maxOrNull() ?: 1, 1)
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Writing Activity", fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                counts.forEach { count ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height((count.toFloat() / maxCount.toFloat() * 52f).dp.coerceAtLeast(2.dp))
                            .background(
                                color = if (count == 0) Color(0x20B0ABB7) else AccentFlame.copy(alpha = 0.5f + 0.5f * count / maxCount.toFloat()),
                                shape = RoundedCornerShape(2.dp),
                            ),
                    )
                }
            }
            Text(
                text = "Period total words: ${formatCompact(uiState.stats.totalWords)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF817B87),
            )
        }
    }
}

@Composable
private fun PatternCard(uiState: InsightUiState) {
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Behaviour Patterns", fontWeight = FontWeight.SemiBold)
            Text("Most productive day: ${uiState.behaviourPatterns.mostProductiveDay}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF635E69))
            Text("Preferred time: ${uiState.behaviourPatterns.mostProductiveTime}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF635E69))
            Text("Best mood day: ${uiState.behaviourPatterns.bestMoodDay}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF635E69))
        }
    }
}

@Composable
private fun TopicsCard(uiState: InsightUiState) {
    if (uiState.topTopics.isEmpty()) return
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Top Themes", fontWeight = FontWeight.SemiBold)
            uiState.topTopics.forEach { topic ->
                MetricBar(
                    label = topic.topic,
                    value = topic.count.toString(),
                    progress = topic.percentage.toFloat(),
                    color = AccentFlame,
                )
            }
        }
    }
}

@Composable
private fun Metric(
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF847F8A))
    }
}

@Composable
private fun MetricBar(
    label: String,
    value: String,
    progress: Float,
    color: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.width(84.dp), style = MaterialTheme.typography.bodySmall, color = Color(0xFF6A6471))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .background(Color(0xFFE9E6EE), CircleShape),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(6.dp)
                    .background(color, CircleShape),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(value, style = MaterialTheme.typography.labelMedium, color = Color(0xFF5A5560))
    }
}

@Composable
private fun EmptyCard() {
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color(0xFFC0BCC8), modifier = Modifier.size(46.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("Start journaling to see insights", fontWeight = FontWeight.SemiBold)
            Text(
                "Your mood patterns and writing habits will appear here.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF847F8A),
            )
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
) {
    Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Unable to load insights", fontWeight = FontWeight.SemiBold)
            Text(message, style = MaterialTheme.typography.bodySmall, color = Color(0xFF847F8A))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

private fun condensedCounts(uiState: InsightUiState): List<Int> {
    return when (uiState.selectedPeriod) {
        InsightPeriod.WEEK, InsightPeriod.MONTH -> uiState.writingActivity.map { it.entryCount }
        InsightPeriod.THREE_MONTHS, InsightPeriod.YEAR -> {
            val chunk = if (uiState.selectedPeriod == InsightPeriod.THREE_MONTHS) 7 else 14
            uiState.writingActivity.chunked(chunk).map { segment -> segment.sumOf { it.entryCount } }
        }
    }
}

private fun moodColor(mood: MoodLevel): Color {
    return when (mood) {
        MoodLevel.GREAT -> Color(0xFF43B36C)
        MoodLevel.GOOD -> Color(0xFF7CCB72)
        MoodLevel.OKAY -> Color(0xFFE8B347)
        MoodLevel.LOW -> Color(0xFFF38B55)
        MoodLevel.BAD -> Color(0xFFE45555)
    }
}

private fun formatCompact(value: Int): String {
    return if (value >= 1000) {
        String.format("%.1fK", value / 1000f)
    } else {
        value.toString()
    }
}
