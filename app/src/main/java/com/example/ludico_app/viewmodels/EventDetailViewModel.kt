package com.example.ludico_app.viewmodels

import androidx.compose.animation.core.copy
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.model.EventDetailUiState
import com.example.ludico_app.model.RsvpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Obtenemos el eventId de los argumentos de navegación
    private val eventId: String = savedStateHandle.get<String>("eventId")!!

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // aqui usaria el eventId para cargar el evento de una bdd
        println("Cargando detalles para el evento: $eventId")
    }

    fun toggleRsvp() {
        val currentState = _uiState.value.rsvpState
        val newState = if (currentState == RsvpState.JOINED) RsvpState.NOT_JOINED else RsvpState.JOINED

        // Simulación: Actualiza el estado de RSVP.
        _uiState.update { it.copy(rsvpState = newState) }
    }
}