package com.example.ludico_app.data.dto

import com.example.ludico_app.data.entities.Event

// 1. El campo ha sido renombrado a creatorId
data class EventDto(
    val eventId: String,
    val title: String,
    val description: String,
    val gameType: String,
    val date: String,
    val time: String,
    val location: String,
    val maxParticipants: Int,
    val creatorId: String // <-- CAMBIADO
)

// 2. La función de mapeo ahora también usa creatorId
fun Event.toEventDto(): EventDto {
    return EventDto(
        eventId = this.eventId,
        title = this.title,
        description = this.description,
        gameType = this.gameType,
        date = this.date,
        time = this.time,
        location = this.location,
        maxParticipants = this.maxParticipants,
        creatorId = this.creatorId // <-- CAMBIADO
    )
}
