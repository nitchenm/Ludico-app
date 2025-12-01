package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.data.repository.UserRepository
import com.example.ludico_app.data.session.SessionManager

class LudicoViewModelFactory(
    private val navViewModel: NavViewModel,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(navViewModel, userRepository, sessionManager) as T
            }
            modelClass.isAssignableFrom(CreateEventViewModel::class.java) -> {
                CreateEventViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(eventRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
