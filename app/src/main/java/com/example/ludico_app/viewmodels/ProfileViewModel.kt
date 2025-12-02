package com.example.ludico_app.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.repository.UserRepository
import com.example.ludico_app.data.session.SessionManager
import com.example.ludico_app.viewmodels.ProfileTab
import com.example.ludico_app.model.ProfileUiState
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ProfileTab{MY_EVENTS, PREFERENCES}
class ProfileViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {


    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // Simulamos carga de datos
        _uiState.update {
            it.copy(
                userName = "Aventurero Lúdico",
                userLocation = "Santiago, Chile",
                gamesPlayed = 42,
                createdEvents = listOf("Noche de D&D", "Torneo Catan"),
                joinedEvents = listOf("Partida Pathfinder"),
                pastEvents = listOf("Campaña Gloomhaven"),
                favoriteGames = listOf("Dungeons & Dragons", "Catan", "Dixit"),

            )
        }
    }

    fun onEditToggle() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
        // Aquí agregarías la lógica para guardar en backend si isEditing pasa a false
    }

    fun onTabSelected(tab: ProfileTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onUserNameChange(newName: String) {
        _uiState.update { it.copy(userName = newName) }
    }

    fun onUserLocationChange(newLoc: String) {
        _uiState.update { it.copy(userLocation = newLoc) }
    }

    fun logout() {

    }
}
data class ProfileUiState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false, // Modo edición
    val selectedTab: ProfileTab = ProfileTab.MY_EVENTS, // Pestaña actual

    // Datos del Usuario
    val userName: String = "Cargando...",
    val userLocation: String = "",
    val profilePictureUrl: String? = null,
    val gamesPlayed: Int = 0,

    // Listas de datos
    val createdEvents: List<String> = emptyList(),
    val joinedEvents: List<String> = emptyList(),
    val pastEvents: List<String> = emptyList(),
    val favoriteGames: List<String> = emptyList()
)