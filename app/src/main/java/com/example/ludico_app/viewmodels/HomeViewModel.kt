
package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.model.EventDetailUiState
import com.example.ludico_app.model.EventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val events: List<EventDetailUiState> = emptyList()
)

class HomeViewModel : ViewModel() {

    val uiState: StateFlow<HomeUiState> = EventRepository.events
        .map { eventsMap -> HomeUiState(events = eventsMap.values.toList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )
}
