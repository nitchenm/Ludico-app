package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.data.repository.SupportRepository
import com.example.ludico_app.data.repository.UserRepository
import com.example.ludico_app.data.session.SessionManager

class LudicoViewModelFactory(
    private val navViewModel: NavViewModel,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val supportRepository: SupportRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>,extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
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
            modelClass.isAssignableFrom(SupportViewModel::class.java) ->{
                SupportViewModel(supportRepository, navViewModel) as T
            }
            modelClass.isAssignableFrom(EventDetailViewModel::class.java)->{
                EventDetailViewModel(eventRepository, savedStateHandle) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java)->{
                ProfileViewModel(userRepository, sessionManager) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
