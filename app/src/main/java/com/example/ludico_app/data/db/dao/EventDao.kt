package com.example.ludico_app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.entities.User
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la entidad Event.
 * Aquí se definen todas las consultas a la base de datos para los eventos.
 */
@Dao
interface EventDao {
    /**
     * Inserta un evento. Si el evento ya existe (misma eventId), lo reemplaza.
     * 'suspend' indica que debe ser llamada desde una corrutina para no bloquear el hilo principal.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    /**
     * Obtiene un evento específico por su ID.
     * Devuelve un Flow, lo que significa que la UI se actualizará automáticamente si los datos de este evento cambian.
     */
    @Query("SELECT * FROM events WHERE eventId = :id")
    fun getEvent(id: String?): Flow<Event?>

    /**
     * Obtiene todos los eventos de la base de datos, ordenados por fecha descendente.
     * El Flow notificará a la UI cada vez que se inserte, actualice o elimine un evento.
     */
    @Query("SELECT * FROM events ORDER BY date DESC")
    fun getAllEvents(): Flow<List<Event>>

    /**
     * Obtiene todos los eventos creados por un usuario específico.
     */
    @Query("SELECT * FROM events WHERE hostUserId = :userId ORDER BY date DESC")
    fun getEventsCreatedByUser(userId: String): Flow<List<Event>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow<User?> // Usamos Flow para que se actualice si el usuario cambia
}