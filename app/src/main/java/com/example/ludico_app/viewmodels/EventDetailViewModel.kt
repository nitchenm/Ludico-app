package com.example.ludico_app.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.model.EventDetailUiState
import com.example.ludico_app.model.EventRepository
import com.example.ludico_app.model.RsvpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EventDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId")!!

    private val _newCommentText = MutableStateFlow("")

    val uiState: StateFlow<EventDetailUiState> = combine(
        EventRepository.events.map { it[eventId] ?: EventDetailUiState() },
        _newCommentText
    ) { event, newComment ->
        event.copy(newCommentText = newComment)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EventDetailUiState()
    )

    fun onNewCommentChange(text: String) {
        _newCommentText.value = text
    }

    fun submitComment() {
        if (_newCommentText.value.isNotBlank()) {
            EventRepository.addComment(
                eventId = eventId,
                author = "Usuario Actual", // TODO: Reemplazar con el nombre del usuario real
                text = _newCommentText.value
            )
            // Limpiar el campo después de enviar
            _newCommentText.value = ""
        }
    }

    fun toggleRsvp() {
        val currentEvent = uiState.value
        val newRsvpState = if (currentEvent.rsvpState == RsvpState.JOINED) RsvpState.NOT_JOINED else RsvpState.JOINED

        val updatedEvent = currentEvent.copy(rsvpState = newRsvpState)
        EventRepository.updateEvent(updatedEvent)
    }
}
