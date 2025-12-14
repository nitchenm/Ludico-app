package com.example.ludico_app.data.model
import com.example.ludico_app.data.dto.SupportTicketResponse


data class SupportTicket(
    // CORRECCIÓN: El ID debe ser un tipo específico, como String.
    val id: String,
    val contactEmail: String,
    val subject: String,
    val description: String,
    val status: String
)

data class CreateTicketRequest(
    val contactEmail: String,
    val subject: String,
    val description: String
)

data class UpdateTicketRequest(
    val subject: String,
    val description: String,
    val status: String
)

fun SupportTicketResponse.toDomain(): SupportTicket {
    return SupportTicket(
        id = this.id ?: "", // Si el id es nulo, usamos un valor por defecto (string vacío)
        contactEmail = this.contactEmail ?: "Sin email",
        subject = this.subject ?: "Sin asunto",
        description = this.description ?: "Sin descripción",
        status = this.status ?: "DESCONOCIDO"
    )
}


fun List<SupportTicketResponse>.toDomainList(): List<SupportTicket> {
    return this.map { it.toDomain() }
}