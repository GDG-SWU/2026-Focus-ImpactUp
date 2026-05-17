package com.gdgswu.qos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gdgswu.qos.ui.guide.WhatToDoScreen
import com.gdgswu.qos.ui.home.HomeScreen
import com.gdgswu.qos.ui.splash.SplashScreen
import com.gdgswu.qos.ui.map.MapFilter
import com.gdgswu.qos.ui.map.MapScreen
import com.gdgswu.qos.ui.ocr.OcrScanScreen
import com.gdgswu.qos.ui.onboarding.OnboardingScreen
import com.gdgswu.qos.ui.profile.SettingScreen
import com.gdgswu.qos.ui.translation.SavedPhrasesScreen
import com.gdgswu.qos.ui.translation.SosCardScreen
import com.gdgswu.qos.ui.translation.TranslationScreen

sealed class Screen(val route: String) {
    object Splash        : Screen("splash")
    object Onboarding    : Screen("onboarding")
    object Home          : Screen("home")
    object Map           : Screen("map")
    object Guide         : Screen("guide")          // 번역 카드
    object WhatToDo      : Screen("what_to_do")    // 72h 가이드
    object SosCard       : Screen("sos_card")
    object OcrScan       : Screen("ocr_scan")
    object Setting       : Screen("setting")
    object SavedPhrases  : Screen("saved_phrases")
}

@Composable
fun QOSNavGraph(navController: NavHostController, startDestination: String = Screen.Splash.route) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(
            route = "${Screen.Map.route}?filter={filter}",
            arguments = listOf(navArgument("filter") {
                type = NavType.StringType
                defaultValue = MapFilter.ALL.name
            })
        ) { backStackEntry ->
            val filterName = backStackEntry.arguments?.getString("filter") ?: MapFilter.ALL.name
            val filter = try {
                val f = MapFilter.valueOf(filterName)
                if (f == MapFilter.ALL) null else f
            } catch (e: Exception) { null }
            MapScreen(navController = navController, initialFilter = filter)
        }
        composable(Screen.Guide.route) {
            TranslationScreen(navController = navController)
        }
        composable(Screen.WhatToDo.route) {
            WhatToDoScreen(navController = navController)
        }
        composable(Screen.SosCard.route) {
            SosCardScreen(navController = navController)
        }
        composable(Screen.OcrScan.route) {
            OcrScanScreen(navController = navController)
        }
        composable(Screen.Setting.route) {
            SettingScreen(navController = navController)
        }
        composable(Screen.SavedPhrases.route) {
            SavedPhrasesScreen(navController = navController)
        }
    }
}
