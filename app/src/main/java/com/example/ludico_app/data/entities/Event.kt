package com.example.ludico_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Representa la tabla 'events' en la base de datos.
 */@Entity(tableName = "events")
data class Event(
    // Clave primaria que se autogenera con un ID único universal.
    @PrimaryKey val eventId: String = UUID.randomUUID().toString(),

    val title: String,
    val description: String,
    val gameType: String,
    val date: String,
    val time: String,
    val location: String,
    val maxParticipants: Int,

    // Clave foránea (Foreign Key) que relaciona este evento con el usuario que lo creó.
    val hostUserId: String
)