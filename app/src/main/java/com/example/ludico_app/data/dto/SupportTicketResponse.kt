package com.example.ludico_app.data.dto

data class SupportTicketResponse(
    val id: String?,
    val contactEmail: String?,
    val subject: String?,
    val description: String?,
    val status: String?,
    val createdAt: String? = null,
    val updatedAt: String? = null
)