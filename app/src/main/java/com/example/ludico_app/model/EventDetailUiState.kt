package com.example.ludico_app.model

// Modelo de datos simple para un participante
data class Participant(val id: String, val name: String, val avatarUrl: String? = null)

// Modelo de datos simple para un comentario
data class Comment(val author: String, val text: String, val timestamp: String)

// Estado completo de la pantalla de detalles
data class EventDetailUiState(
    val eventTitle: String = "Torneo de Magic: The Gathering",
    val description: String = "Torneo formato Standard con premios para los primeros 3 lugares. ¡Trae tu mejor mazo y prepárate para la competencia! Se requiere puntualidad.",
    val gameType: String = "TCG",
    val date: String = "Miércoles, 24 septiembre",
    val time: String = "18:00",
    val location: String = "Reino de los duelos",
    val host: String = "Nitch",
    val currentParticipants: Int = 11,
    val maxParticipants: Int = 16,
    val participants: List<Participant> = List(11) { Participant(id = "$it", name = "Jugador ${it + 1}") },
    val comments: List<Comment> = listOf(
        Comment("Carlos", "¡Qué buena iniciativa! Allí estaré.", "Hace 2 horas"),
        Comment("Ana", "¿Hay que pagar inscripción?", "Hace 1 hora")
    ),

    val isUserTheCreator: Boolean = true, // Simulación: el usuario actual es el creador
    val rsvpState: RsvpState = RsvpState.JOINED, // Simulación: el usuario ya se unió

    val isLoading: Boolean = false
)

enum class RsvpState {
    JOINED, NOT_JOINED, FULL
}