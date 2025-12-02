package com.example.ludico_app.data.model


/**
 * DTO (Data Transfer Object) para enviar la solicitud de creación de un ticket.
 * Los nombres de las variables deben coincidir con los campos de tu entidad en Spring Boot.
 */
data class CreateTicketRequest(
    val contactEmail: String,
    val subject: String,
    val description: String
    // Nota: No enviamos el 'status' porque es lógico que el backend lo asigne
    // por defecto a "Open" al crear un nuevo ticket.
)

/**
 * DTO para la respuesta que el backend podría enviar tras crear el ticket.
 * Es una buena práctica para confirmar que la operación fue exitosa.
 */
data class TicketResponse(
    val id: Long, // Suponiendo que el backend devuelve el ID del ticket creado.
    val message: String
)