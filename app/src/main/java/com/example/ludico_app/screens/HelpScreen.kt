// En: screens/HelpScreen.kt
package com.example.ludico_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.HelpViewModel
import com.example.ludico_app.viewmodels.NavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    viewModel: HelpViewModel,
    navViewModel: NavViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto para mostrar mensajes de éxito o error
    LaunchedEffect(uiState.submissionSuccess, uiState.submissionError) {
        if (uiState.submissionSuccess) {
            snackbarHostState.showSnackbar("Ticket enviado con éxito. Nos pondremos en contacto contigo pronto.")
            // Espera un poco y navega hacia atrás
            kotlinx.coroutines.delay(1500)
            navViewModel.onNavEvent(NavEvent.Back)
        }
        uiState.submissionError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onFormConsumed() // Limpia el error para no mostrarlo de nuevo
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Centro de Ayuda") },
                navigationIcon = {
                    IconButton(onClick = { navViewModel.onNavEvent(NavEvent.Back) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Contacta con Soporte", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.contactEmail,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Tu Email de Contacto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.subject,
                onValueChange = viewModel::onSubjectChange,
                label = { Text("Asunto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Describe tu problema detalladamente") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::submitTicket,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Enviar Ticket")
                }
            }
        }
    }
}
    