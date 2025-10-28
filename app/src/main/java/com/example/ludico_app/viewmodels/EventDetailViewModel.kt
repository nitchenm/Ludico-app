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

    fun shareEvent(context: Context) {
        // Obtenemos el evento actual del estado de la UI
        val event = uiState.value.event
        if (event == null) {
            Log.w("AppDebug", "ShareEvent: No se puede compartir porque el evento es nulo.")
            return
        }

        // 1. Construimos el mensaje que queremos compartir.
        val shareText = """
            ¡Te invito a un evento de juegos!
            
            Título: ${event.title}
            Juego: ${event.gameType}
            Cuándo: ${event.date} a las ${event.time}
            Dónde: ${event.location}
            
            Descripción: ${event.description}
            
            ¡Descarga Ludico para más detalles!
        """.trimIndent()

        // 2. Creamos un Intent de tipo ACTION_SEND.
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain" // Indicamos que estamos enviando texto plano.
        }

        // 3. Creamos un "chooser" para que el usuario elija la app con la que quiere compartir.
        val shareIntent = Intent.createChooser(sendIntent, "Compartir evento vía...")

        // 4. Lanzamos el Intent desde el contexto.
        // Es importante añadir FLAG_ACTIVITY_NEW_TASK cuando se lanza un Intent desde fuera de una Activity.
        context.startActivity(shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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
        val currentEvent = uiState.value
        val newRsvpState = if (currentEvent.rsvpState == RsvpState.JOINED) RsvpState.NOT_JOINED else RsvpState.JOINED

        val updatedEvent = currentEvent.copy(rsvpState = newRsvpState)
        EventRepository.updateEvent(updatedEvent)
    }
}
