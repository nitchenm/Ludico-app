package com.example.ludico_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel
import com.example.ludico_app.viewmodels.SupportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(navViewModel: NavViewModel, supportViewModel: SupportViewModel) {
    val uiState by supportViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current


    Scaffold(
        topBar = { TopAppBar(title = { Text("Contact Support") },
            navigationIcon = {
                IconButton(onClick = { navViewModel.onNavEvent(NavEvent.Back) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }})}
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = supportViewModel::onEmailChange,
                label = { Text("Tu Email de contacto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.subject,
                onValueChange = supportViewModel::onSubjectChange,
                label = { Text("Asunto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = supportViewModel::onDescriptionChange,
                label = { Text("Describe tu problema") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // Altura para área de texto
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    focusManager.clearFocus() // Ocultar teclado
                    supportViewModel.submitTicket()
                },
                enabled = !uiState.isLoading && uiState.email.isNotBlank() && uiState.description.isNotBlank(), // Validación básica
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enviar Ticket")
                }
            }

            // Mensaje de éxito o error visual
            if (uiState.submissionSuccess) {
                Text(
                    "¡Ticket enviado! Volviendo...",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}