package com.example.ludico_app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.repository.UserRepository
import com.example.ludico_app.data.session.SessionManager
import com.example.ludico_app.model.AuthUiState
import com.example.ludico_app.navigation.NavEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val navViewModel: NavViewModel,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, emailError = null) }
    }

    fun onPasswordChange(newPass: String) {
        _uiState.update { it.copy(password = newPass, passwordError = null) }
    }

    fun onConfirmPasswordChange(newPass: String) {
        _uiState.update { it.copy(confirmPassword = newPass, confirmPasswordError = null) }
    }

    fun login() {
        if (!validateLogin()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = userRepository.login(_uiState.value.email, _uiState.value.password)
                if (response.isSuccessful) {
                    val authData = response.body()
                    if(authData != null){
                        sessionManager.saveAuthToken(authData.token)
                        sessionManager.saveUserId(authData.userId)

                        Log.d("AppDebug", "AuthVM: Login exitoso. Token y UserID (${authData.userId}) guardados.")
                        navViewModel.onNavEvent(NavEvent.ToHome)
                    }else{
                        Log.e("AppDebug", "AuthVM: El cuerpo de la respuesta es nulo")
                    }

                } else {
                    _uiState.update { it.copy(passwordError = "Invalid credentials") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(passwordError = "Connection failed") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun register() {
        if (!validateRegistration()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = userRepository.register(_uiState.value.email, _uiState.value.password)
                if (response.isSuccessful) {
                    navViewModel.onNavEvent(NavEvent.ToLogin)
                } else {
                    _uiState.update { it.copy(emailError = "User already exists") }
                }
            } catch (e: Exception) { 
                _uiState.update { it.copy(emailError = "Connection failed") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun validateLogin(): Boolean {
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()
        val passValid = _uiState.value.password.length > 5

        _uiState.update {
            it.copy(
                emailError = if (!emailValid) "Invalid email format" else null,
                passwordError = if (!passValid) "Password must be at least 6 characters" else null
            )
        }

        return emailValid && passValid
    }

    private fun validateRegistration(): Boolean {
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()
        val passValid = _uiState.value.password.length > 5
        val confirmPassValid = _uiState.value.password == _uiState.value.confirmPassword

        _uiState.update {
            it.copy(
                emailError = if (!emailValid) "Invalid email format" else null,
                passwordError = if (!passValid) "Password must be at least 6 characters" else null,
                confirmPasswordError = if (!confirmPassValid) "Passwords do not match" else null
            )
        }

        return emailValid && passValid && confirmPassValid
    }
}
