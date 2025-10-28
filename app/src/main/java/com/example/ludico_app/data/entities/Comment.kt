package com.example.ludico_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Representa la tabla 'comments' en la base de datos.
 */
@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey val commentId: String = UUID.randomUUID().toString(),

    // Clave foránea que relaciona el comentario con un evento específico.
    val eventId: String,

    // Clave foránea que relaciona el comentario con el usuario que lo escribió.
    val authorUserId: String,

    val text: String,

    // Guarda la fecha y hora del comentario en milisegundos para poder ordenarlos fácilmente.
    val timestamp: Long = System.currentTimeMillis()
)