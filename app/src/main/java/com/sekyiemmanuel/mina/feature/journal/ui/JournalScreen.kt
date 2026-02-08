package com.sekyiemmanuel.mina.feature.journal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
import kotlin.math.abs

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
    val entryContentDescription = stringResource(id = R.string.journal_entry)
    val voiceInputDescription = stringResource(id = R.string.voice_input)
    val cameraInputDescription = stringResource(id = R.string.camera_input)
    val addInputDescription = stringResource(id = R.string.add_input)
    val keyboardInputDescription = stringResource(id = R.string.keyboard_input)
    val voiceRecordingWaveformDescription = stringResource(id = R.string.voice_recording_waveform)
    val voiceAcceptDescription = stringResource(id = R.string.accept_voice_recording)
    val voiceCancelDescription = stringResource(id = R.string.discard_voice_recording)
    val bottomPlaceholderLabel = stringResource(id = R.string.temporary_navigation_placeholder)
    var entryText by rememberSaveable { mutableStateOf("") }
    var isEntryFocused by rememberSaveable { mutableStateOf(false) }
    var isVoiceRecording by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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
            .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 24.dp),
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

        JournalEntryField(
            text = entryText,
            placeholder = uiState.emptyStateMessage,
            contentDescription = entryContentDescription,
            isFocused = isEntryFocused,
            onTextChanged = { entryText = it },
            onFocusChanged = {
                isEntryFocused = it
                if (!it) {
                    isVoiceRecording = false
                }
            },
            onTap = {
                isEntryFocused = true
                focusRequester.requestFocus()
                keyboardController?.show()
            },
            focusRequester = focusRequester,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isEntryFocused) {
            if (isVoiceRecording) {
                VoiceRecordingBar(
                    waveformDescription = voiceRecordingWaveformDescription,
                    confirmDescription = voiceAcceptDescription,
                    cancelDescription = voiceCancelDescription,
                    onConfirm = { isVoiceRecording = false },
                    onCancel = { isVoiceRecording = false },
                )
            } else {
                ComposerActionsBar(
                    voiceInputDescription = voiceInputDescription,
                    cameraInputDescription = cameraInputDescription,
                    addInputDescription = addInputDescription,
                    keyboardInputDescription = keyboardInputDescription,
                    onMicClick = { isVoiceRecording = true },
                )
            }
        } else {
            // Reserved area for the real bottom navigation tab in a later feature.
            TemporaryBottomPlaceholder(
                label = bottomPlaceholderLabel,
            )
        }
    }
}

@Composable
private fun JournalEntryField(
    text: String,
    placeholder: String,
    contentDescription: String,
    isFocused: Boolean,
    onTextChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onTap: () -> Unit,
    focusRequester: FocusRequester,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cursorColor = if (isFocused) Color(0xFF94B7E6) else Color.Transparent

    BasicTextField(
        value = text,
        onValueChange = onTextChanged,
        textStyle = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChanged(it.isFocused) }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTap,
            )
            .semantics { this.contentDescription = contentDescription },
        cursorBrush = SolidColor(cursorColor),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Default,
            keyboardType = KeyboardType.Text,
        ),
        keyboardActions = KeyboardActions(),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = PlaceholderText,
                            fontSize = 22.sp,
                        ),
                    )
                innerTextField()
            }
        },
    )
}

@Composable
private fun ComposerActionsBar(
    voiceInputDescription: String,
    cameraInputDescription: String,
    addInputDescription: String,
    keyboardInputDescription: String,
    onMicClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
    ) {
        CircleActionButton(
            icon = Icons.Filled.Mic,
            contentDescription = voiceInputDescription,
            tint = Color(0xFF3B79E5),
            onClick = onMicClick,
        )
        CircleActionButton(
            icon = Icons.Filled.CameraAlt,
            contentDescription = cameraInputDescription,
            tint = Color(0xFF9A56CF),
            onClick = {},
        )
        CircleActionButton(
            icon = Icons.Filled.Add,
            contentDescription = addInputDescription,
            tint = Color(0xFFE6962E),
            onClick = {},
        )
        IconButton(
            onClick = {},
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Keyboard,
                contentDescription = keyboardInputDescription,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun VoiceRecordingBar(
    waveformDescription: String,
    confirmDescription: String,
    cancelDescription: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 0.dp,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .semantics { contentDescription = waveformDescription },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (index in 0 until 23) {
                    val distanceToCenter = abs(index - 11)
                    val barHeight = (22 - distanceToCenter).coerceAtLeast(10).dp
                    val barColor = if (distanceToCenter <= 7) {
                        Color(0xFF131313)
                    } else {
                        Color(0xFF9E9E9E)
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 4.dp, height = barHeight)
                            .background(
                                color = barColor,
                                shape = RoundedCornerShape(3.dp),
                            ),
                    )
                }
            }
        }

        CircleActionButton(
            icon = Icons.Filled.Check,
            contentDescription = confirmDescription,
            tint = Color(0xFF63B967),
            onClick = onConfirm,
        )
        CircleActionButton(
            icon = Icons.Filled.Close,
            contentDescription = cancelDescription,
            tint = Color(0xFFE25B50),
            onClick = onCancel,
        )
    }
}

@Composable
private fun CircleActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 0.dp,
        modifier = Modifier.size(52.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

@Composable
private fun MascotPlaceholder(description: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
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
        shadowElevation = 0.dp,
        modifier = Modifier
            .height(56.dp)
            .semantics { this.contentDescription = contentDescription },
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp),
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
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 2.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = streakContentDescription,
                tint = AccentFlame,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = streak.toString(),
                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(44.dp),
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
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(horizontal = 6.dp, vertical = 4.dp),
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
                    fontSize = 17.sp,
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
