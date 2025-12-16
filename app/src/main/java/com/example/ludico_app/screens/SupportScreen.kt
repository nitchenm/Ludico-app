// Updated SupportScreen.kt
package com.example.ludico_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ludico_app.data.model.SupportTicket
import com.example.ludico_app.navigation.NavEvent
import com.example.ludico_app.viewmodels.NavViewModel
import com.example.ludico_app.viewmodels.SupportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(navViewModel: NavViewModel, supportViewModel: SupportViewModel) {
    val uiState by supportViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val isEditMode = uiState.currentId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Support") },
                navigationIcon = {
                    IconButton(onClick = { navViewModel.onNavEvent(NavEvent.Back) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Form Section
            Text(if (isEditMode) "Edit Ticket" else "Create Ticket", style = MaterialTheme.typography.headlineSmall)

            if (!isEditMode) {
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = supportViewModel::onEmailChange,
                    label = { Text("Your Contact Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                Text("Contact Email: ${uiState.email}", style = MaterialTheme.typography.bodyLarge)
            }

            OutlinedTextField(
                value = uiState.subject,
                onValueChange = supportViewModel::onSubjectChange,
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = supportViewModel::onDescriptionChange,
                label = { Text("Describe your issue") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            if (isEditMode) {
                OutlinedTextField(
                    value = uiState.status,
                    onValueChange = supportViewModel::onStatusChange,
                    label = { Text("Status") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        supportViewModel.submitTicket()
                    },
                    enabled = !uiState.isLoading &&
                            (if (isEditMode) uiState.subject.isNotBlank() && uiState.description.isNotBlank()
                            else uiState.email.isNotBlank() && uiState.subject.isNotBlank() && uiState.description.isNotBlank()),
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (isEditMode) "Update Ticket" else "Submit Ticket")
                    }
                }

                if (isEditMode) {
                    Button(
                        onClick = { supportViewModel.clearForm() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Cancel")
                    }
                }
            }

            if (uiState.submissionSuccess) {
                Text(
                    "Ticket ${if (isEditMode) "updated" else "submitted"} successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tickets List Section
            Text("Your Tickets", style = MaterialTheme.typography.headlineSmall)

            if (uiState.tickets.isEmpty()) {
                Text("No tickets found.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.tickets) { ticket ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Subject: ${ticket.subject}", style = MaterialTheme.typography.titleMedium)
                                Text("Description: ${ticket.description}")
                                Text("Status: ${ticket.status}")
                                Text("Email: ${ticket.contactEmail}")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { supportViewModel.selectTicketForEdit(ticket) }) {
                                        Text("Edit")
                                    }
                                    TextButton(onClick = { supportViewModel.deleteTicket(ticket.id) }) {
                                        Text("Delete", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}