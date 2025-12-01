package com.example.ludico_app.data.dto

data class BackendEventDto(
    val eventId: String,
    val title: String,    val description: String,
    val gameType: String,
    val date: String,
    val time: String,
    val location: String,
    val maxParticipants: Int,
    val creatorId: String, // <-- CAMBIADO
    val createdAt: String // Assuming the backend serializes LocalDateTime to a String

)
