package com.example.ludico_app.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel
import com.example.ludico_app.viewmodels.CreateEventViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    navViewModel: NavViewModel,

    createEventViewModel: CreateEventViewModel = viewModel()
) {
    // Obtenemos el estado de la UI desde el ViewModel. La UI se recompondrá cuando cambie.
    val uiState by createEventViewModel.uiState.collectAsState()

    // Estados para los diálogos.
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Efecto para navegar hacia atrás cuando el evento se crea con éxito
    LaunchedEffect(uiState.eventCreatedSuccessfully) {
        if (uiState.eventCreatedSuccessfully) {
            navViewModel.onNavEvent(NavEvent.Back)
            createEventViewModel.resetNavigationState() // Resetea para no volver a navegar
        }
    }

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
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            FormTextField(
                label = "Título del evento",
                placeholder = "Ej: Torneo de Mtg",
                value = uiState.title,
                onValueChange = createEventViewModel::onTitleChange, // Pasamos la referencia a la función
                isError = uiState.titleError != null,
                errorMessage = uiState.titleError
            )

            // Descripción
            FormTextField(
                label = "Descripción",
                placeholder = "Describe el evento, reglas, premios...",
                value = uiState.description,
                onValueChange = createEventViewModel::onDescriptionChange,
                isError = uiState.descriptionError != null,
                errorMessage = uiState.descriptionError,
                singleLine = false,
                minLines = 3
            )

            // Tipo de Juego
            FormDropdownField(
                label = "Tipo de juego",
                options = listOf("Juego de mesa", "TCG", "Rol", "Videojuegos"),
                selectedOption = uiState.gameType,
                onSelectionChange = createEventViewModel::onGameTypeChange
            )

            // --- SECCIÓN DE FECHA Y HORA ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    DateField(
                        label = "Fecha",
                        value = uiState.date,
                        onClick = { showDatePicker = true },
                        isError = uiState.dateError != null,
                        errorMessage = uiState.dateError
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    TimeField(
                        label = "Hora",
                        value = uiState.time,
                        onClick = { showTimePicker = true }
                    )
                }
            }

            // Ubicación
            FormTextField(
                label = "Ubicación",
                placeholder = "Ej: Cafe BoardGame...",
                value = uiState.location,
                onValueChange = createEventViewModel::onLocationChange
            )
            // Máximo de Participantes
            FormDropdownField(
                label = "Máximo de Participantes",
                options = (2..32).map { it.toString() },
                selectedOption = uiState.maxParticipants,
                onSelectionChange = createEventViewModel::onMaxParticipantsChange
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botones de acción
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { navViewModel.onNavEvent(NavEvent.Back) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) { Text("Cancelar") }

                Button(
                    onClick = createEventViewModel::createEvent, // Llama a la función del ViewModel
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading, // Deshabilitar mientras carga
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onSecondary)
                    } else {
                        Text("Crear Evento")
                    }
                }
            }
        }
    }

    // --- DIÁLOGOS ---
    if (showDatePicker) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            createEventViewModel.onDateChange("$day/${month + 1}/$year")
            showDatePicker = false
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).apply { setOnDismissListener { showDatePicker = false }; show() }
    }

    if (showTimePicker) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        TimePickerDialog(context, { _, hour, minute ->
            // Llama al ViewModel para actualizar el estado
            createEventViewModel.onTimeChange(String.format("%02d:%02d", hour, minute))
            showTimePicker = false // Corrección del error tipográfico
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        ).apply { setOnDismissListener { showTimePicker = false }; show() }
    }
}


//  COMPOSABLES REUTILIZABLES

@Composable
private fun FormTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
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
            isError = isError,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                errorIndicatorColor = MaterialTheme.colorScheme.error
            ),
            singleLine = singleLine,
            minLines = minLines
        )
        if (isError && errorMessage != null) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
        }
    }
}

@Composable
private fun DateField(
    label: String,
    value: String,
    onClick: () -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            readOnly = true,
            enabled = false, // Deshabilitado para cambiar el color y evitar el cursor
            isError = isError,
            placeholder = { Text("DD/MM/AAAA", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = { Icon(Icons.Default.DateRange, "Date Icon") },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledIndicatorColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        if (isError && errorMessage != null) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
        }
    }
}

@Composable
private fun TimeField(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
            readOnly = true,
            enabled = false,
            placeholder = { Text("HH:MM", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = { Icon(Icons.Default.Schedule, "Time Icon") },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onSelectionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                readOnly = true,
                value = selectedOption.ifEmpty { options.getOrNull(0) ?: "" },
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
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onSelectionChange(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
