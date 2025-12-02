package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.data.entities.Event
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Data class que modela el estado de la UI para HomeScreen.
 */
data class HomeUiState(
    val eventList: List<Event> = emptyList(), // La lista de eventos ahora será de tipo Event.
    val isLoading: Boolean = true // Empezamos en estado de carga.
)

/**
 * ViewModel para la pantalla de inicio.
 * Su principal responsabilidad es obtener y exponer la lista de eventos desde el repositorio.
 *
 * @param eventRepository El repositorio para interactuar con los datos de eventos.
 */
class HomeViewModel(
    eventRepository: EventRepository // Recibe el repositorio inyectado por la fábrica.
) : ViewModel() {

    /**
     * Exponemos el estado de la UI como un StateFlow.
     * Este Flow se actualiza automáticamente cada vez que los datos en la base de datos cambian,
     * gracias a la magia de Room y Flow.
     */
    val uiState: StateFlow<HomeUiState> = eventRepository.allEvents // 1. Observamos el Flow de todos los eventos del repositorio.
        .map { eventList -> // 2. Transformamos la lista de Event en nuestro objeto HomeUiState.
            HomeUiState(eventList = eventList, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // El Flow se activa cuando la UI es visible y se detiene 5s después.
            initialValue = HomeUiState() // Estado inicial mientras se cargan los datos.
        )
}