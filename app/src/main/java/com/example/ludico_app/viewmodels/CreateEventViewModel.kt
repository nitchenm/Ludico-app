
package com.example.ludico_app.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.model.CreateEventUiState
import com.example.ludico_app.model.EventDetailUiState
import com.example.ludico_app.model.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateEventViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val eventId: String? = savedStateHandle.get<String>("eventId")

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (eventId != null) {
            loadEventData(eventId)
        }
    }

    private fun loadEventData(id: String) {
        val event = EventRepository.getEvent(id)
        if (event != null) {
            _uiState.update {
                it.copy(
                    title = event.eventTitle,
                    description = event.description,
                    gameType = event.gameType,
                    date = event.date,
                    time = event.time,
                    location = event.location,
                    maxParticipants = event.maxParticipants.toString(),
                    isEditing = true
                )
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, titleError = null) }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription, descriptionError = null) }
    }

    fun onGameTypeChange(newGameType: String) {
        _uiState.update { it.copy(gameType = newGameType) }
    }

    fun onDateChange(newDate: String) {
        _uiState.update { it.copy(date = newDate, dateError = null) }
    }

    fun onTimeChange(newTime: String) {
        _uiState.update { it.copy(time = newTime) }
    }

    fun onLocationChange(newLocation: String) {
        _uiState.update { it.copy(location = newLocation) }
    }

    fun onMaxParticipantsChange(newMax: String) {
        _uiState.update { it.copy(maxParticipants = newMax) }
    }

    fun saveEvent() {
        if (!validateFields()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentState = _uiState.value
            val event = EventDetailUiState(
                id = eventId ?: "", // Si es nuevo, el repo le asignará un id
                eventTitle = currentState.title,
                description = currentState.description,
                gameType = currentState.gameType,
                date = currentState.date,
                time = currentState.time,
                location = currentState.location,
                maxParticipants = currentState.maxParticipants.toIntOrNull() ?: 0,
            )

            if (currentState.isEditing) {
                EventRepository.updateEvent(event)
            } else {
                EventRepository.addEvent(event)
            }

            _uiState.update { it.copy(isLoading = false, eventCreatedSuccessfully = true) }
        }
    }

    fun resetNavigationState() {
        _uiState.update { it.copy(eventCreatedSuccessfully = false, createdEventId = null) }
    }

    private fun validateFields(): Boolean {
        val currentState = _uiState.value
        val titleValid = currentState.title.isNotBlank() && currentState.title.length > 5
        val descriptionValid = currentState.description.isNotBlank()
        val dateValid = currentState.date.isNotBlank()

        _uiState.update {
            it.copy(
                titleError = if (!titleValid) "El título debe tener más de 5 caracteres." else null,
                descriptionError = if (!descriptionValid) "La descripción no puede estar vacía." else null,
                dateError = if (!dateValid) "Debes seleccionar una fecha." else null
            )
        }

        return titleValid && descriptionValid && dateValid
    }
}
