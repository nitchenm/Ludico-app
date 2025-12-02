package com.example.ludico_app.data.repository

import com.example.ludico_app.data.model.CreateTicketRequest
import com.example.ludico_app.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun createTicket(email: String, subject: String, description: String) =
        apiService.createSupportTicket(
            CreateTicketRequest(
                contactEmail = email,
                subject = subject,
                description = description
            )
        )
}
