package com.sekyiemmanuel.mina.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sekyiemmanuel.mina.core.ui.theme.AccentFlame
import com.sekyiemmanuel.mina.core.ui.theme.CanvasBackground
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val OnboardingMutedText = Color(0xFF76737D)
private val OnboardingCardBorder = Color(0xFFE7E5EC)
private val OnboardingDotFuture = Color(0xFFD2D0D8)
private val OnboardingDotComplete = AccentFlame.copy(alpha = 0.56f)

@Composable
fun OnboardingRoute(
    onOnboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.navEvents.collect { event ->
            when (event) {
                OnboardingNavEvent.NavigateToJournal -> onOnboardingCompleted()
            }
        }
    }

    OnboardingScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onEvent: (OnboardingUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.showCustomTimePicker) {
        val initialTime = uiState.data.preferredTime ?: TimePreset.CUSTOM.defaultTime
        OnboardingTimePickerDialog(
            title = "Choose your preferred time",
            initialTime = initialTime,
            onDismiss = { onEvent(OnboardingUiEvent.DismissCustomTimePicker) },
            onConfirm = { onEvent(OnboardingUiEvent.CustomTimeChanged(it)) },
        )
    }

    if (uiState.showReminderTimePicker) {
        val initialTime = uiState.data.reminderTime
            ?: uiState.data.preferredTime
            ?: LocalTime.of(20, 0)
        OnboardingTimePickerDialog(
            title = "Choose your reminder time",
            initialTime = initialTime,
            onDismiss = { onEvent(OnboardingUiEvent.DismissReminderTimePicker) },
            onConfirm = { onEvent(OnboardingUiEvent.ReminderTimeChanged(it)) },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        if (uiState.showProgress) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OnboardingProgressIndicator(
                    currentStep = uiState.currentStep.ordinal,
                    totalSteps = OnboardingStep.totalSteps,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (uiState.currentStep.isOptional) {
                    TextButton(onClick = { onEvent(OnboardingUiEvent.SkipClicked) }) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Box(modifier = Modifier.weight(1f)) {
            when (uiState.currentStep) {
                OnboardingStep.WELCOME -> WelcomeStep(
                    onGetStarted = { onEvent(OnboardingUiEvent.GetStartedClicked) },
                    onSignIn = { onEvent(OnboardingUiEvent.SignInClicked) },
                )
                OnboardingStep.WHY_JOURNAL -> WhyJournalStep(
                    selected = uiState.data.motivation,
                    onSelect = { onEvent(OnboardingUiEvent.MotivationSelected(it)) },
                )
                OnboardingStep.EXPERIENCE_LEVEL -> ExperienceLevelStep(
                    selected = uiState.data.experienceLevel,
                    onSelect = { onEvent(OnboardingUiEvent.ExperienceLevelSelected(it)) },
                )
                OnboardingStep.JOURNALING_GOAL -> JournalingGoalStep(
                    selected = uiState.data.frequency,
                    onSelect = { onEvent(OnboardingUiEvent.FrequencySelected(it)) },
                )
                OnboardingStep.PREFERRED_TIME -> PreferredTimeStep(
                    selectedPreset = uiState.data.preferredTimePreset,
                    selectedTime = uiState.data.preferredTime,
                    onPresetSelect = { onEvent(OnboardingUiEvent.TimePresetSelected(it)) },
                    onPickCustomTime = { onEvent(OnboardingUiEvent.OpenCustomTimePicker) },
                )
                OnboardingStep.TOPICS -> TopicsStep(
                    selectedTopics = uiState.data.topics,
                    customTopic = uiState.data.customTopic,
                    onTopicToggle = { onEvent(OnboardingUiEvent.TopicToggled(it)) },
                    onCustomTopicChange = { onEvent(OnboardingUiEvent.CustomTopicChanged(it)) },
                )
                OnboardingStep.AI_ASSISTANCE -> AiAssistanceStep(
                    sliderValue = uiState.aiSliderValue,
                    currentLevel = uiState.data.aiLevel,
                    onSliderChange = { onEvent(OnboardingUiEvent.AiSliderChanged(it)) },
                )
                OnboardingStep.PRIVACY_SECURITY -> PrivacyStep(
                    enabled = uiState.data.enablePasscode,
                    onToggle = { onEvent(OnboardingUiEvent.PasscodeToggled(it)) },
                )
                OnboardingStep.HEALTH_SYNC -> HealthSyncStep(
                    enabled = uiState.data.syncHealth,
                    onToggle = { onEvent(OnboardingUiEvent.HealthSyncToggled(it)) },
                )
                OnboardingStep.NOTIFICATIONS -> NotificationsStep(
                    enabled = uiState.data.enableNotifications,
                    reminderTime = uiState.data.reminderTime ?: uiState.data.preferredTime ?: LocalTime.of(20, 0),
                    onToggle = { onEvent(OnboardingUiEvent.NotificationsToggled(it)) },
                    onChangeReminderTime = { onEvent(OnboardingUiEvent.OpenReminderTimePicker) },
                )
                OnboardingStep.SETUP_SUMMARY -> SetupSummaryStep(
                    data = uiState.data,
                    onEdit = { onEvent(OnboardingUiEvent.EditPreferencesClicked) },
                )
                OnboardingStep.CREATE_ACCOUNT -> CreateAccountStep(
                    isLoading = uiState.isLoading,
                    onGoogle = { onEvent(OnboardingUiEvent.ContinueWithGoogleClicked) },
                    onEmail = { onEvent(OnboardingUiEvent.ContinueWithEmailClicked) },
                    onSkip = { onEvent(OnboardingUiEvent.SkipAccountClicked) },
                )
            }
        }

        if (uiState.showBottomNavigation) {
            OnboardingBottomNavigation(
                showBack = uiState.showBackButton,
                nextText = uiState.nextButtonText,
                canProceed = uiState.canProceed,
                isLoading = uiState.isLoading,
                onBack = { onEvent(OnboardingUiEvent.BackClicked) },
                onNext = { onEvent(OnboardingUiEvent.NextClicked) },
            )
        }
    }
}

@Composable
private fun WelcomeStep(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        MascotIllustration(
            icon = Icons.Outlined.Pets,
            size = 208.dp,
            label = "MINA",
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Welcome to Mina",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Your private space for thoughts, reflections, and self-discovery.",
            style = MaterialTheme.typography.bodyLarge.copy(color = OnboardingMutedText),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Built so you stick with it.",
            style = MaterialTheme.typography.bodyLarge.copy(color = OnboardingMutedText),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onGetStarted,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = AccentFlame),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(
                text = "Get Started",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                ),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = onSignIn) {
            Text(
                text = "Already have an account? Sign in",
                style = MaterialTheme.typography.bodyMedium.copy(color = OnboardingMutedText),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun WhyJournalStep(
    selected: JournalingMotivation?,
    onSelect: (JournalingMotivation) -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.Lightbulb,
        title = "Why do you want to journal?",
        subtitle = "This helps personalize your experience",
    ) {
        JournalingMotivation.entries.forEach { item ->
            OnboardingOptionCard(
                title = item.title,
                subtitle = item.subtitle,
                icon = motivationIcon(item),
                selected = selected == item,
                onClick = { onSelect(item) },
            )
        }
    }
}

@Composable
private fun ExperienceLevelStep(
    selected: ExperienceLevel?,
    onSelect: (ExperienceLevel) -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.Book,
        title = "What is your experience with journaling?",
        subtitle = "We will tailor guidance to your level",
    ) {
        ExperienceLevel.entries.forEach { level ->
            OnboardingOptionCard(
                title = level.title,
                subtitle = level.subtitle,
                icon = experienceIcon(level),
                selected = selected == level,
                onClick = { onSelect(level) },
            )
        }
    }
}

@Composable
private fun JournalingGoalStep(
    selected: JournalingFrequency?,
    onSelect: (JournalingFrequency) -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.Flag,
        title = "How often do you want to journal?",
        subtitle = "Set a goal that feels achievable",
    ) {
        JournalingFrequency.entries.forEach { item ->
            OnboardingOptionCard(
                title = item.title,
                subtitle = item.subtitle,
                icon = frequencyIcon(item),
                selected = selected == item,
                onClick = { onSelect(item) },
            )
        }
    }
}

@Composable
private fun PreferredTimeStep(
    selectedPreset: TimePreset?,
    selectedTime: LocalTime?,
    onPresetSelect: (TimePreset) -> Unit,
    onPickCustomTime: () -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.AccessTime,
        title = "When do you prefer to journal?",
        subtitle = "We can send reminders at the right time",
    ) {
        TimePreset.entries.forEach { preset ->
            OnboardingOptionCard(
                title = preset.title,
                subtitle = preset.subtitle,
                icon = timePresetIcon(preset),
                selected = selectedPreset == preset,
                onClick = { onPresetSelect(preset) },
            )
        }

        if (selectedPreset == TimePreset.CUSTOM) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Custom time",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = formatClock(selectedTime ?: TimePreset.CUSTOM.defaultTime),
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64626A)),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onPickCustomTime) {
                        Text("Change")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TopicsStep(
    selectedTopics: Set<JournalTopic>,
    customTopic: String,
    onTopicToggle: (JournalTopic) -> Unit,
    onCustomTopicChange: (String) -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.Label,
        title = "What do you want to explore?",
        subtitle = "Select all that interest you (optional)",
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            JournalTopic.entries.forEach { topic ->
                OnboardingChip(
                    title = topic.title,
                    selected = selectedTopics.contains(topic),
                    onClick = { onTopicToggle(topic) },
                )
            }
        }

        OutlinedTextField(
            value = customTopic,
            onValueChange = onCustomTopicChange,
            label = { Text("Anything else? (optional)") },
            placeholder = { Text("For example: Starting a business") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = Color(0xFF88868E),
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "These help personalize your prompts. You can change them anytime in settings.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8B8991)),
            )
        }
    }
}

@Composable
private fun AiAssistanceStep(
    sliderValue: Float,
    currentLevel: AIAssistanceLevel,
    onSliderChange: (Float) -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.AutoAwesome,
        title = "How should Mina help you write?",
        subtitle = "Adjust how much AI guidance you receive",
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = currentLevel.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentLevel.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = OnboardingMutedText),
                )
            }
        }

        Slider(
            value = sliderValue,
            onValueChange = onSliderChange,
            valueRange = 0f..1f,
            steps = 3,
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = AccentFlame,
                activeTrackColor = AccentFlame,
                inactiveTrackColor = Color(0xFFE2E1E6),
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = AIAssistanceLevel.MINIMAL.title,
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8B8991)),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = AIAssistanceLevel.FULL.title,
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8B8991)),
            )
        }

        Text(
            text = "Example",
            style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF8B8991)),
        )
        Text(
            text = currentLevel.example,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF72707A)),
        )
    }
}

@Composable
private fun PrivacyStep(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.Lock,
        title = "Your privacy matters",
        subtitle = "Your entries are encrypted and private",
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color(0xFF59A869),
                    modifier = Modifier.size(24.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "End-to-end encryption",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Text(
                        text = "Only you can read your entries",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF7F7C85)),
                    )
                }
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF59A869),
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        OnboardingToggleCard(
            title = "Enable passcode",
            subtitle = "Add an extra layer of protection",
            icon = Icons.Filled.Lock,
            iconTint = AccentFlame,
            checked = enabled,
            onCheckedChange = onToggle,
        )

        Text(
            text = "We never sell your data.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8B8991)),
        )
    }
}

@Composable
private fun HealthSyncStep(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.Favorite,
        title = "Connect with Health Connect",
        subtitle = "Correlate your mood with health metrics",
    ) {
        OnboardingToggleCard(
            title = "Sync with Health Connect",
            subtitle = "Track mood alongside sleep, movement, and more",
            icon = Icons.Filled.Favorite,
            iconTint = Color(0xFFE35555),
            checked = enabled,
            onCheckedChange = onToggle,
        )

        if (enabled) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "What you will get",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                    BenefitRow(text = "See how sleep affects your mood")
                    BenefitRow(text = "Track exercise impact on wellbeing")
                    BenefitRow(text = "Discover patterns in emotional health")
                }
            }
        }

        Text(
            text = "You can enable this later in settings.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8B8991)),
        )
    }
}

@Composable
private fun NotificationsStep(
    enabled: Boolean,
    reminderTime: LocalTime,
    onToggle: (Boolean) -> Unit,
    onChangeReminderTime: () -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.Notifications,
        title = "Enable notifications?",
        subtitle = "Mina can send gentle reminders to build consistency",
    ) {
        OnboardingToggleCard(
            title = "Enable notifications",
            subtitle = "Get daily reminders to journal",
            icon = Icons.Filled.Notifications,
            iconTint = Color(0xFF497BEE),
            checked = enabled,
            onCheckedChange = onToggle,
        )

        if (enabled) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reminder time",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        )
                        Text(
                            text = formatClock(reminderTime),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF72707A)),
                        )
                    }
                    TextButton(onClick = onChangeReminderTime) {
                        Text("Change")
                    }
                }
            }
        }

        Text(
            text = "You can customize notification settings anytime.",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8B8991)),
        )
    }
}

@Composable
private fun SetupSummaryStep(
    data: OnboardingData,
    onEdit: () -> Unit,
) {
    StepContainer(
        illustrationIcon = Icons.Filled.CheckCircle,
        title = "Your personalized setup",
        subtitle = "Review your preferences",
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                SummaryRow(label = "Goal", value = data.frequency?.title ?: "Not set")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE9E7EC))
                SummaryRow(
                    label = "Reminder",
                    value = if (data.enableNotifications) {
                        formatClock(data.reminderTime ?: data.preferredTime ?: LocalTime.of(20, 0))
                    } else {
                        "Off"
                    },
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE9E7EC))
                SummaryRow(label = "Focus", value = summarizeTopics(data.topics))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE9E7EC))
                SummaryRow(label = "AI mode", value = data.aiLevel.title)
            }
        }

        Surface(
            color = AccentFlame.copy(alpha = 0.12f),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Mina's first impression",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = data.aiPersonalitySummary(),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF5F5D67)),
                )
            }
        }

        TextButton(onClick = onEdit) {
            Text("Edit preferences")
        }
    }
}

@Composable
private fun CreateAccountStep(
    isLoading: Boolean,
    onGoogle: () -> Unit,
    onEmail: () -> Unit,
    onSkip: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            MascotIllustration(
                icon = Icons.Filled.PersonAdd,
                size = 176.dp,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Save your progress",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create an account to sync data across devices and never lose your journal history.",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF7B7880)),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onGoogle,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onEmail,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = AccentFlame),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Use email instead",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            TextButton(onClick = onSkip) {
                Text(
                    text = "Skip for now",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnboardingMutedText,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your data stays on this device until you create an account.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF8B8991)),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (isLoading) {
            Surface(
                color = Color.Black.copy(alpha = 0.32f),
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun StepContainer(
    illustrationIcon: ImageVector,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        MascotIllustration(icon = illustrationIcon, size = 156.dp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(color = OnboardingMutedText),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun MascotIllustration(
    icon: ImageVector,
    size: Dp,
    label: String? = null,
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = CircleShape,
            color = AccentFlame.copy(alpha = 0.10f),
            modifier = Modifier.fillMaxSize(),
        ) {}
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentFlame,
            modifier = Modifier.size(size * 0.34f),
        )
        if (label != null) {
            Text(
                text = label,
                color = AccentFlame,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp),
            )
        }
    }
}

@Composable
private fun OnboardingOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (selected) AccentFlame.copy(alpha = 0.07f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) AccentFlame else OnboardingCardBorder,
        ),
        shadowElevation = if (selected) 1.dp else 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) AccentFlame else Color(0xFF8D8A93),
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = OnboardingMutedText),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = AccentFlame,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun OnboardingToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = OnboardingMutedText),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@Composable
private fun OnboardingChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = if (selected) AccentFlame.copy(alpha = 0.14f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) AccentFlame else OnboardingCardBorder,
        ),
        shadowElevation = 0.dp,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (selected) AccentFlame else MaterialTheme.colorScheme.onBackground,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
        )
    }
}

@Composable
private fun OnboardingProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(totalSteps) { index ->
            if (index == currentStep) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentFlame,
                    modifier = Modifier
                        .width(28.dp)
                        .height(8.dp),
                ) {}
            } else {
                Surface(
                    shape = CircleShape,
                    color = if (index < currentStep) {
                        OnboardingDotComplete
                    } else {
                        OnboardingDotFuture
                    },
                    modifier = Modifier.size(8.dp),
                ) {}
            }
        }
    }
}

@Composable
private fun OnboardingBottomNavigation(
    showBack: Boolean,
    nextText: String,
    canProceed: Boolean,
    isLoading: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBack) {
            Surface(
                onClick = onBack,
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF6F6D75),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onNext,
            enabled = canProceed && !isLoading && nextText.isNotEmpty(),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentFlame,
                disabledContainerColor = Color(0xFFCAC7CE),
            ),
            modifier = Modifier.height(52.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = nextText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(start = 14.dp),
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6E6B74)),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun BenefitRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = AccentFlame,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF6F6D75)),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OnboardingTimePickerDialog(
    title: String,
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
                },
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

private fun formatClock(time: LocalTime): String {
    return time.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))
}

private fun summarizeTopics(topics: Set<JournalTopic>): String {
    if (topics.isEmpty()) {
        return "All topics"
    }
    return topics
        .take(3)
        .joinToString(", ") { it.title }
}

private fun motivationIcon(item: JournalingMotivation): ImageVector {
    return when (item) {
        JournalingMotivation.MENTAL_CLARITY -> Icons.Filled.Lightbulb
        JournalingMotivation.REDUCE_STRESS -> Icons.Filled.Favorite
        JournalingMotivation.PERSONAL_GROWTH -> Icons.Filled.TrendingUp
        JournalingMotivation.TRACK_LIFE -> Icons.Filled.EventNote
        JournalingMotivation.CREATIVE_EXPRESSION -> Icons.Filled.Brush
    }
}

private fun experienceIcon(item: ExperienceLevel): ImageVector {
    return when (item) {
        ExperienceLevel.NEW_TO_JOURNALING -> Icons.Filled.Spa
        ExperienceLevel.JOURNALED_BEFORE -> Icons.Filled.Book
        ExperienceLevel.REGULAR_JOURNALER -> Icons.Filled.Star
    }
}

private fun frequencyIcon(item: JournalingFrequency): ImageVector {
    return when (item) {
        JournalingFrequency.DAILY -> Icons.Filled.LocalFireDepartment
        JournalingFrequency.WEEKDAYS -> Icons.Filled.CalendarToday
        JournalingFrequency.FEW_TIMES_A_WEEK -> Icons.Filled.DateRange
        JournalingFrequency.WEEKLY -> Icons.Filled.Event
        JournalingFrequency.NO_GOAL -> Icons.Filled.Explore
    }
}

private fun timePresetIcon(item: TimePreset): ImageVector {
    return when (item) {
        TimePreset.MORNING -> Icons.Filled.WbSunny
        TimePreset.AFTERNOON -> Icons.Filled.WbSunny
        TimePreset.EVENING -> Icons.Filled.Schedule
        TimePreset.NIGHT -> Icons.Filled.Schedule
        TimePreset.CUSTOM -> Icons.Filled.AccessTime
    }
}
