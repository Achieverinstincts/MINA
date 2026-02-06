package com.sekyiemmanuel.mina.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sekyiemmanuel.mina.feature.journal.ui.JournalRoute
import com.sekyiemmanuel.mina.feature.settings.ui.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.JOURNAL,
    ) {
        composable(AppDestinations.JOURNAL) {
            JournalRoute(
                onNavigateToSettings = { navController.navigate(AppDestinations.SETTINGS) },
            )
        }
        composable(AppDestinations.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}

