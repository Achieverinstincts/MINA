package com.sekyiemmanuel.mina.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sekyiemmanuel.mina.R
import com.sekyiemmanuel.mina.feature.gallery.ui.GalleryRoute
import com.sekyiemmanuel.mina.feature.insight.ui.InsightRoute
import com.sekyiemmanuel.mina.feature.inbox.ui.InboxRoute
import com.sekyiemmanuel.mina.feature.journal.ui.JournalRoute
import com.sekyiemmanuel.mina.feature.onboarding.ui.OnboardingRoute
import com.sekyiemmanuel.mina.feature.settings.ui.SettingsScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource

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

    val featureDestinations = listOf(
        FeatureDestination(
            route = AppDestinations.JOURNAL,
            labelRes = R.string.journal,
            icon = Icons.Filled.Book,
        ),
        FeatureDestination(
            route = AppDestinations.GALLERY,
            labelRes = R.string.gallery,
            icon = Icons.Filled.PhotoCamera,
        ),
        FeatureDestination(
            route = AppDestinations.INBOX,
            labelRes = R.string.inbox,
            icon = Icons.Filled.Inbox,
        ),
        FeatureDestination(
            route = AppDestinations.INSIGHT,
            labelRes = R.string.insight,
            icon = Icons.Filled.AutoAwesome,
        ),
    )
    val featureRoutes = featureDestinations.map { it.route }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showFeatureBar = currentDestination?.hierarchy?.any { destination ->
        destination.route in featureRoutes
    } == true

    Scaffold(
        bottomBar = {
            if (showFeatureBar) {
                NavigationBar {
                    featureDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true
                        val label = stringResource(id = destination.labelRes)

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = label,
                                )
                            },
                            label = {
                                Text(text = label)
                            },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues),
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
                    onNavigateToSettings = { navController.navigate(AppDestinations.SETTINGS) },
                )
            }
            composable(AppDestinations.GALLERY) {
                GalleryRoute()
            }
            composable(AppDestinations.INBOX) {
                InboxRoute()
            }
            composable(AppDestinations.INSIGHT) {
                InsightRoute()
            }
            composable(AppDestinations.SETTINGS) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}

private data class FeatureDestination(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
)
