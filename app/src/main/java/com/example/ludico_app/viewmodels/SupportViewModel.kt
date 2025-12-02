package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.repository.SupportRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SupportUiState(
    val email: String = "",
    val subject: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val submissionSuccess: Boolean = false
)

class SupportViewModel(
    private val supportRepository: SupportRepository,
    private val navViewModel: NavViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) = _uiState.update { it.copy(email = newEmail) }
    fun onSubjectChange(newSubject: String) = _uiState.update { it.copy(subject = newSubject) }
    fun onDescriptionChange(newDesc: String) = _uiState.update { it.copy(description = newDesc) }

    fun submitTicket() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = supportRepository.createTicket(
                    _uiState.value.email,
                    _uiState.value.subject,
                    _uiState.value.description
                )
                    _uiState.update { it.copy(isLoading = false, submissionSuccess = true) }
                    // Optionally navigate back after a short delay
                    kotlinx.coroutines.delay(1000)
                    navViewModel.onNavEvent(com.example.ludico_app.navigation.NavEvent.Back)

            } catch (e: Exception) {
                // Handle network error
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}