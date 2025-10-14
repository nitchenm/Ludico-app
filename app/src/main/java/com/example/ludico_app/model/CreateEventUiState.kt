package com.example.ludico_app.model

data class CreateEventUiState(
    val title: String = "",
    val description: String = "",
    val gameType: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val maxParticipants: String = "",

    // Errores
    val titleError: String? = null,
    val descriptionError: String? = null,
    val dateError: String? = null,

    // Estado general de la UI
    val isLoading: Boolean = false,         // Para mostrar un spinner al enviar el formulario
    val eventCreatedSuccessfully: Boolean = false // Para saber cuándo navegar hacia atrás
)