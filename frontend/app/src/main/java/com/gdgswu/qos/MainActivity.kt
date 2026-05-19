package com.gdgswu.qos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gdgswu.qos.ui.guide.TutorialOverlay
import com.gdgswu.qos.ui.guide.TutorialState
import com.gdgswu.qos.ui.navigation.BottomNavBar
import com.gdgswu.qos.ui.navigation.QOSNavGraph
import com.gdgswu.qos.ui.navigation.Screen
import com.gdgswu.qos.ui.theme.QOSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QOSTheme {
                val navController = rememberNavController()
                val navBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStack?.destination?.route

                val showBottomBar = currentRoute != Screen.Splash.route
                        && currentRoute != Screen.Onboarding.route
                        && currentRoute != Screen.OcrScan.route

                // Wrap everything in a Box so the tutorial overlay can sit above the Scaffold
                // (including the bottom navigation bar)
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) BottomNavBar(navController = navController)
                        }
                    ) { _ ->
                        QOSNavGraph(navController = navController)
                    }

                    // Tutorial overlay — shown on top of EVERYTHING (including bottom nav)
                    if (TutorialState.isActive) {
                        val step = TutorialState.currentStep()
                        if (step != null) {
                            TutorialOverlay(
                                step = step,
                                stepIndex = TutorialState.currentStepIndex,
                                totalSteps = TutorialState.steps().size,
                                onStepTapped = {
                                    // Navigate first (if this step requires it), then advance
                                    step.navigateTo?.let { route ->
                                        navController.navigate(route) {
                                            launchSingleTop = true
                                        }
                                    }
                                    TutorialState.advanceStep()
                                },
                                onCancel = { TutorialState.cancel() }
                            )
                        }
                    }
                }
            }
        }
    }
}
