package com.example.ludico_app.viewmodels

import android.content.Context
import android.content.Intent
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
            eventRepository.getEvent(eventId)
                .filterNotNull()
                .flatMapLatest { event ->
                    eventRepository.getUser(event.creatorId).map { host ->
                        Log.d("AppDebug", "EventDetailVM: Host cargado: ${host?.userName}")
                        EventDetailUiState(
                            event = event,
                            host = host,
                            isUserTheCreator = event.creatorId == "1", // TODO: Usar ID de usuario logueado
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
            MutableStateFlow(EventDetailUiState(isLoading = false, event = null))
        }
    fun toggleRsvp() {
        // TODO: Implementar la lógica para unirse/abandonar un evento.
    }

    fun shareEvent(context: Context) {
        val event = uiState.value.event
        if (event == null) {
            Log.w("AppDebug", "ShareEvent: No se puede compartir porque el evento es nulo.")
            return
        }

        val shareText = """
            ¡Te invito a un evento de juegos!
            
            Título: ${event.title}
            Juego: ${event.gameType}
            Cuándo: ${event.date} a las ${event.time}
            Dónde: ${event.location}
            
            Descripción: ${event.description}
            
            ¡Descarga Ludico para más detalles!
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Compartir evento vía...")

        context.startActivity(shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
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
