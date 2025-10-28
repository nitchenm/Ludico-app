package com.example.ludico_app.viewmodels


import androidx.lifecycle.ViewModel
import com.example.ludico_app.viewmodels.ProfileTab
import com.example.ludico_app.model.ProfileUiState
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun onTabSelected(tab: ProfileTab){
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onEditToggle() {
        val isCurrentlyEditing = _uiState.value.isEditing
        _uiState.update { it.copy (isEditing = !isCurrentlyEditing)}
    }

    fun onUserNameChange(newName: String){
        if (_uiState.value.isEditing){
            _uiState.update { it.copy(userName = newName) }
        }
    }

    fun onUserLocationChange(newLocation: String){
        if(_uiState.value.isEditing){
            _uiState.update{it.copy(userLocation = newLocation)}
        }
    }
}