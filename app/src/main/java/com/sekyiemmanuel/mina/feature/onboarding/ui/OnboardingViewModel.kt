package com.sekyiemmanuel.mina.feature.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekyiemmanuel.mina.feature.onboarding.domain.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<OnboardingNavEvent>(extraBufferCapacity = 1)
    val navEvents: SharedFlow<OnboardingNavEvent> = _navEvents.asSharedFlow()

    fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.GetStartedClicked -> navigateForward()
            OnboardingUiEvent.BackClicked -> navigateBackward()
            OnboardingUiEvent.NextClicked -> onNextClicked()
            OnboardingUiEvent.SkipClicked -> onSkipClicked()
            OnboardingUiEvent.SignInClicked -> completeOnboarding()
            OnboardingUiEvent.EditPreferencesClicked -> goToStep(OnboardingStep.WHY_JOURNAL)
            OnboardingUiEvent.ContinueWithGoogleClicked -> simulateAccountCreation()
            OnboardingUiEvent.ContinueWithEmailClicked -> simulateAccountCreation()
            OnboardingUiEvent.SkipAccountClicked -> completeOnboarding()
            OnboardingUiEvent.OpenCustomTimePicker -> {
                _uiState.update { it.copy(showCustomTimePicker = true) }
            }
            OnboardingUiEvent.DismissCustomTimePicker -> {
                _uiState.update { it.copy(showCustomTimePicker = false) }
            }
            is OnboardingUiEvent.CustomTimeChanged -> {
                _uiState.update { current ->
                    current.copy(
                        showCustomTimePicker = false,
                        data = current.data.copy(
                            preferredTime = event.time,
                            reminderTime = event.time,
                        ),
                    )
                }
            }
            OnboardingUiEvent.OpenReminderTimePicker -> {
                _uiState.update { it.copy(showReminderTimePicker = true) }
            }
            OnboardingUiEvent.DismissReminderTimePicker -> {
                _uiState.update { it.copy(showReminderTimePicker = false) }
            }
            is OnboardingUiEvent.ReminderTimeChanged -> {
                _uiState.update { current ->
                    current.copy(
                        showReminderTimePicker = false,
                        data = current.data.copy(reminderTime = event.time),
                    )
                }
            }
            is OnboardingUiEvent.MotivationSelected -> {
                _uiState.update { current ->
                    current.copy(
                        data = current.data.copy(motivation = event.motivation),
                    )
                }
            }
            is OnboardingUiEvent.ExperienceLevelSelected -> {
                _uiState.update { current ->
                    current.copy(
                        data = current.data.copy(experienceLevel = event.level),
                    )
                }
            }
            is OnboardingUiEvent.FrequencySelected -> {
                _uiState.update { current ->
                    current.copy(
                        data = current.data.copy(frequency = event.frequency),
                    )
                }
            }
            is OnboardingUiEvent.TimePresetSelected -> {
                _uiState.update { current ->
                    val time = if (event.preset == TimePreset.CUSTOM) {
                        current.data.preferredTime ?: TimePreset.CUSTOM.defaultTime
                    } else {
                        event.preset.defaultTime
                    }
                    current.copy(
                        showCustomTimePicker = event.preset == TimePreset.CUSTOM,
                        data = current.data.copy(
                            preferredTimePreset = event.preset,
                            preferredTime = time,
                            reminderTime = time,
                        ),
                    )
                }
            }
            is OnboardingUiEvent.TopicToggled -> {
                _uiState.update { current ->
                    val nextTopics = current.data.topics.toMutableSet().apply {
                        if (contains(event.topic)) {
                            remove(event.topic)
                        } else {
                            add(event.topic)
                        }
                    }
                    current.copy(data = current.data.copy(topics = nextTopics))
                }
            }
            is OnboardingUiEvent.CustomTopicChanged -> {
                _uiState.update { current ->
                    current.copy(
                        data = current.data.copy(customTopic = event.topic),
                    )
                }
            }
            is OnboardingUiEvent.AiSliderChanged -> {
                _uiState.update { current ->
                    val sliderValue = event.value.coerceIn(0f, 1f)
                    current.copy(
                        aiSliderValue = sliderValue,
                        data = current.data.copy(
                            aiLevel = AIAssistanceLevel.fromSlider(sliderValue),
                        ),
                    )
                }
            }
            is OnboardingUiEvent.PasscodeToggled -> {
                _uiState.update { current ->
                    current.copy(
                        data = current.data.copy(enablePasscode = event.enabled),
                    )
                }
            }
            is OnboardingUiEvent.HealthSyncToggled -> {
                _uiState.update { current ->
                    current.copy(
                        data = current.data.copy(syncHealth = event.enabled),
                    )
                }
            }
            is OnboardingUiEvent.NotificationsToggled -> {
                _uiState.update { current ->
                    current.copy(
                        data = current.data.copy(enableNotifications = event.enabled),
                    )
                }
            }
        }
    }

    private fun onNextClicked() {
        if (!_uiState.value.canProceed) {
            return
        }
        navigateForward()
    }

    private fun onSkipClicked() {
        if (_uiState.value.currentStep.isOptional) {
            navigateForward()
        }
    }

    private fun navigateForward() {
        val nextStep = _uiState.value.currentStep.next()
        if (nextStep == null) {
            completeOnboarding()
            return
        }
        _uiState.update { current ->
            current.copy(
                currentStep = nextStep,
                showCustomTimePicker = false,
                showReminderTimePicker = false,
            )
        }
    }

    private fun navigateBackward() {
        val previousStep = _uiState.value.currentStep.previous() ?: return
        _uiState.update { current ->
            current.copy(
                currentStep = previousStep,
                showCustomTimePicker = false,
                showReminderTimePicker = false,
            )
        }
    }

    private fun goToStep(step: OnboardingStep) {
        _uiState.update { current ->
            current.copy(
                currentStep = step,
                showCustomTimePicker = false,
                showReminderTimePicker = false,
            )
        }
    }

    private fun simulateAccountCreation() {
        if (_uiState.value.isLoading) {
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            delay(900L)
            _uiState.update { it.copy(isLoading = false) }
            completeOnboarding()
        }
    }

    private fun completeOnboarding() {
        onboardingRepository.setOnboardingCompleted(true)
        viewModelScope.launch {
            _navEvents.emit(OnboardingNavEvent.NavigateToJournal)
        }
    }
}

sealed interface OnboardingUiEvent {
    object GetStartedClicked : OnboardingUiEvent
    object SignInClicked : OnboardingUiEvent

    object BackClicked : OnboardingUiEvent
    object NextClicked : OnboardingUiEvent
    object SkipClicked : OnboardingUiEvent
    object EditPreferencesClicked : OnboardingUiEvent

    data class MotivationSelected(val motivation: JournalingMotivation) : OnboardingUiEvent
    data class ExperienceLevelSelected(val level: ExperienceLevel) : OnboardingUiEvent
    data class FrequencySelected(val frequency: JournalingFrequency) : OnboardingUiEvent

    data class TimePresetSelected(val preset: TimePreset) : OnboardingUiEvent
    object OpenCustomTimePicker : OnboardingUiEvent
    object DismissCustomTimePicker : OnboardingUiEvent
    data class CustomTimeChanged(val time: java.time.LocalTime) : OnboardingUiEvent

    data class TopicToggled(val topic: JournalTopic) : OnboardingUiEvent
    data class CustomTopicChanged(val topic: String) : OnboardingUiEvent

    data class AiSliderChanged(val value: Float) : OnboardingUiEvent

    data class PasscodeToggled(val enabled: Boolean) : OnboardingUiEvent
    data class HealthSyncToggled(val enabled: Boolean) : OnboardingUiEvent
    data class NotificationsToggled(val enabled: Boolean) : OnboardingUiEvent

    object OpenReminderTimePicker : OnboardingUiEvent
    object DismissReminderTimePicker : OnboardingUiEvent
    data class ReminderTimeChanged(val time: java.time.LocalTime) : OnboardingUiEvent

    object ContinueWithGoogleClicked : OnboardingUiEvent
    object ContinueWithEmailClicked : OnboardingUiEvent
    object SkipAccountClicked : OnboardingUiEvent
}

sealed interface OnboardingNavEvent {
    object NavigateToJournal : OnboardingNavEvent
}
