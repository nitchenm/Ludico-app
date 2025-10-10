package com.example.ludico_app.navigation

sealed class Routes (val route: String) {
    data object Home : Routes ("home")
    data object Detail : Routes("detail")
    data object Settings : Routes ("settings")
}