package com.example.ludico_app.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(navViewModel: NavViewModel) {
    // --- ESTADOS PARA EL FORMULARIO ---
    // En una app real, esto estaría en un ViewModel dedicado al formulario.
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Estados para la fecha
    var selectedDateText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Estados para la hora
    var selectedTimeText by remember { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Evento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navViewModel.onNavEvent(NavEvent.Back) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            FormTextField(
                label = "Título del evento",
                placeholder = "Ej: Torneo de Mtg",
                value = title,
                onValueChange = { title = it }
            )

            // Descripción
            FormTextField(
                label = "Descripción",
                placeholder = "Describe el evento, reglas, premios...",
                value = description,
                onValueChange = { description = it },
                singleLine = false,
                minLines = 3
            )

            // Tipo de Juego
            FormDropdownField(label = "Tipo de juego", options = listOf("Juego de mesa", "TCG", "Rol", "Videojuegos"))

            // --- SECCIÓN DE FECHA Y HORA ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // CAMPO DE FECHA
                Box(modifier = Modifier.weight(1f)) {
                    DateField(
                        label = "Fecha",
                        value = selectedDateText,
                        onClick = { showDatePicker = true }
                    )
                }
                // CAMPO DE HORA
                Box(modifier = Modifier.weight(1f)) {
                    TimeField(
                        label = "Hora",
                        value = selectedTimeText,
                        onClick = { showTimePicker = true }
                    )
                }
            }

            // Ubicación
            FormTextField(label = "Ubicación", placeholder = "Ej: Cafe BoardGame, Casa del Juan.", value = "", onValueChange = {})

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
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = { /* TODO: Lógica de crear evento y navegar */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("Crear Evento")
                }
            }
        }
    }

    // --- DIÁLOGO DEL DATE PICKER ---
    if (showDatePicker) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year, month, dayOfMonth ->
                selectedDateText = "$dayOfMonth/${month + 1}/$year"
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePicker = false }
            show()
        }
    }

    // --- DIÁLOGO DEL TIME PICKER ---
    if (showTimePicker) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                selectedTimeText = String.format("%02d:%02d", hour, minute)
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // true para formato de 24h
        ).apply {
            setOnDismissListener { showTimePicker = false }
            show()
        }
    }
}


// --- COMPOSABLES REUTILIZABLES PARA EL FORMULARIO ---

@Composable
private fun FormTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}

@Composable
private fun DateField(label: String, value: String, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
            placeholder = { Text("DD/MM/AAAA", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = { Icon(Icons.Default.DateRange, "Date Icon") },
            readOnly = true,
            enabled = false
        )
    }
}

@Composable
private fun TimeField(label: String, value: String, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = { /* No hacemos nada */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurface
            ),
            placeholder = { Text("HH:MM", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = { Icon(Icons.Default.Schedule, "Time Icon") },
            readOnly = true,
            enabled = false
        )
    }
}

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
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = selectedOptionText,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
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

