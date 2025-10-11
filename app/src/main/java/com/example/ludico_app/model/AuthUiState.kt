package com.example.ludico_app.model


data class AuthUiState(
    //Campos rellenables
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    //Errores
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null

) {
}