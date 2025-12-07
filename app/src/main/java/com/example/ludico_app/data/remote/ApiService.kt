package com.example.ludico_app.data.remote

import com.example.ludico_app.data.dto.BackendEventDto
import com.example.ludico_app.data.dto.EventDto
import com.example.ludico_app.data.model.CreateTicketRequest
import com.example.ludico_app.data.model.Post
import com.example.ludico_app.data.model.SupportTicketResponse
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

    @POST("api/v1/events")
    suspend fun createEvent(@Body event: EventDto): Response<BackendEventDto>

    @GET("api/v1/events")
    suspend fun getAllEvents(): Response<List<com.example.ludico_app.data.entities.Event>>

    @POST("/api/v1/support")
    suspend fun createSupportTicket(
        @Body request: CreateTicketRequest
    ): Response<SupportTicketResponse>

    @PUT("api/v1/events/{id}")
    suspend fun updateEvent(@Path("id") eventId: String, @Body event: EventDto): Response<BackendEventDto>

    @DELETE("api/v1/events/{id}")
    suspend fun deleteEvent(@Path("id") eventId: String): Response<Unit>
}
