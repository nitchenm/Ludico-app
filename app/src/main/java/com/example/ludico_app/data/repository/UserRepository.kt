
package com.example.ludico_app.data.repository

import com.example.ludico_app.data.remote.ApiService
import com.example.ludico_app.data.remote.RetrofitInstance
import com.example.ludico_app.model.AuthRequest
import com.example.ludico_app.model.AuthResponse
import retrofit2.Response

class UserRepository {

    private val apiService: ApiService = RetrofitInstance.api

    suspend fun login(authRequest: AuthRequest): Response<AuthResponse> {
        return apiService.login(authRequest)
    }

    suspend fun register(authRequest: AuthRequest): Response<AuthResponse> {
        return apiService.register(authRequest)
    }
}
