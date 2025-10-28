package com.example.ludico_app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.entities.User
import com.example.ludico_app.model.Comment
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel
import com.example.ludico_app.model.EventDetailUiState
import com.example.ludico_app.viewmodels.EventDetailViewModel
import com.example.ludico_app.model.RsvpState

@Composable
fun EventDetailScreen(
    navViewModel: NavViewModel,
    windowSizeClass: WindowSizeClass,
    eventDetailViewModel: EventDetailViewModel
) {
    // se obtiene el estado desde el ViewModel para que el UI se actualice automaticamente
    val uiState by eventDetailViewModel.uiState.collectAsState()

    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val context = LocalContext.current
    Scaffold(
        topBar = {
            DetailTopAppBar(
                isUserTheCreator = uiState.isUserTheCreator,
                onBackPressed = { navViewModel.onNavEvent(NavEvent.Back) },
                onEditPressed = { /* TODO: Navegar a pantalla de edición */ } ,
                onSharePressed = { eventDetailViewModel.shareEvent(context) }
            )
        },
        floatingActionButton = {
            RsvpFab(
                rsvpState = uiState.rsvpState,
                onClick = eventDetailViewModel::toggleRsvp
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.event != null) {
            // Una vez cargado el evento, mostramos el layout correspondiente.
            if (isExpanded) {
                ExpandedDetailLayout(uiState = uiState, modifier = Modifier.padding(innerPadding))
            } else {
                CompactDetailLayout(uiState = uiState, modifier = Modifier.padding(innerPadding))
            }
        } else {
            // Manejo del caso en que el evento no se encuentra.
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Evento no encontrado.")
            }
        }
    }
}

// --- Layouts Adaptables  ---

@Composable
private fun CompactDetailLayout(uiState: EventDetailUiState, modifier: Modifier = Modifier) {
    uiState.event?.let { eventFromState ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                EventHeader(event = eventFromState, hostName = uiState.host?.userName ?: "Desconocido")
                Spacer(Modifier.height(16.dp))
                EventDescription(event = eventFromState)
                Spacer(Modifier.height(16.dp))
                LocationPreview()
            }
            item { SectionTitle("Participantes (${uiState.participants.size}/${eventFromState.maxParticipants})") }
            items(uiState.participants) { participant ->
                ParticipantItem(user = participant)
            }
            item { SectionTitle("Comentarios (${uiState.comments.size})") }
            items(uiState.comments) { comment ->
                CommentItem(comment = comment)
            }
            // Espacio final para que el FAB no tape el último elemento
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ExpandedDetailLayout(uiState: EventDetailUiState, modifier: Modifier = Modifier) {
    uiState.event?.let { event ->
        Row(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { EventHeader(event = event, hostName = uiState.host?.userName ?: "Desconocido") }
                item { EventDescription(event = event) }
                item { SectionTitle("Participantes (?/${event.maxParticipants})") }
                // El resto del contenido...
            }
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(end = 24.dp, top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                LocationPreview(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopAppBar(
    isUserTheCreator: Boolean,
    onBackPressed: () -> Unit,
    onEditPressed: () -> Unit,
    onSharePressed: () -> Unit
) {
    TopAppBar(
        title = { Text("Detalles del Evento") },
        navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, "Volver") } },
        actions = {
            if (isUserTheCreator) {
                IconButton(onClick = onEditPressed) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Evento")
                }
            }

            IconButton(onClick = onSharePressed) { // <-- Usar el nuevo parámetro aquí
                Icon(Icons.Default.Share, contentDescription = "Compartir")
            }
            IconButton(onClick = { /* TODO */ }) { Icon(Icons.Default.Share, "Compartir") }
        }
    )
}

@Composable
private fun RsvpFab(rsvpState: RsvpState, onClick: () -> Unit) {
    val (text, icon, color) = when (rsvpState) {
        RsvpState.JOINED -> Triple("Abandonar", Icons.Default.Check, MaterialTheme.colorScheme.error)
        RsvpState.NOT_JOINED -> Triple("Unirse", Icons.Default.Add, MaterialTheme.colorScheme.primary)
        RsvpState.FULL -> Triple("Completo", Icons.Default.Close, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
    ExtendedFloatingActionButton(text = { Text(text) }, icon = { Icon(icon, null) }, onClick = onClick, containerColor = color)
}

@Composable
fun EventHeader(event: Event, hostName: String) {
    Column {
        Text(event.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        InfoRow(icon = Icons.Default.CalendarToday, text = "${event.date} a las ${event.time}")
        InfoRow(icon = Icons.Default.LocationOn, text = event.location)
        InfoRow(icon = Icons.Default.VideogameAsset, text = "Juego: ${event.gameType}")
        InfoRow(icon = Icons.Default.Person, text = "Organizador: $hostName")
    }
}

@Composable
fun EventDescription(event: Event) {
    Column {
        SectionTitle("Descripción")
        Text(event.description, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun LocationPreview(modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(200.dp)) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), Alignment.Center) {
            Text("Vista previa del mapa", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ParticipantItem(user: User) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
        Spacer(Modifier.width(16.dp))
        // Usamos la propiedad 'userName' del objeto User
        Text(user.userName, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            // TODO: Necesitarías obtener el nombre del autor usando comment.authorUserId
            Text(comment.author, fontWeight = FontWeight.Bold)
            Text(comment.text)
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}
