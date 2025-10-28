
package com.example.ludico_app.viewmodels

import android.util.Log
import androidx.activity.result.launch
import androidx.compose.animation.core.copy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.model.CreateEventUiState
import com.example.ludico_app.model.EventDetailUiState
import com.example.ludico_app.model.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class CreateEventViewModel(
    private val eventRepository: EventRepository // <-- CORRECCIÓN: El constructor ahora acepta el repositorio.
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState = _uiState.asStateFlow()

    // --- FUNCIONES PARA ACTUALIZAR EL ESTADO DESDE LA UI ---
    // (Estas funciones no cambian)
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

    // --- LÓGICA DE NEGOCIO (Ahora interactúa con la base de datos) ---

    fun createEvent() {
        if (!validateFields()) {
            return // La validación falló, no continuamos.
        }

        // Lanzamos una corrutina en el scope del ViewModel para la operación de base de datos.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Creamos una instancia de nuestra entidad 'Event' con los datos del formulario.
            val newEvent = Event(
                title = _uiState.value.title,
                description = _uiState.value.description,
                gameType = _uiState.value.gameType,
                date = _uiState.value.date,
                time = _uiState.value.time,
                location = _uiState.value.location,
                maxParticipants = _uiState.value.maxParticipants.toIntOrNull() ?: 0,
                hostUserId = "user_123" // TODO: Reemplazar con el ID del usuario real logueado.
            )

            Log.d("AppDebu", "CreateEventVM: Intentando guardar evento con id : ${newEvent.eventId}")

            // 2. Usamos el repositorio inyectado para guardar el evento en la base de datos.
            eventRepository.insert(newEvent)

            // 3. Actualizamos el estado de la UI para indicar que el proceso ha terminado y fue exitoso.
            _uiState.update { it.copy(isLoading = false, eventCreatedSuccessfully = true) }
        }
    }

    /**
     * Resetea el estado de navegación para evitar que la app navegue hacia atrás
     * automáticamente si la pantalla se recompone.
     */
    fun resetNavigationState() {
        _uiState.update { it.copy(eventCreatedSuccessfully = false, createdEventId = null) }
    }

    /**
     * Valida los campos del formulario.
     * Devuelve 'true' si son válidos, 'false' en caso contrario.
     * Actualiza el 'uiState' con los mensajes de error correspondientes.
     */
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

/**
 * Data class que modela el estado de la UI para CreateEventScreen.
 */
data class CreateEventUiState(
    // Campos del formulario
    val title: String = "",
    val description: String = "",
    val gameType: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val maxParticipants: String = "",

    // Errores de validación
    val titleError: String? = null,
    val descriptionError: String? = null,
    val dateError: String? = null,

    // Estado de la UI
    val isLoading: Boolean = false,
    val eventCreatedSuccessfully: Boolean = false
)
