package com.example.ludico_app.model

import com.example.ludico_app.viewmodels.ProfileTab

data class ProfileUiState(
    val userName: String = "Nit",
    val userLocation: String = "Santiago, Chile",
    val profilePictureUrl: String? = null,
    val gamesPlayed: Int = 5,
    val favoriteGames: List<String> = listOf("Magic The Gathering", "D&D", "Catan"),

    val selectedTab: ProfileTab = ProfileTab.MY_EVENTS,
    val isEditing: Boolean =false,

    val createdEvents: List<String> = listOf("Torneo semanal de Magic.", "Partida novatos D&D"),
    val joinedEvents : List<String> = listOf("Noche de catan en la casa del seba", "Magic con los cabros"),
    val pastEvents: List<String> = listOf("Torneo de Mitos y Leyendas", "Teas sin control")
)
