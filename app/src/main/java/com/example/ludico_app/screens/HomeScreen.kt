package com.example.ludico_app.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.HomeViewModel
import com.example.ludico_app.viewmodels.NavViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navViewModel: NavViewModel,
    homeViewModel: HomeViewModel, // <-- CORRECCIÓN: La pantalla ahora recibe el HomeViewModel
    windowSizeClass: WindowSizeClass
) {
    // Recolectamos el estado desde el ViewModel. La UI se actualizará automáticamente.
    val uiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Eventos de Juegos", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: Nav to Profile */ }) {
                        Icon(Icons.Default.AccountCircle, "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navViewModel.onNavEvent(NavEvent.ToCreateEvent) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "Crear Evento")
            }
        }
    ) { innerPadding ->
        // Mostramos un indicador de carga mientras los datos se obtienen de la base de datos.
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // ... (Hola usuario, SearchBar, etc.)
                }

                // --- CORRECCIÓN: Mostramos la lista real de eventos ---
                if (uiState.eventList.isEmpty()) {
                    item {
                        Text(
                            "No hay eventos todavía. ¡Crea el primero!",
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                } else {
                    items(uiState.eventList) { event ->
                        EventCard(navViewModel = navViewModel, event = event)
                    }
                }
            }
        }
    }
}

// --- CORRECCIÓN: EventCard ahora toma un objeto 'Event' ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventCard(navViewModel: NavViewModel, event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = {
            Log.d("AppDebug", "HomeScreen: Clic en evento. Navegando con id: ${event.eventId}")
            navViewModel.onNavEvent(NavEvent.ToDetail(event.eventId)) } // <-- Navegación con ID real
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Mostramos los datos reales del objeto 'event'
            Text(event.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                event.description,
                fontSize = 14.sp,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            // ... (Resto de los campos como fecha, hora, etc., usando event.date, event.time)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("${event.date} a las ${event.time}")
            }
        }
    }
}

@Composable
private fun SearchBar() {
    TextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("buscar eventos") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(50), // Completamente redondeado
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface, // #212121
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
private fun FilterSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.surfaceVariant)
            )
        ) {
            Text("Todos los Eventos")
        }
        OutlinedButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.surfaceVariant)
            )
        ) {
            Text("Todos los Tipos")
        }
    }
}

