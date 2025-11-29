package com.example.ludico_app.data.dto

import com.example.ludico_app.data.entities.Event

data class EventDto(
    val eventId: String,
    val title: String,
    val description: String,
    val gameType: String,
    val date: String,
    val time: String,
    val location: String,
    val maxParticipants: Int,
    val hostUserId: String
)

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
        hostUserId = this.hostUserId
    )
}
