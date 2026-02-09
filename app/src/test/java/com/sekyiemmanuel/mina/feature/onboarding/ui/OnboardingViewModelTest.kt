package com.sekyiemmanuel.mina.feature.onboarding.ui

import com.sekyiemmanuel.mina.feature.journal.ui.MainDispatcherRule
import com.sekyiemmanuel.mina.feature.onboarding.domain.OnboardingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onboardingStep_requiresSelectionBeforeProceeding() = runTest {
        val viewModel = OnboardingViewModel(FakeOnboardingRepository())

        viewModel.onEvent(OnboardingUiEvent.GetStartedClicked)

        assertEquals(OnboardingStep.WHY_JOURNAL, viewModel.uiState.value.currentStep)
        assertFalse(viewModel.uiState.value.canProceed)

        viewModel.onEvent(OnboardingUiEvent.MotivationSelected(JournalingMotivation.MENTAL_CLARITY))

        assertTrue(viewModel.uiState.value.canProceed)
    }

    @Test
    fun skipOnOptionalStep_movesForward() = runTest {
        val viewModel = OnboardingViewModel(FakeOnboardingRepository())

        moveToPreferredTimeStep(viewModel)

        assertEquals(OnboardingStep.PREFERRED_TIME, viewModel.uiState.value.currentStep)

        viewModel.onEvent(OnboardingUiEvent.SkipClicked)

        assertEquals(OnboardingStep.TOPICS, viewModel.uiState.value.currentStep)
    }

    @Test
    fun skipAccount_completesOnboardingAndEmitsNavigation() = runTest(UnconfinedTestDispatcher()) {
        val repository = FakeOnboardingRepository()
        val viewModel = OnboardingViewModel(repository)

        moveToCreateAccountStep(viewModel)

        assertEquals(OnboardingStep.CREATE_ACCOUNT, viewModel.uiState.value.currentStep)

        val navEvent = async { viewModel.navEvents.first() }
        viewModel.onEvent(OnboardingUiEvent.SkipAccountClicked)
        advanceUntilIdle()

        assertTrue(repository.completed)
        assertEquals(OnboardingNavEvent.NavigateToJournal, navEvent.await())
    }

    private fun moveToPreferredTimeStep(viewModel: OnboardingViewModel) {
        viewModel.onEvent(OnboardingUiEvent.GetStartedClicked)
        viewModel.onEvent(OnboardingUiEvent.MotivationSelected(JournalingMotivation.MENTAL_CLARITY))
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
        viewModel.onEvent(OnboardingUiEvent.ExperienceLevelSelected(ExperienceLevel.NEW_TO_JOURNALING))
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
        viewModel.onEvent(OnboardingUiEvent.FrequencySelected(JournalingFrequency.DAILY))
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
    }

    private fun moveToCreateAccountStep(viewModel: OnboardingViewModel) {
        moveToPreferredTimeStep(viewModel)
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
        viewModel.onEvent(OnboardingUiEvent.NextClicked)
    }
}

private class FakeOnboardingRepository : OnboardingRepository {
    var completed: Boolean = false

    override fun hasCompletedOnboarding(): Boolean = completed

    override fun setOnboardingCompleted(completed: Boolean) {
        this.completed = completed
    }
}
