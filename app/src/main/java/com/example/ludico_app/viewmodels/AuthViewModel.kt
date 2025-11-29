package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.repository.UserRepository
import com.example.ludico_app.model.AuthUiState
import com.example.ludico_app.navigation.NavEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class AuthViewModel(
    private val navViewModel: NavViewModel,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, generalError = null) }
    }

    fun login() {
        viewModelScope.launch {
            if (!validateLoginFields()) {
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            try {
                val response = userRepository.login(_uiState.value.email, _uiState.value.password)

                if (response.isSuccessful) {
                    navViewModel.onNavEvent(NavEvent.ToHome)
                } else {
                    _uiState.update { it.copy(generalError = "Invalid credentials.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(generalError = "An unexpected error occurred.") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            if (!validateRegisterFields()) {
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            try {
                val response = userRepository.register(_uiState.value.email, _uiState.value.password)

                if (response.isSuccessful) {
                    navViewModel.onNavEvent(NavEvent.ToLogin)
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null && errorBody.contains("User already exists")) {
                        _uiState.update { it.copy(emailError = "User already exists.") }
                    } else {
                        _uiState.update { it.copy(generalError = "Registration failed.") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(generalError = "An unexpected error occurred.") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun validateLoginFields(): Boolean {
        val emailValid = isEmailValid(_uiState.value.email)
        if (!emailValid) {
            _uiState.update { it.copy(emailError = "Please enter a valid email.") }
        }
        return emailValid
    }

    private fun validateRegisterFields(): Boolean {
        val emailValid = isEmailValid(_uiState.value.email)
        val passwordValid = _uiState.value.password.length >= 6
        val passwordsMatch = _uiState.value.password == _uiState.value.confirmPassword

        _uiState.update {
            it.copy(
                emailError = if (!emailValid) "Please enter a valid email." else null,
                passwordError = if (!passwordValid) "Password must be at least 6 characters." else null,
                confirmPasswordError = if (!passwordsMatch) "Passwords do not match." else null
            )
        }
        return emailValid && passwordValid && passwordsMatch
    }

    private fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
        ).matcher(email).matches()
    }
}