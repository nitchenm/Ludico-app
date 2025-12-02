// En: viewmodels/HelpViewModel.kt
package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.model.CreateTicketRequest
import com.example.ludico_app.data.repository.HelpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Clase que representa el estado del formulario en la UI
data class HelpFormState(
    val contactEmail: String = "",
    val subject: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val submissionSuccess: Boolean = false,
    val submissionError: String? = null
)

class HelpViewModel(private val repository: HelpRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HelpFormState())
    val uiState = _uiState.asStateFlow()

    // --- Funciones para que la UI actualice el estado ---
    fun onEmailChange(email: String) { _uiState.update { it.copy(contactEmail = email) } }
    fun onSubjectChange(subject: String) { _uiState.update { it.copy(subject = subject) } }
    fun onDescriptionChange(description: String) { _uiState.update { it.copy(description = description) } }

    // --- Lógica Principal: Enviar el Ticket ---
    fun submitTicket() {
        if (uiState.value.contactEmail.isBlank() || uiState.value.subject.isBlank() || uiState.value.description.isBlank()) {
            _uiState.update { it.copy(submissionError = "Todos los campos son obligatorios.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, submissionError = null) }
            try {
                val request = CreateTicketRequest(
                    contactEmail = uiState.value.contactEmail,
                    subject = uiState.value.subject,
                    description = uiState.value.description
                )
                repository.createTicket(request) // Se realiza la llamada a la API

                // Éxito
                _uiState.update { it.copy(isLoading = false, submissionSuccess = true) }
            } catch (e: Exception) {
                // Error
                _uiState.update { it.copy(isLoading = false, submissionError = "Error al enviar el ticket.") }
            }
        }
    }

    // Función para resetear el estado del formulario, por ejemplo, después de un envío exitoso.
    fun onFormConsumed() {
        _uiState.update { it.copy(submissionSuccess = false, submissionError = null) }
    }
}
    