package com.example.ludico_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla 'users' en la base de datos.
 * Cada instancia de esta clase es una fila en la tabla.
 */
@Entity(tableName = "users")
data class User(
    // La clave primaria. Debería ser única para cada usuario.
    // Usar el ID de un servicio de autenticación (como Firebase Auth) es ideal.
    @PrimaryKey val userId: String,

    val userName: String,
    val email: String,
    val location: String?, // Puede ser nulo si el usuario no lo ha establecido.
    val profilePictureUrl: String? // También puede ser nulo.
)