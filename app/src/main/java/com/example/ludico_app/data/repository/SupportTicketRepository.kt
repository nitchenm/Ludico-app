// En: data/repository/SupportTicketRepository.kt
package com.example.ludico_app.data.repository

import android.util.Log
import com.example.ludico_app.data.model.*
import com.example.ludico_app.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.Result

// 1. CORRECCIÓN: Se elimina la importación conflictiva de SupportTicketResponse del paquete 'model'
// import com.example.ludico_app.data.model.SupportTicketResponse

// Se importa el DTO correcto desde el paquete 'dto'
import com.example.ludico_app.data.dto.SupportTicketResponse

class SupportTicketRepository(private val apiService: ApiService) {

    suspend fun createTicket(request: CreateTicketRequest): Result<SupportTicket> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.createSupportTicket(request)
                if (!response.isSuccessful) throw HttpException(response)
                // 2. CORRECCIÓN: Se asegura el mapeo a dominio
                val dto = response.body() ?: error("Respuesta vacía del servidor")
                dto.toDomain()
            }
        }

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