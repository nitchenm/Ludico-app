package com.example.ludico_app.data.repository

import android.util.Log
import com.example.ludico_app.data.db.dao.EventDao
import com.example.ludico_app.data.db.dao.UserDao
import com.example.ludico_app.data.dto.toEventDto
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.entities.User
import com.example.ludico_app.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EventRepository(
    private val eventDao: EventDao,
    private val userDao: UserDao,
    private val apiService: ApiService
) {
    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    fun getEvent(eventId: String): Flow<Event?> {
        return eventDao.getEvent(eventId)
    }

    fun getUser(userId: String): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun insert(event: Event) = withContext(Dispatchers.IO) {
        val eventDto = event.toEventDto()
        try {
            Log.d("AppDebug", "EventRepository: Intentando guardar evento con id : ${event.eventId}")
            val response = apiService.createEvent(eventDto)

            if (response.isSuccessful) {
                event.isSynced = true
                Log.d("AppDebug", "EventRepository: Evento guardado con id : ${event.eventId}")
                eventDao.insertEvent(event)
            } else {
                val errorCode = response.code()
                val errorMessage = response.errorBody()?.string()
                Log.e("AppDebug", "API Error: Code $errorCode - Message: $errorMessage")
                event.isSynced = false
                eventDao.insertEvent(event)
                throw Exception("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AppDebug", "Network Error in insert(): Could not connect to the server.", e)
            event.isSynced = false
            eventDao.insertEvent(event)
            throw e
        }
    }

    suspend fun updateEvent(event: Event) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateEvent(event.eventId, event.toEventDto())
            if (response.isSuccessful) {
                eventDao.updateEvent(event.copy(isSynced = true))
            } else {
                val errorCode = response.code()
                val errorMessage = response.errorBody()?.string()
                Log.e("AppDebug", "API Error in updateEvent(): Code $errorCode - Message: $errorMessage")
                eventDao.updateEvent(event.copy(isSynced = false))
            }
        } catch (e: Exception) {
            Log.e("AppDebug", "Network Error in updateEvent()", e)
            eventDao.updateEvent(event.copy(isSynced = false))
        }
    }

    suspend fun deleteEvent(eventId: String) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteEvent(eventId)
            if (response.isSuccessful) {
                eventDao.deleteEventById(eventId)
            } else {
                 Log.e("AppDebug", "API Error in deleteEvent(): Code ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AppDebug", "Network Error in deleteEvent()", e)
        }
    }

    suspend fun refreshEvents() = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllEvents()
            if (response.isSuccessful) {
                response.body()?.let { remoteEvents ->
                    eventDao.insertAll(remoteEvents)
                    Log.d("AppDebug", "EventRepository: Successfully refreshed events from remote.")
                }
            } else {
                Log.e("AppDebug", "EventRepository: Failed to fetch events. Code: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AppDebug", "EventRepository: Failed to refresh events.", e)
        }
    }
}
