package com.example.ludico_app.navigation

sealed class Routes (val route: String) {
    data object Home : Routes ("home")
    data object Detail : Routes("detail/{eventId}"){
        fun createRoute(eventId: String) ="detail/$eventId"
    }
    data object Settings : Routes ("settings")
    data object Login : Routes ("login")
    data object Register: Routes ("register")

    data object CreateEvent: Routes ("create_event")

    data object Profile : Routes("profile")

    data object Support : Routes("support")
}