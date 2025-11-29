package com.example.ludico_app.data.repository

import com.example.ludico_app.data.remote.ApiService
import com.example.ludico_app.data.remote.RetrofitInstance
import com.example.ludico_app.model.AuthResponse
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    suspend fun login(email: String, pass: String): Response<AuthResponse> {
        return apiService.login(email, pass)
    }

    suspend fun register(email: String, pass: String): Response<String> {
        return apiService.register(email, pass)
    }
}
