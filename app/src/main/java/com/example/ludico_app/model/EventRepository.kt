package com.example.ludico_app.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Simulación de una base de datos en memoria para eventos.
object EventRepository {
    private val _events = MutableStateFlow<Map<String, EventDetailUiState>>(emptyMap())
    val events = _events.asStateFlow()

    fun getEvent(id: String): EventDetailUiState? {
        return _events.value[id]
    }

    fun addEvent(event: EventDetailUiState): String {
        val id = (_events.value.size + 1).toString()
        val newEvent = event.copy(
            id = id,
            comments = emptyList() // Los eventos nuevos no tienen comentarios
        )
        _events.value = _events.value + (id to newEvent)
        return id
    }

    fun updateEvent(event: EventDetailUiState) {
        if (_events.value.containsKey(event.id)) {
            val currentEvents = _events.value.toMutableMap()
            currentEvents[event.id] = event
            _events.value = currentEvents
        }
    }

    fun addComment(eventId: String, author: String, text: String) {
        val event = getEvent(eventId) ?: return

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val newComment = Comment(
            author = author,
            text = text,
            timestamp = "Hoy a las ${sdf.format(Date())}"
        )

        val updatedEvent = event.copy(
            comments = event.comments + newComment
        )

        updateEvent(updatedEvent)
    }

    fun getAllEvents(): List<EventDetailUiState> {
        return _events.value.values.toList()
    }
}
