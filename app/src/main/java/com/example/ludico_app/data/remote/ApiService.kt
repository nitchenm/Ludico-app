
package com.example.ludico_app.data.remote

import com.example.ludico_app.data.model.Post
import com.example.ludico_app.model.AuthRequest
import com.example.ludico_app.model.AuthResponse
import com.example.ludico_app.data.dto.EventDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @GET("posts")
    suspend fun getPosts(): List<Post>

    @POST("events")
    suspend fun createEvent(@Body event: EventDto): Response<Unit>
}
