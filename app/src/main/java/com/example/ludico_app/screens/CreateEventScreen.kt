package com.example.ludico_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel

// Colores del diseño.
private val FieldBackgroundColor = Color(0xFFF0F0F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(navViewModel: NavViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Evento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navViewModel.onNavEvent(NavEvent.Back) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Para que el formulario sea scrollable
        ) {
            // Título
            FormTextField(label = "Título del evento", placeholder = "Ej: Torneo de Mtg")

            // Descripción
            FormTextField(label = "Descripción", placeholder = "Describe el evento, reglas, premios...", singleLine = false, minLines = 3)

            // Tipo de Juego
            FormDropdownField(label = "Tipo de juego", options = listOf("Juego de mesa", "TCG", "Rol", "Videojuegos"))

            // Fecha y Hora
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    FormTextField(label = "Fecha", placeholder = "DD/MM/AAAA")
                }
                Box(modifier = Modifier.weight(1f)) {
                    FormTextField(label = "Hora", placeholder = "HH:MM")
                }
            }

            // Ubicación
            FormTextField(label = "Ubicación", placeholder = "Ej: Cafe BoardGame, Casa del Juan.")

            // Máximo de Participantes
            FormDropdownField(label = "Máximo de Participantes", options = (2..32).map { it.toString() })

            Spacer(modifier = Modifier.weight(1f)) // Empuja los botones hacia abajo

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navViewModel.onNavEvent(NavEvent.Back) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.onSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = { /* falta la logica de crear evento y navegar */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.onSecondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Crear Evento")
                }
            }
        }
    }
}

// Composable reutilizable para los campos de texto del formulario
@Composable
private fun FormTextField(label: String, placeholder: String, singleLine: Boolean = true, minLines: Int = 1) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = "",
            onValueChange = { /* TODO */ },
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = FieldBackgroundColor,
                focusedContainerColor = FieldBackgroundColor
            ),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}

// Composable reutilizable para los menús desplegables
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormDropdownField(label: String, options: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options.getOrNull(0) ?: "") }

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                readOnly = true,
                value = selectedOptionText,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = FieldBackgroundColor,
                    focusedContainerColor = FieldBackgroundColor
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedOptionText = selectionOption
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}