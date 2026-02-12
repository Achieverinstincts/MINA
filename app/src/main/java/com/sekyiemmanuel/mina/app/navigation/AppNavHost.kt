package com.sekyiemmanuel.mina.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.sekyiemmanuel.mina.core.ui.theme.AccentFlame

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
            selectedIcon = Icons.Filled.MenuBook,
            unselectedIcon = Icons.Outlined.MenuBook,
        ),
        FeatureDestination(
            route = AppDestinations.GALLERY,
            labelRes = R.string.gallery,
            selectedIcon = Icons.Filled.Collections,
            unselectedIcon = Icons.Outlined.Collections,
        ),
        FeatureDestination(
            route = AppDestinations.INBOX,
            labelRes = R.string.inbox,
            selectedIcon = Icons.Filled.Inbox,
            unselectedIcon = Icons.Outlined.Inbox,
        ),
        FeatureDestination(
            route = AppDestinations.INSIGHT,
            labelRes = R.string.insight,
            selectedIcon = Icons.Filled.ShowChart,
            unselectedIcon = Icons.Outlined.ShowChart,
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
                Surface(color = Color.White) {
                    Column {
                        HorizontalDivider(
                            color = Color(0xFFD9D5D1),
                            thickness = 1.dp,
                        )
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 0.dp,
                            modifier = Modifier.height(78.dp),
                        ) {
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
                                            imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                            contentDescription = label,
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    },
                                    alwaysShowLabel = true,
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = AccentFlame,
                                        selectedTextColor = AccentFlame,
                                        unselectedIconColor = Color(0xFFAAA7AF),
                                        unselectedTextColor = Color(0xFFAAA7AF),
                                        indicatorColor = Color.Transparent,
                                    ),
                                )
                            }
                        }
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
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)
