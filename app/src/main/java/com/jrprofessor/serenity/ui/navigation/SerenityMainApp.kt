package com.jrprofessor.serenity.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jrprofessor.serenity.ui.components.NavTab
import com.jrprofessor.serenity.ui.components.SerenityBottomNavBar
import com.jrprofessor.serenity.ui.screens.home.HomeScreen
import com.jrprofessor.serenity.ui.screens.insights.InsightsScreen
import com.jrprofessor.serenity.ui.screens.insights.InsightsViewModel
import com.jrprofessor.serenity.ui.screens.journal.JournalListScreen
import com.jrprofessor.serenity.ui.screens.mood.CheckInResultScreen
import com.jrprofessor.serenity.ui.screens.mood.JournalScreen
import com.jrprofessor.serenity.ui.screens.mood.MoodCheckInScreen
import com.jrprofessor.serenity.ui.screens.mood.MoodViewModel
import com.jrprofessor.serenity.ui.theme.serenityBackground

@Composable
fun SerenityMainApp(
    moodViewModel: MoodViewModel,
    insightsViewModel: InsightsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTab = when (currentRoute) {
        Screen.Home.route -> NavTab.HOME
        Screen.MoodCheckIn.route, Screen.JournalFocus.route, Screen.CheckInResult.route -> NavTab.MOOD
        Screen.JournalList.route -> NavTab.JOURNAL
        Screen.Insights.route -> NavTab.INSIGHTS
        else -> NavTab.HOME
    }

    // Hide bottom bar only during full-focus writing mode (Screen 2)
    val showBottomBar = currentRoute != Screen.JournalFocus.route

    Box(modifier = Modifier.serenityBackground()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            // Home Dashboard
            composable(Screen.Home.route) {
                HomeScreen(
                    insightsViewModel = insightsViewModel,
                    onStartCheckIn = {
                        moodViewModel.resetCheckIn()
                        navController.navigate(Screen.MoodCheckIn.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToInsights = {
                        navController.navigate(Screen.Insights.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Screen 1: Mood Check-In
            composable(Screen.MoodCheckIn.route) {
                MoodCheckInScreen(
                    viewModel = moodViewModel,
                    onNavigateToJournal = {
                        navController.navigate(Screen.JournalFocus.route)
                    }
                )
            }

            // Screen 2: Journal Focus Mode (No bottom nav)
            composable(Screen.JournalFocus.route) {
                JournalScreen(
                    viewModel = moodViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToResult = {
                        navController.navigate(Screen.CheckInResult.route) {
                            popUpTo(Screen.MoodCheckIn.route)
                        }
                    }
                )
            }

            // Screen 3: Check-In Result (AI Analysis)
            composable(Screen.CheckInResult.route) {
                CheckInResultScreen(
                    viewModel = moodViewModel,
                    onSaveSuccess = {
                        moodViewModel.resetCheckIn()
                        navController.navigate(Screen.Insights.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Journal Archives Tab
            composable(Screen.JournalList.route) {
                JournalListScreen(
                    viewModel = insightsViewModel
                )
            }

            // Screen 4: Insights & History ("Your Journey")
            composable(Screen.Insights.route) {
                InsightsScreen(
                    viewModel = insightsViewModel
                )
            }
        }

        // Floating Bottom Navigation Bar
        AnimatedVisibility(
            visible = showBottomBar,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            SerenityBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    val targetRoute = when (tab) {
                        NavTab.HOME -> Screen.Home.route
                        NavTab.MOOD -> Screen.MoodCheckIn.route
                        NavTab.JOURNAL -> Screen.JournalList.route
                        NavTab.INSIGHTS -> Screen.Insights.route
                    }

                    if (currentRoute != targetRoute) {
                        if (targetRoute == Screen.Home.route) {
                            val popped = navController.popBackStack(Screen.Home.route, inclusive = false)
                            if (!popped) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            navController.navigate(targetRoute) {
                                popUpTo(Screen.Home.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }
    }
}
