package com.example.ludico_app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.error
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Asume que Coil está en tus dependencias: implementation("io.coil-kt:coil-compose:2.5.0")
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ludico_app.R // Necesitarás un ic_person_placeholder en drawable
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel
import com.example.ludico_app.viewmodels.ProfileTab
import com.example.ludico_app.model.ProfileUiState
import com.example.ludico_app.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navViewModel: NavViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navViewModel.onNavEvent(NavEvent.Back) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    val buttonText = if (uiState.isEditing) "Guardar" else "Editar"
                    TextButton(onClick = profileViewModel::onEditToggle) {
                        Text(buttonText)
                    }
                }
            )
        },
        // --- NAVEGACIÓN INFERIOR CON PESTAÑAS ---
        bottomBar = {
            ProfileBottomBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = profileViewModel::onTabSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Hacemos scrollable toda la pantalla
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(uiState, profileViewModel)
            Spacer(modifier = Modifier.height(24.dp))

            // Contenido dinámico según la pestaña seleccionada
            when (uiState.selectedTab) {
                ProfileTab.MY_EVENTS -> MyEventsContent(uiState)
                ProfileTab.PREFERENCES -> PreferencesContent(uiState)

            }
        }
    }
}


// --- COMPONENTES DE LA PANTALLA DE PERFIL ---

@Composable
fun ProfileHeader(uiState: ProfileUiState, viewModel: ProfileViewModel) {
    // Foto de Perfil
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uiState.profilePictureUrl)
            .placeholder(R.drawable.logo) // Asegúrate de tener este drawable
            .error(R.drawable.logo)
            .crossfade(true)
            .build(),
        contentDescription = "Foto de perfil",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Nombre (Editable)
    if (uiState.isEditing) {
        OutlinedTextField(
            value = uiState.userName,
            onValueChange = viewModel::onUserNameChange,
            label = { Text("Nombre de usuario") }
        )
    } else {
        Text(uiState.userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Ubicación (Editable)
    if (uiState.isEditing) {
        OutlinedTextField(
            value = uiState.userLocation,
            onValueChange = viewModel::onUserLocationChange,
            label = { Text("Ubicación") }
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(uiState.userLocation, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Métrica de usuario
    Text(
        "¡Has participado en ${uiState.gamesPlayed} eventos!",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun MyEventsContent(uiState: ProfileUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        EventListSection("Eventos Creados", uiState.createdEvents)
        Spacer(modifier = Modifier.height(16.dp))
        EventListSection("Próximos Eventos", uiState.joinedEvents)
        Spacer(modifier = Modifier.height(16.dp))
        EventListSection("Eventos Pasados", uiState.pastEvents)
    }
}

@Composable
fun EventListSection(title: String, events: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        if (events.isEmpty()) {
            Text("Aún no hay eventos aquí.", color = Color.Gray)
        } else {
            events.forEach { eventName ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(eventName, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun PreferencesContent(uiState: ProfileUiState) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text("Juegos Favoritos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.favoriteGames) { game ->
                SuggestionChip(label = { Text(game) }, onClick = {})
            }
            if (uiState.isEditing) {
                item {
                    SuggestionChip(
                        label = { Icon(Icons.Default.Add, null) },
                        onClick = { /* TODO: Abrir diálogo para añadir juego */ }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileBottomBar(selectedTab: ProfileTab, onTabSelected: (ProfileTab) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == ProfileTab.MY_EVENTS,
            onClick = { onTabSelected(ProfileTab.MY_EVENTS) },
            icon = { Icon(Icons.Default.Event, contentDescription = "Mis Eventos") },
            label = { Text("Mis Eventos") }
        )
        NavigationBarItem(
            selected = selectedTab == ProfileTab.PREFERENCES,
            onClick = { onTabSelected(ProfileTab.PREFERENCES) },
            icon = { Icon(Icons.Default.Tune, contentDescription = "Preferencias") },
            label = { Text("Preferencias") }
        )
    }
}