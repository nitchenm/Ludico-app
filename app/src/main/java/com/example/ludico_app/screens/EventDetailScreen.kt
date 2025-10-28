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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    eventDetailViewModel: EventDetailViewModel = viewModel()
) {
    val uiState by eventDetailViewModel.uiState.collectAsState()
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    Scaffold(
        topBar = {
            DetailTopAppBar(
                isUserTheCreator = uiState.isUserTheCreator,
                onBackPressed = { navViewModel.onNavEvent(NavEvent.Back) },
                onEditPressed = { navViewModel.onNavEvent(NavEvent.ToEditEvent(uiState.id)) }
            )
        },
        floatingActionButton = {
            RsvpFab(
                rsvpState = uiState.rsvpState,
                onClick = eventDetailViewModel::toggleRsvp
            )
        }
    ) { innerPadding ->
        if (isExpanded) {
            ExpandedDetailLayout(
                uiState = uiState,
                onCommentChange = eventDetailViewModel::onNewCommentChange,
                onCommentSubmit = eventDetailViewModel::submitComment,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            CompactDetailLayout(
                uiState = uiState,
                onCommentChange = eventDetailViewModel::onNewCommentChange,
                onCommentSubmit = eventDetailViewModel::submitComment,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun CompactDetailLayout(
    uiState: EventDetailUiState,
    onCommentChange: (String) -> Unit,
    onCommentSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { EventHeader(uiState) }
        item { EventDescription(uiState) }
        item { LocationPreview() }
        item { SectionTitle("Participantes (${uiState.currentParticipants}/${uiState.maxParticipants})") }
        items(uiState.participants) { participant -> ParticipantItem(name = participant.name) }
        item { 
            CommentSection(
                comments = uiState.comments,
                newCommentText = uiState.newCommentText,
                onCommentChange = onCommentChange,
                onCommentSubmit = onCommentSubmit
            ) 
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun ExpandedDetailLayout(
    uiState: EventDetailUiState,
    onCommentChange: (String) -> Unit,
    onCommentSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(0.6f).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { EventHeader(uiState) }
            item { EventDescription(uiState) }
            item { SectionTitle("Participantes (${uiState.currentParticipants}/${uiState.maxParticipants})") }
            items(uiState.participants) { participant -> ParticipantItem(name = participant.name) }
            item { 
                CommentSection(
                    comments = uiState.comments,
                    newCommentText = uiState.newCommentText,
                    onCommentChange = onCommentChange,
                    onCommentSubmit = onCommentSubmit
                ) 
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
        Box(
            modifier = Modifier.weight(0.4f).fillMaxHeight().padding(end = 24.dp, top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            LocationPreview(modifier = Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopAppBar(isUserTheCreator: Boolean, onBackPressed: () -> Unit, onEditPressed: () -> Unit) {
    TopAppBar(
        title = { Text("Detalles del Evento") },
        navigationIcon = { IconButton(onClick = onBackPressed) { Icon(Icons.Default.ArrowBack, "Volver") } },
        actions = {
            if (isUserTheCreator) {
                IconButton(onClick = onEditPressed) { Icon(Icons.Default.Edit, "Editar") }
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
fun EventHeader(uiState: EventDetailUiState) {
    Column {
        Text(uiState.eventTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        InfoRow(icon = Icons.Default.CalendarToday, text = "${uiState.date} a las ${uiState.time}")
        InfoRow(icon = Icons.Default.LocationOn, text = uiState.location)
        InfoRow(icon = Icons.Default.VideogameAsset, text = "Juego: ${uiState.gameType}")
        InfoRow(icon = Icons.Default.Person, text = "Organizador: ${uiState.host}")
    }
}

@Composable
fun EventDescription(uiState: EventDetailUiState) {
    Column {
        SectionTitle("Descripción")
        Text(uiState.description, style = MaterialTheme.typography.bodyLarge)
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
private fun ParticipantItem(name: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
        Spacer(Modifier.width(16.dp))
        Text(name, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun CommentSection(
    comments: List<Comment>,
    newCommentText: String,
    onCommentChange: (String) -> Unit,
    onCommentSubmit: () -> Unit
) {
    Column {
        SectionTitle("Comentarios")
        if (comments.isEmpty()) {
            Text(
                text = "Aún no hay comentarios. ¡Sé el primero en comentar!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            comments.forEach { comment ->
                CommentItem(author = comment.author, text = comment.text, timestamp = comment.timestamp)
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = newCommentText,
            onValueChange = onCommentChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Escribe un comentario...") },
            trailingIcon = {
                IconButton(onClick = onCommentSubmit, enabled = newCommentText.isNotBlank()) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar comentario")
                }
            }
        )
    }
}

@Composable
private fun CommentItem(author: String, text: String, timestamp: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(author, fontWeight = FontWeight.Bold)
                Text(timestamp, style = MaterialTheme.typography.bodySmall)
            }
            Text(text)
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
