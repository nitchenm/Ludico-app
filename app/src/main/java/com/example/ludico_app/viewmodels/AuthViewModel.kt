package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.model.AuthUiState
import com.example.ludico_app.navigation.NavEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class AuthViewModel(
    private val navViewModel: NavViewModel
) : ViewModel(){
    //Estado privado para que solo el viewmodel pueda modificar
    private val _uiState = MutableStateFlow(AuthUiState())
    //version publica y de lectura
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email:String){
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChange(password:String){
        _uiState.update { it.copy(password = password, confirmPasswordError = null, generalError = null) }
    }
    fun onConfirmPasswordChange(confirmPassword:String){
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, generalError = null) }
    }


    fun login(){
        viewModelScope.launch {
            if (!validateLoginFields()){
                return@launch
            }
            _uiState.update {
                it.copy(isLoading = true)
            }

            delay(2000)

            if(_uiState.value.email == "test@test.com" && _uiState.value.password == "123456"){
                navViewModel.onNavEvent(NavEvent.ToHome)
                _uiState.update { it.copy(isLoading = false) }
            }else{
                _uiState.update {it.copy(isLoading = false, generalError = "Email o contraseña incorrectos.")}
            }
        }
    }

    fun register (){
        viewModelScope.launch {
            if (!validateRegisterFields()){
                return@launch
            }
            _uiState.update { it.copy(isLoading = true) }

            delay(2000)

            if(_uiState.value.email == "test@test.com"){
                _uiState.update { it.copy(isLoading = false, emailError = "Este correo ya esta en uso.") }
            }else{
                navViewModel.onNavEvent(NavEvent.ToLogin)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun validateLoginFields(): Boolean{
        val emailValid = isEmailValid(_uiState.value.email)

        _uiState.update {
            it.copy(
                emailError = if (emailValid) "Porfavor introduce un email valido." else null
            )
        }
        return emailValid
    }

    private fun validateRegisterFields(): Boolean {
        val registerUsername = isEmailValid(_uiState.value.email)
        val registerPassword = _uiState.value.password.length >= 6
        val passwordMatch = _uiState.value.password == _uiState.value.confirmPassword

        _uiState.update {
            it.copy(
                emailError = if(!registerUsername) "Porfavor, introduce un email valido." else null,
                passwordError = if (!registerPassword) "La contraseña debe tener al menos 6 caracteres." else null,
                confirmPasswordError = if (passwordMatch) "Las contraseñas no coinciden." else null
            )
        }
        return registerUsername && registerPassword
    }

    private fun isEmailValid(email:String) :Boolean{
        return Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$", email)
    }
}