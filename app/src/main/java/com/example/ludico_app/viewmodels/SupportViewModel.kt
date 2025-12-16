// Updated SupportViewModel.kt
package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.model.CreateTicketRequest
import com.example.ludico_app.data.model.SupportTicket
import com.example.ludico_app.data.model.UpdateTicketRequest
import com.example.ludico_app.data.repository.SupportRepository  // Assuming this is the interface or alias for SupportTicketRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SupportUiState(
    val email: String = "",
    val subject: String = "",
    val description: String = "",
    val status: String = "Open",
    val currentId: String? = null,
    val tickets: List<SupportTicket> = emptyList(),
    val isLoading: Boolean = false,
    val submissionSuccess: Boolean = false,
    val errorMessage: String? = null
)

class SupportViewModel(
    private val supportRepository: SupportRepository,
    private val navViewModel: NavViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchTickets()
    }

    fun onEmailChange(newEmail: String) = _uiState.update { it.copy(email = newEmail) }
    fun onSubjectChange(newSubject: String) = _uiState.update { it.copy(subject = newSubject) }
    fun onDescriptionChange(newDesc: String) = _uiState.update { it.copy(description = newDesc) }
    fun onStatusChange(newStatus: String) = _uiState.update { it.copy(status = newStatus) }

    fun selectTicketForEdit(ticket: SupportTicket) {
        _uiState.update {
            it.copy(
                email = ticket.contactEmail,
                subject = ticket.subject,
                description = ticket.description,
                status = ticket.status,
                currentId = ticket.id
            )
        }
    }

    fun clearForm() {
        _uiState.update {
            it.copy(
                email = "",
                subject = "",
                description = "",
                status = "Open",
                currentId = null,
                submissionSuccess = false,
                errorMessage = null
            )
        }
    }

    fun submitTicket() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                if (state.currentId == null) {
                    supportRepository.createTicket(

                            state.email,
                            state.subject,
                            state.description

                    )
                } else {
                    supportRepository.updateTicket(
                        state.currentId,
                        UpdateTicketRequest(
                            state.subject,
                            state.description,
                            state.status
                        )
                    ).getOrThrow()
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        submissionSuccess = true,
                        currentId = null,
                        email = "",
                        subject = "",
                        description = "",
                        status = "Open"
                    )
                }
                fetchTickets()
                delay(2000) // Show success message briefly
                _uiState.update { it.copy(submissionSuccess = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteTicket(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                supportRepository.deleteTicket(id).getOrThrow()
                fetchTickets()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error deleting ticket: ${e.message}") }
            }
        }
    }

    private fun fetchTickets() {
        viewModelScope.launch {
            try {
                val tickets = supportRepository.getAllTickets().getOrThrow()
                _uiState.update { it.copy(tickets = tickets) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error fetching tickets: ${e.message}") }
            }
        }
    }
}