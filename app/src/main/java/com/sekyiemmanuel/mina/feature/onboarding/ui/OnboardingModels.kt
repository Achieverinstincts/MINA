package com.sekyiemmanuel.mina.feature.onboarding.ui

import java.time.LocalTime
import kotlin.math.roundToInt

enum class OnboardingStep(
    val isOptional: Boolean = false,
) {
    WELCOME,
    WHY_JOURNAL,
    EXPERIENCE_LEVEL,
    JOURNALING_GOAL,
    PREFERRED_TIME(isOptional = true),
    TOPICS(isOptional = true),
    AI_ASSISTANCE,
    PRIVACY_SECURITY,
    HEALTH_SYNC(isOptional = true),
    NOTIFICATIONS,
    SETUP_SUMMARY,
    CREATE_ACCOUNT,
    ;

    companion object {
        val totalSteps: Int
            get() = entries.size
    }
}

fun OnboardingStep.next(): OnboardingStep? = OnboardingStep.entries.getOrNull(ordinal + 1)

fun OnboardingStep.previous(): OnboardingStep? = OnboardingStep.entries.getOrNull(ordinal - 1)

enum class JournalingMotivation(
    val title: String,
    val subtitle: String,
) {
    MENTAL_CLARITY(
        title = "Mental clarity",
        subtitle = "Organize thoughts and gain focus",
    ),
    REDUCE_STRESS(
        title = "Reduce stress and anxiety",
        subtitle = "Process emotions and find calm",
    ),
    PERSONAL_GROWTH(
        title = "Personal growth",
        subtitle = "Reflect, learn, and evolve",
    ),
    TRACK_LIFE(
        title = "Track life events",
        subtitle = "Document memories and milestones",
    ),
    CREATIVE_EXPRESSION(
        title = "Creative expression",
        subtitle = "Explore ideas and imagination",
    ),
    ;
}

enum class ExperienceLevel(
    val title: String,
    val subtitle: String,
) {
    NEW_TO_JOURNALING(
        title = "New to journaling",
        subtitle = "I will guide you every step of the way",
    ),
    JOURNALED_BEFORE(
        title = "I have journaled before",
        subtitle = "Great. We will build on your experience",
    ),
    REGULAR_JOURNALER(
        title = "I journal regularly",
        subtitle = "Welcome back. Let us enhance your practice",
    ),
    ;
}

enum class JournalingFrequency(
    val title: String,
    val subtitle: String,
) {
    DAILY(
        title = "Every day",
        subtitle = "Build a daily reflection habit",
    ),
    WEEKDAYS(
        title = "Weekdays",
        subtitle = "5 days a week, weekends off",
    ),
    FEW_TIMES_A_WEEK(
        title = "A few times a week",
        subtitle = "2 to 3 times per week",
    ),
    WEEKLY(
        title = "Once a week",
        subtitle = "A weekly check-in",
    ),
    NO_GOAL(
        title = "No specific goal",
        subtitle = "Write whenever inspiration strikes",
    ),
    ;
}

enum class JournalTopic(
    val title: String,
) {
    GRATITUDE("Gratitude"),
    MINDFULNESS("Mindfulness"),
    DREAMS("Dreams"),
    GOALS("Goals"),
    RELATIONSHIPS("Relationships"),
    CAREER("Career"),
    HEALTH("Health and Wellness"),
    CREATIVITY("Creativity"),
    ;
}

enum class AIAssistanceLevel(
    val title: String,
    val description: String,
    val example: String,
) {
    MINIMAL(
        title = "Minimal",
        description = "I stay quiet unless you ask for help",
        example = "You write freely. AI helps on request.",
    ),
    GENTLE(
        title = "Gentle",
        description = "Occasional prompts when you seem stuck",
        example = "Subtle nudges after long pauses.",
    ),
    BALANCED(
        title = "Balanced",
        description = "Helpful suggestions without being intrusive",
        example = "Daily prompt with gentle suggestions.",
    ),
    ACTIVE(
        title = "Active",
        description = "Regular prompts and writing ideas",
        example = "Multiple prompts with follow-up questions.",
    ),
    FULL(
        title = "Full guidance",
        description = "Comprehensive support and structured writing",
        example = "Guided sessions with exercises each day.",
    ),
    ;

    val sliderValue: Float
        get() = ordinal.toFloat() / (entries.size - 1).toFloat()

    companion object {
        fun fromSlider(sliderValue: Float): AIAssistanceLevel {
            val clamped = sliderValue.coerceIn(0f, 1f)
            val index = (clamped * (entries.size - 1)).roundToInt()
            return entries[index]
        }
    }
}

enum class TimePreset(
    val title: String,
    val subtitle: String,
    val defaultTime: LocalTime,
) {
    MORNING(
        title = "Morning",
        subtitle = "6:00 to 9:00 AM",
        defaultTime = LocalTime.of(7, 0),
    ),
    AFTERNOON(
        title = "Afternoon",
        subtitle = "12:00 to 2:00 PM",
        defaultTime = LocalTime.of(13, 0),
    ),
    EVENING(
        title = "Evening",
        subtitle = "6:00 to 8:00 PM",
        defaultTime = LocalTime.of(19, 0),
    ),
    NIGHT(
        title = "Night",
        subtitle = "9:00 to 11:00 PM",
        defaultTime = LocalTime.of(21, 0),
    ),
    CUSTOM(
        title = "Custom",
        subtitle = "Set your own time",
        defaultTime = LocalTime.of(20, 0),
    ),
    ;
}

data class OnboardingData(
    val motivation: JournalingMotivation? = null,
    val experienceLevel: ExperienceLevel? = null,
    val frequency: JournalingFrequency? = null,
    val preferredTimePreset: TimePreset? = null,
    val preferredTime: LocalTime? = null,
    val topics: Set<JournalTopic> = emptySet(),
    val customTopic: String = "",
    val aiLevel: AIAssistanceLevel = AIAssistanceLevel.BALANCED,
    val enablePasscode: Boolean = false,
    val syncHealth: Boolean = false,
    val enableNotifications: Boolean = true,
    val reminderTime: LocalTime? = null,
) {
    fun aiPersonalitySummary(): String {
        val parts = buildList {
            when (motivation) {
                JournalingMotivation.MENTAL_CLARITY -> add("help organize your thoughts")
                JournalingMotivation.REDUCE_STRESS -> add("support your emotional wellbeing")
                JournalingMotivation.PERSONAL_GROWTH -> add("encourage your personal growth")
                JournalingMotivation.TRACK_LIFE -> add("help document your journey")
                JournalingMotivation.CREATIVE_EXPRESSION -> add("spark your creativity")
                null -> Unit
            }

            when (frequency) {
                JournalingFrequency.DAILY -> add("with daily reflection prompts")
                JournalingFrequency.WEEKDAYS -> add("on weekdays")
                JournalingFrequency.FEW_TIMES_A_WEEK -> add("a few times per week")
                JournalingFrequency.WEEKLY -> add("with weekly check-ins")
                JournalingFrequency.NO_GOAL -> add("whenever you feel ready")
                null -> Unit
            }

            if (topics.isNotEmpty()) {
                val topicList = topics.take(3).joinToString(", ") { it.title.lowercase() }
                add("while focusing on $topicList")
            }
        }

        if (parts.isEmpty()) {
            return "I will help you build a sustainable journaling habit."
        }
        return "I will ${parts.joinToString(" ")}."
    }
}
