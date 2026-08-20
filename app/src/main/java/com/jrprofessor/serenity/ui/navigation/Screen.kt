package com.jrprofessor.serenity.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object MoodCheckIn : Screen("mood_check_in")
    data object JournalFocus : Screen("journal_focus")
    data object CheckInResult : Screen("check_in_result")
    data object JournalList : Screen("journal_list")
    data object Insights : Screen("insights")
}
