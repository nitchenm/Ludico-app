package com.example.ludico_app.data.remote

import com.example.ludico_app.data.dto.EventDto
import com.example.ludico_app.data.model.Post
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
        @Field("password") pass: String
    ): Response<String>

    @GET("posts")
    suspend fun getPosts(): List<Post>

    @POST("events")
    suspend fun createEvent(@Body event: EventDto): Response<Unit>
}
