package com.example.ludico_app.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.data.session.SessionManager
import com.example.ludico_app.model.CreateEventUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CreateEventViewModel(
    private val eventRepository: EventRepository,
    private val savedStateHandle: SavedStateHandle,
    private val sessionManager: SessionManager
) : ViewModel() {

    var isEditMode = false
        private set // The UI can read this, but only the ViewModel can set it.
    private var currentEventId: String? = null

    init {
        savedStateHandle.get<String>("eventId")?.let { eventId ->
            if (eventId.isNotEmpty()) {
                isEditMode = true
                currentEventId = eventId
                loadEventForEditing(eventId)
            }
        }
    }

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState = _uiState.asStateFlow()

    // --- UI STATE UPDATE FUNCTIONS ---
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


    private fun loadEventForEditing(eventId: String) {
        viewModelScope.launch {
            // Use first() if getEvent returns a Flow
            val event = eventRepository.getEvent(eventId).first()
            event?.let {
                _uiState.update { currentState ->
                    currentState.copy(
                        title = event.title,
                        description = event.description,
                        gameType = event.gameType,
                        date = event.date,
                        time = event.time,
                        location = event.location,
                        maxParticipants = event.maxParticipants.toString()
                    )
                }
            }
        }
    }

    fun saveEvent() {
        Log.d("AppDebug", "Paso 1: Se presionó el botón Guardar")
        if (!validateFields()) {
            Log.e("AppDebug", "ERROR: La validación de campos falló")
            return
        }

        viewModelScope.launch {
            Log.d("AppDebug", "Paso 2: Iniciando Coroutine y Loading")
            _uiState.update { it.copy(isLoading = true) }

            try{
                val userId = sessionManager.fetchUserId() // Fetch real ID
                Log.d("AppDebug", "Paso 3: UserId obtenido: '$userId'")
                if(userId.isNullOrBlank()){
                    Log.e("AppDebug", "ERROR FATAL: fetchUserId devolvió NULL. El usuario no parece estar logueado.")
                    _uiState.update { it.copy(isLoading = false) } // <--- APAGAMOS LA RUEDA
                    return@launch
                }
                if (isEditMode) {
                    val updatedEvent = Event(
                        eventId = currentEventId!!,
                        title = _uiState.value.title,
                        description = _uiState.value.description,
                        gameType = _uiState.value.gameType,
                        date = _uiState.value.date,
                        time = _uiState.value.time,
                        location = _uiState.value.location,
                        maxParticipants = _uiState.value.maxParticipants.toIntOrNull() ?: 0,
                        creatorId = userId
                    )
                    eventRepository.updateEvent(updatedEvent)
            } else {
                Log.d("AppDebug", "Paso 4: Creando objeto Event para enviar")
                val newEvent = Event(
                    title = _uiState.value.title,
                    description = _uiState.value.description,
                    gameType = _uiState.value.gameType,
                    date = _uiState.value.date,
                    time = _uiState.value.time,
                    location = _uiState.value.location,
                    maxParticipants = _uiState.value.maxParticipants.toIntOrNull() ?: 0,
                    creatorId = userId
                )
                eventRepository.insert(newEvent)
                Log.d("AppDebug", "Paso 5: Llamando a eventRepository.insert()")
            }
                _uiState.update { it.copy(isLoading = false, eventCreatedSuccessfully = true) }
            } catch (e: Exception) {
                Log.e("AppDebug", "CRITICAL FAILURE in saveEvent", e)
                _uiState.update { it.copy(isLoading = false) } // Also add this to stop the spinner
            }
        }
    }

    fun resetNavigationState() {
        _uiState.update { it.copy(eventCreatedSuccessfully = false) }
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
