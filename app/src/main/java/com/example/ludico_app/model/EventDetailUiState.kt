package com.example.ludico_app.model

import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.entities.User

// Modelo de datos simple para un participante
data class Participant(val id: String, val name: String, val avatarUrl: String? = null)

// Modelo de datos simple para un comentario
data class Comment(val author: String, val text: String, val timestamp: String)

// Estado completo de la pantalla de detalles
data class EventDetailUiState(
    // El objeto Event completo que viene de la base de datos. Puede ser nulo mientras carga.
    val event: Event? = null,

    // El usuario que creó el evento.
    val host: User? = null,

    // Lista de usuarios que se han unido al evento.
    // La llamaremos 'participants' para que coincida con lo que la pantalla espera.
    // Por ahora será una lista de User, podrías crear un modelo más simple si lo necesitas.
    val participants: List<User> = emptyList(),

    // Lista de comentarios del evento.
    val comments: List<Comment> = emptyList(),

    // Estado que indica si el usuario logueado es el creador.
    val isUserTheCreator: Boolean = false,

    // Estado que indica si el usuario ya se ha unido.
    val rsvpState: RsvpState = RsvpState.NOT_JOINED,

    // Estado para mostrar un indicador de carga.
    val isLoading: Boolean = true
)

enum class RsvpState {
    JOINED, NOT_JOINED, FULL
}
