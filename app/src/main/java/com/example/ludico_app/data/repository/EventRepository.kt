package com.example.ludico_app.data.repository

import com.example.ludico_app.data.db.dao.EventDao
import com.example.ludico_app.data.db.dao.UserDao
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.entities.User
import kotlinx.coroutines.flow.Flow

/**
 * El Repositorio actúa como una capa de abstracción entre los ViewModels y las fuentes de datos.
 * Su trabajo es obtener los datos (desde la base de datos o una red) y proveerlos de forma limpia.
 */
class EventRepository(private val eventDao: EventDao, private val userDao: UserDao) {

    /**
     * Expone un Flow con la lista de todos los eventos.
     * El ViewModel se suscribirá a este Flow para obtener actualizaciones en tiempo real.
     */
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    /**
     * Obtiene un solo evento por su ID.
     */
    fun getEvent(eventId: String?): Flow<Event?> {
        return eventDao.getEvent(eventId)
    }

    /**
     * Inserta un evento en la base de datos a través de una corrutina.
     * Esta es una función 'suspend' porque las operaciones de escritura en disco deben ser asíncronas.
     */
    suspend fun insert(event: Event) {
        eventDao.insertEvent(event)
    }

    fun getUser(userId: String): Flow<User?> {
        return userDao.getUserById(userId)
    }
}