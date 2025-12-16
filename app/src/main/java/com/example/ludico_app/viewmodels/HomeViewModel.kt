package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    data class HomeUiState(
        val eventList: List<Event> = emptyList(),
        val isLoading: Boolean = true,
        val currentUserId: String? = null
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            val userId = sessionManager.fetchUserId()
            _uiState.update { it.copy(currentUserId = userId) }

            eventRepository.allEvents.collect { events ->
                _uiState.update { it.copy(eventList = events, isLoading = false) }
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
        }
    }
}
