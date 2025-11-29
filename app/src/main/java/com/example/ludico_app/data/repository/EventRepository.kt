package com.example.ludico_app.data.repository

import com.example.ludico_app.data.db.dao.EventDao
import com.example.ludico_app.data.db.dao.UserDao
import com.example.ludico_app.data.dto.toEventDto
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.entities.User
import com.example.ludico_app.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class EventRepository(private val eventDao: EventDao, private val userDao: UserDao, private val apiService: ApiService) {

    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    fun getEvent(eventId: String?): Flow<Event?> {
        return eventDao.getEvent(eventId)
    }

    suspend fun insert(event: Event) {
        eventDao.insertEvent(event)
        try {
            val response = apiService.createEvent(event.toEventDto())
            if (response.isSuccessful) {
                eventDao.updateEvent(event.copy(isSynced = true))
            }
        } catch (e: IOException) {
            // The error will be handled by the sync worker
        }
    }

    fun getUser(userId: String): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun syncEvents() {
        val unsyncedEvents = eventDao.getUnsyncedEvents()
        for (event in unsyncedEvents) {
            try {
                val response = apiService.createEvent(event.toEventDto())
                if (response.isSuccessful) {
                    eventDao.updateEvent(event.copy(isSynced = true))
                }
            } catch (e: IOException) {
                // Continue to the next event
            }
        }
    }
}