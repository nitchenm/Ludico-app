package com.example.ludico_app.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ludico_app.model.EventDetailUiState
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.HomeViewModel
import com.example.ludico_app.viewmodels.NavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navViewModel: NavViewModel,
    homeViewModel: HomeViewModel,
    windowSizeClass: WindowSizeClass
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Eventos de Juegos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface, 
                    titleContentColor = MaterialTheme.colorScheme.onSurface 
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navViewModel.onNavEvent(NavEvent.ToCreateEvent) },
                containerColor = MaterialTheme.colorScheme.primary, 
                contentColor = MaterialTheme.colorScheme.onPrimary 
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear Evento")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Hola!, usuario",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                SearchBar()
            }

            item {
                FilterSection()
            }

            items(uiState.events) { event ->
                EventCard(navViewModel = navViewModel, event = event)
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
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
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

@Composable
private fun EventCard(navViewModel: NavViewModel, event: EventDetailUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface 
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                event.eventTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                event.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    event.date,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    event.time,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(event.location, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "${event.currentParticipants}/${event.maxParticipants} Participantes",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            TextButton(
                onClick = { navViewModel.onNavEvent(NavEvent.ToDetail(event.id)) },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary) 
            ) {
                Text("Ver Detalles", fontWeight = FontWeight.Bold)
            }
        }
    }
}