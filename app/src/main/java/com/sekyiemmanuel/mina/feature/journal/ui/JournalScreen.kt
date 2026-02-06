package com.sekyiemmanuel.mina.feature.journal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sekyiemmanuel.mina.R
import com.sekyiemmanuel.mina.core.ui.theme.AccentFlame
import com.sekyiemmanuel.mina.core.ui.theme.PlaceholderText
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun JournalRoute(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JournalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.navEvents.collect { event ->
            when (event) {
                JournalNavEvent.NavigateToSettings -> onNavigateToSettings()
                JournalNavEvent.ShowDatePicker -> showDatePicker = true
            }
        }
    }

    JournalScreen(
        uiState = uiState,
        showDatePicker = showDatePicker,
        onDateClick = { viewModel.onEvent(JournalUiEvent.DateClicked) },
        onDateSelected = { selectedDate ->
            viewModel.onEvent(JournalUiEvent.DateSelected(selectedDate))
        },
        onDismissDatePicker = { showDatePicker = false },
        onSettingsClick = { viewModel.onEvent(JournalUiEvent.SettingsClicked) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    uiState: JournalUiState,
    showDatePicker: Boolean,
    onDateClick: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onDismissDatePicker: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val journalDateDescription = stringResource(id = R.string.journal_date)
    val dailyStreakDescription = stringResource(id = R.string.daily_streak)
    val openSettingsDescription = stringResource(id = R.string.open_settings)
    val mascotDescription = stringResource(id = R.string.mascot_placeholder)
    val bottomPlaceholderLabel = stringResource(id = R.string.temporary_navigation_placeholder)

    if (showDatePicker) {
        key(uiState.selectedDate) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.selectedDate.toEpochMillis(),
            )
            DatePickerDialog(
                onDismissRequest = onDismissDatePicker,
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis
                                ?.toLocalDate()
                                ?.let(onDateSelected)
                            onDismissDatePicker()
                        },
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDatePicker) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MascotPlaceholder(
                description = mascotDescription,
            )
            DatePill(
                label = uiState.dateLabel,
                contentDescription = journalDateDescription,
                onClick = onDateClick,
            )
            StatusPill(
                streak = uiState.streakCount,
                streakContentDescription = dailyStreakDescription,
                settingsContentDescription = openSettingsDescription,
                onSettingsClick = onSettingsClick,
            )
        }

        Spacer(modifier = Modifier.height(42.dp))

        Text(
            text = uiState.emptyStateMessage,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = PlaceholderText,
            ),
        )

        Spacer(modifier = Modifier.weight(1f))

        // Reserved area for the real bottom navigation tab in a later feature.
        TemporaryBottomPlaceholder(
            label = bottomPlaceholderLabel,
        )
    }
}

@Composable
private fun MascotPlaceholder(description: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(color = Color.White, shape = CircleShape)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Pets,
            contentDescription = null,
            tint = Color(0xFF1F3BFE),
            modifier = Modifier.size(26.dp),
        )
    }
}

@Composable
private fun DatePill(
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .height(56.dp)
            .semantics { this.contentDescription = contentDescription },
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 36.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
private fun StatusPill(
    streak: Int,
    streakContentDescription: String,
    settingsContentDescription: String,
    onSettingsClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = streakContentDescription,
                tint = AccentFlame,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = streak.toString(),
                modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = settingsContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun TemporaryBottomPlaceholder(label: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(40.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 6.dp, vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = PlaceholderText,
                ),
            )
        }
    }
}

private fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

private fun LocalDate.toEpochMillis(): Long {
    return atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}
