package com.example.ludico_app.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("email") val email: String
)
