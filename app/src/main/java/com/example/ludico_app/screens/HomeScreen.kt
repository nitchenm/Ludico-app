package com.example.ludico_app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel

// Colores del diseño. Mover a tu archivo Theme/Color.kt si lo prefieres
private val LightGrayBackground = Color(0xFFF5F5F5)
private val CardBackgroundColor = Color(0xFFE8F5E9) // Un verde muy claro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navViewModel: NavViewModel,
    windowSizeClass: WindowSizeClass // Lo mantenemos para futura adaptabilidad
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Eventos de Juegos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        // --- BOTÓN FLOTANTE ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navViewModel.onNavEvent(NavEvent.ToCreateEvent) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear Evento")
            }
        },
        containerColor = LightGrayBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Saludo y barra de búsqueda
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Hola!, usuario", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                SearchBar()
            }

            // Filtros
            item {
                FilterSection()
            }

            // Lista de eventos (aquí solo mostramos un ejemplo)
            items(5) { // Reemplaza esto con tu lista real de eventos
                EventCard(navViewModel)
            }

            // Botón de Cargar Más
            item {
                Button(
                    onClick = { /* TODO: Lógica para cargar más eventos */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cargar Más", fontWeight = FontWeight.Bold)
                }
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
        placeholder = { Text("buscar eventos", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Icono de búsqueda") },
        shape = RoundedCornerShape(30.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface

        )
    )
}

@Composable
private fun FilterSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Estos serían ExposedDropdownMenuBox en una implementación real
        OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
            Text("Todos los Eventos")
        }
        OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
            Text("Todos los Tipos")
        }
    }
}


@Composable
private fun EventCard(navViewModel: NavViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Torneo de Magic: The Gathering",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Torneo formato Standard con premios para los primeros 3 lugares",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Fila para Fecha y Hora
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = "Icono de fecha", tint = Color.DarkGray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Miércoles, 24 septiembre", fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.weight(1f))
                Text("18:00", fontSize = 14.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Reino de los duelos", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
            Text("11/16 Participantes", fontSize = 14.sp, color = Color.DarkGray)

            TextButton(
                onClick = { navViewModel.onNavEvent(NavEvent.ToDetail) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Ver Detalles", fontWeight = FontWeight.Bold)
            }
        }
    }
}