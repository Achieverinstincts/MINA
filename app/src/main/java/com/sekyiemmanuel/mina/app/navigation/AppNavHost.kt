package com.sekyiemmanuel.mina.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.CircularProgressIndicator
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sekyiemmanuel.mina.feature.gallery.ui.GalleryRoute
import com.sekyiemmanuel.mina.feature.journal.ui.JournalRoute
import com.sekyiemmanuel.mina.feature.onboarding.ui.OnboardingRoute
import com.sekyiemmanuel.mina.feature.settings.ui.SettingsScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AppNavHost(
    viewModel: AppNavViewModel = hiltViewModel(),
) {
    val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    if (startDestination.isBlank()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(AppDestinations.ONBOARDING) {
            OnboardingRoute(
                onOnboardingCompleted = {
                    navController.navigate(AppDestinations.JOURNAL) {
                        popUpTo(AppDestinations.ONBOARDING) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppDestinations.JOURNAL) {
            JournalRoute(
                onNavigateToGallery = { navController.navigate(AppDestinations.GALLERY) },
                onNavigateToSettings = { navController.navigate(AppDestinations.SETTINGS) },
            )
        }
        composable(AppDestinations.GALLERY) {
            GalleryRoute(
                onBackClick = { navController.popBackStack() },
            )
        }
        composable(AppDestinations.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
