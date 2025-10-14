package com.example.ludico_app.viewmodels

import androidx.activity.result.launch
import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.model.CreateEventUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateEventViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState = _uiState.asStateFlow()

    // --- FUNCIONES PARA ACTUALIZAR EL ESTADO DESDE LA UI ---

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

    // --- LÓGICA DE NEGOCIO ---

    fun createEvent() {
        if (!validateFields()) {
            return // Si la validación falla, no continuamos.
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simulación de una llamada a la red o base de datos
            println("Creando evento: ${_uiState.value}")
            delay(2000) // Simula la espera

            // Una vez completado, actualizamos el estado para indicar éxito.
            _uiState.update { it.copy(isLoading = false, eventCreatedSuccessfully = true) }
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