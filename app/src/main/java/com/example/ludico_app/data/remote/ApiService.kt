package com.example.ludico_app.data.remote

import com.example.ludico_app.data.dto.BackendEventDto
import com.example.ludico_app.data.dto.EventDto
import com.example.ludico_app.data.model.CreateTicketRequest
import com.example.ludico_app.data.model.Post
import com.example.ludico_app.data.dto.SupportTicketResponse
import com.example.ludico_app.data.model.UpdateTicketRequest
import com.example.ludico_app.model.AuthResponse
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") pass: String
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") pass: String,
        @Field("rol") rol: String = "USER"
    ): Response<String>

    @GET("posts")
    suspend fun getPosts(): List<Post>

    /* Seccion para Eventos*/
    @POST("api/v1/events")
    suspend fun createEvent(@Body event: EventDto): Response<BackendEventDto>

    @GET("api/v1/events")
    suspend fun getAllEvents(): Response<List<com.example.ludico_app.data.entities.Event>>


    @PUT("api/v1/events/{id}")
    suspend fun updateEvent(@Path("id") eventId: String, @Body event: EventDto): Response<BackendEventDto>

    @DELETE("api/v1/events/{id}")
    suspend fun deleteEvent(@Path("id") eventId: String): Response<Unit>


    /* Seccion para Ticket de Soporte*/

    // CREATE: Crear un nuevo ticket de soporte.
    @POST("api/v1/support")
    suspend fun createSupportTicket(
        @Body request: CreateTicketRequest
    ): Response<SupportTicketResponse> // Devuelve el DTO de respuesta

    // READ (All): Obtener todos los tickets de soporte.
    @GET("api/v1/support")
    suspend fun getAllSupportTickets(): Response<List<SupportTicketResponse>>

    // READ (One): Obtener un ticket por su ID.
    @GET("api/v1/support/{id}")
    suspend fun getSupportTicketById(
        @Path("id") ticketId: String
    ): Response<SupportTicketResponse> // 2. CORRECCIÓN: Debe devolver el DTO de respuesta.

    // UPDATE: Actualizar un ticket existente.
    @PUT("api/v1/support/{id}")
    suspend fun updateSupportTicket(
        @Path("id") ticketId: String,
        @Body request: UpdateTicketRequest
    ): Response<SupportTicketResponse> // 3. CORRECCIÓN: Debe devolver el DTO de respuesta.

    // DELETE: Eliminar un ticket.
    @DELETE("api/v1/support/{id}")
    suspend fun deleteSupportTicket(@Path("id") ticketId: String): Response<Unit>


}
