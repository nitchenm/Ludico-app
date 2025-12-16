package com.example.ludico_app.data.repository

import com.example.ludico_app.data.model.CreateTicketRequest
import com.example.ludico_app.data.model.SupportTicket
import com.example.ludico_app.data.model.UpdateTicketRequest
import com.example.ludico_app.data.model.toDomain
import com.example.ludico_app.data.model.toDomainList
import com.example.ludico_app.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
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
    suspend fun getAllTickets(): Result<List<SupportTicket>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.getAllSupportTickets()
                if (!response.isSuccessful) throw HttpException(response)
                // Se asegura el mapeo a dominio
                val listDto = response.body() ?: emptyList()
                listDto.toDomainList()
            }
        }

    suspend fun getTicketById(ticketId: String): Result<SupportTicket> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.getSupportTicketById(ticketId)
                if (!response.isSuccessful) throw HttpException(response)
                // 3. CORRECCIÓN CLAVE: Se mapea la respuesta DTO a la clase de dominio
                val dto = response.body() ?: error("Ticket no encontrado")
                dto.toDomain()
            }
        }

    suspend fun updateTicket(ticketId: String, request: UpdateTicketRequest): Result<SupportTicket> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.updateSupportTicket(ticketId, request)
                if (!response.isSuccessful) throw HttpException(response)
                // 4. CORRECCIÓN CLAVE: Se mapea la respuesta DTO a la clase de dominio
                val dto = response.body() ?: error("Respuesta vacía al actualizar")
                dto.toDomain()
            }
        }

    suspend fun deleteTicket(ticketId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.deleteSupportTicket(ticketId)
                if (!response.isSuccessful) throw HttpException(response)
                Unit // La operación fue exitosa, no hay nada que devolver
            }
        }

}
