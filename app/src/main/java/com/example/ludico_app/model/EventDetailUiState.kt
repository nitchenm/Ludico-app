package com.example.ludico_app.model

// Modelo de datos simple para un participante
data class Participant(val id: String, val name: String, val avatarUrl: String? = null)

// Modelo de datos simple para un comentario
data class Comment(val author: String, val text: String, val timestamp: String)

// Estado completo de la pantalla de detalles
data class EventDetailUiState(
    val id: String = "",
    val eventTitle: String = "",
    val description: String = "",
    val gameType: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val host: String = "Nitch",
    val currentParticipants: Int = 0,
    val maxParticipants: Int = 0,
    val participants: List<Participant> = emptyList(),
    val comments: List<Comment> = emptyList(),

    val isUserTheCreator: Boolean = true, // Simulación: el usuario actual es el creador
    val rsvpState: RsvpState = RsvpState.NOT_JOINED,

    val isLoading: Boolean = false,

    // Campo para el nuevo comentario que se está escribiendo
    val newCommentText: String = ""
)

enum class RsvpState {
    JOINED, NOT_JOINED, FULL
}
