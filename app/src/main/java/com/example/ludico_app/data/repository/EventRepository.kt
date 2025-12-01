package com.example.ludico_app.data.repository

import android.util.Log
import com.example.ludico_app.data.db.dao.EventDao
import com.example.ludico_app.data.db.dao.UserDao
import com.example.ludico_app.data.dto.toEventDto
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.entities.User
import com.example.ludico_app.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import java.io.IOException

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

    suspend fun insert(event: Event) {
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
            }
        } catch (e: IOException) {
            Log.e("AppDebug", "Network Error: Could not connect to the server.", e)
            event.isSynced = false
            eventDao.insertEvent(event)
        }
    }

    suspend fun refreshEvents() {
        try {
            // 1. Fetch events from the remote API
            val remoteEvents = apiService.getAllEvents()
            remoteEvents.body()?.let { remoteEvents-> eventDao.insertAll(remoteEvents) }
            // 2. Insert the fetched events into the local database

            Log.d("AppDebug", "EventRepository: Successfully refreshed events from remote.")
        } catch (e: Exception) {
            Log.e("AppDebug", "EventRepository: Failed to refresh events.", e)
            // Re-throwing the exception so the Worker knows the operation failed
            throw e
        }
    }
}
