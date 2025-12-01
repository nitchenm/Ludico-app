package com.example.ludico_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val eventId: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val gameType: String,
    val date: String,
    val time: String,
    val location: String,
    val maxParticipants: Int,
    val creatorId: String, // <-- CAMBIADO
    var isSynced: Boolean = false
)
