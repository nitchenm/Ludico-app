package com.example.ludico_app.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ludico_app.LudicoApplication
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.model.EventDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn


class EventDetailViewModel(
    eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String? = savedStateHandle["eventId"]

    init {
        Log.d("AppDebug", "EventDetailVM: viewmodel iniciado. Intentando buscar con id : $eventId")
    }
    val uiState: StateFlow<EventDetailUiState> =
        if (eventId != null) {
            // 1. Obtenemos el Flow del evento.
            eventRepository.getEvent(eventId)
                .filterNotNull() // Nos aseguramos de no continuar si el evento es nulo.
                // 2. Cuando el evento llega, 'flatMapLatest' cambia a un nuevo Flow para obtener el host.
                .flatMapLatest { event ->
                    // 3. Obtenemos el Flow del usuario (host).
                    eventRepository.getUser(event.hostUserId).map { host ->
                        // 4. Cuando el host llega, combinamos todo en el UiState final.
                        Log.d("AppDebug", "EventDetailVM: Host cargado: ${host?.userName}")
                        EventDetailUiState(
                            event = event,
                            host = host, // <-- AHORA PASAMOS EL HOST REAL
                            isUserTheCreator = event.hostUserId == "user_123", // TODO: Usar ID de usuario logueado
                            isLoading = false
                        )
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = EventDetailUiState(isLoading = true)
                )
        } else {
            // Este bloque no cambia
            MutableStateFlow(EventDetailUiState(isLoading = false, event = null))
        }
    fun toggleRsvp() {
        // TODO: Implementar la lógica para unirse/abandonar un evento.
        // Esto implicaría añadir/quitar una fila en una tabla de relación "UserEventCrossRef".
    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Obtenemos el SavedStateHandle y la Application desde los 'extras'
                val savedStateHandle = extras.createSavedStateHandle()
                val application = (extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LudicoApplication)

                return EventDetailViewModel(
                    application.eventRepository,
                    savedStateHandle
                ) as T
            }
        }
    }
}