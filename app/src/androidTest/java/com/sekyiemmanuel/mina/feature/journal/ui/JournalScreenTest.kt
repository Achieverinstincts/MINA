package com.sekyiemmanuel.mina.feature.journal.ui

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNode
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.sekyiemmanuel.mina.MainActivity
import org.junit.Rule
import org.junit.Test

class JournalScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun screenRenders_emptyStateAndBottomNavigation() {
        composeRule.onNodeWithText("Upload your mind...").assertExists()
        composeRule.onNodeWithText("Journal").assertExists()
        composeRule.onNodeWithText("Gallery").assertExists()
        composeRule.onNodeWithText("Inbox").assertExists()
        composeRule.onNodeWithText("Insights").assertExists()
    }

    @Test
    fun tappingEntryPrompt_showsTypingActionRow() {
        composeRule.onNodeWithText("Upload your mind...").performClick()
        composeRule.onNodeWithContentDescription("Voice input").assertExists()
        composeRule.onNodeWithContentDescription("Camera input").assertExists()
        composeRule.onNodeWithContentDescription("Add input").assertExists()
        composeRule.onNodeWithContentDescription("Show keyboard options").assertExists()
    }

    @Test
    fun micClick_showsVoiceRecordingBarWithConfirmAndCancel() {
        composeRule.onNodeWithText("Upload your mind...").performClick()
        composeRule.onNodeWithContentDescription("Voice input").performClick()

        composeRule.onNodeWithContentDescription("Voice recording waveform").assertExists()
        composeRule.onNodeWithContentDescription("Accept voice recording").assertExists()
        composeRule.onNodeWithContentDescription("Discard voice recording").assertExists()
    }

    @Test
    fun datePillClick_opensDatePickerDialog() {
        composeRule.onNodeWithContentDescription("Select journal date").performClick()
        composeRule.onNodeWithText("Confirm").assertExists()
    }

    @Test
    fun settingsClick_navigatesToSettingsScreen() {
        composeRule.onNodeWithContentDescription("Open settings").performClick()
        composeRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun galleryTabClick_navigatesToGalleryScreen() {
        clickBottomTab("Gallery")
        composeRule.onNodeWithText("Gallery").assertExists()
    }

    @Test
    fun inboxTabClick_navigatesToInboxScreen() {
        clickBottomTab("Inbox")
        composeRule.onNodeWithText("Inbox").assertExists()
    }

    @Test
    fun insightTabClick_navigatesToInsightScreen() {
        clickBottomTab("Insights")
        composeRule.onNodeWithText("Insight").assertExists()
    }

    @Test
    fun accessibility_descriptionsExistForActionableElements() {
        composeRule.onNodeWithContentDescription("Select journal date").assertExists()
        composeRule.onNodeWithContentDescription("Open settings").assertExists()
        composeRule.onNodeWithContentDescription("Daily streak").assertExists()
    }

    private fun clickBottomTab(label: String) {
        composeRule
            .onNode(
                matcher = hasText(label) and hasClickAction(),
                useUnmergedTree = true,
            )
            .performClick()
    }
}
