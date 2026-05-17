package com.gdgswu.qos.ui.guide

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.gdgswu.qos.ui.navigation.Screen

data class TutorialStep(
    val hint: String,
    val targetXFraction: Float,   // 0..1  (fraction of screen width)
    val targetYFraction: Float,   // 0..1  (fraction of screen height)
    val navigateTo: String? = null
)

object TutorialState {
    var activeMission by mutableStateOf<Mission?>(null)
        private set
    var currentStepIndex by mutableStateOf(0)
        private set

    /** missions that the user has fully completed (tutorial done) */
    val completedMissionIds = mutableStateListOf<Int>()

    val isActive get() = activeMission != null

    // ── Step definitions per mission ID ─────────────────────────────────────
    // X/Y fractions are approximate for a typical phone screen.
    // Bottom nav bar sits at ~92-96% height.  Filter row ~16-18%.
    private val stepMap: Map<Int, List<TutorialStep>> = mapOf(
        1 to listOf(   // Find safe shelter
            TutorialStep("Open the map",                        0.32f, 0.935f, "${Screen.Map.route}?filter=CAMP"),
            TutorialStep("Tap the shelter filter",              0.32f, 0.175f),
            TutorialStep("Check the nearest available shelter", 0.50f, 0.760f)
        ),
        2 to listOf(   // Find clean water
            TutorialStep("Open the map",                  0.32f, 0.935f, "${Screen.Map.route}?filter=WATER"),
            TutorialStep("Tap the water filter",           0.68f, 0.175f),
            TutorialStep("Go to the nearest water point",  0.50f, 0.760f)
        ),
        3 to listOf(   // Ask for local support
            TutorialStep("Open translation cards",              0.62f, 0.935f, Screen.Guide.route),
            TutorialStep("Choose 'Ask for help'",               0.50f, 0.560f),
            TutorialStep("Show the card to a local person",     0.50f, 0.560f)
        ),
        4 to listOf(   // Register health info
            TutorialStep("Go to Settings",                  0.88f, 0.935f, Screen.Setting.route),
            TutorialStep("Enter allergies & conditions",     0.50f, 0.450f),
            TutorialStep("Save your health profile",         0.50f, 0.750f)
        ),
        5 to listOf(   // Scan medicine label
            TutorialStep("Open OCR scanner",               0.50f, 0.175f, Screen.OcrScan.route),
            TutorialStep("Point camera at medicine label", 0.50f, 0.480f),
            TutorialStep("Check for allergy warnings",     0.50f, 0.380f)
        )
    )

    fun steps(): List<TutorialStep> = activeMission?.let { stepMap[it.id] } ?: emptyList()
    fun currentStep(): TutorialStep? = steps().getOrNull(currentStepIndex)

    /** Begin a mission's tutorial.  No-op if already completed. */
    fun start(mission: Mission) {
        if (!completedMissionIds.contains(mission.id)) {
            activeMission = mission
            currentStepIndex = 0
        }
    }

    /**
     * Advance to the next step.
     * If this was the last step the mission is marked complete and the tutorial ends.
     */
    fun advanceStep() {
        val allSteps = steps()
        if (currentStepIndex < allSteps.size - 1) {
            currentStepIndex++
        } else {
            activeMission?.id?.let { id ->
                if (!completedMissionIds.contains(id)) completedMissionIds.add(id)
            }
            activeMission = null
            currentStepIndex = 0
        }
    }

    fun cancel() {
        activeMission = null
        currentStepIndex = 0
    }
}
