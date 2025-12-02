package com.example.ludico_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.example.ludico_app.viewmodels.NavViewModel

@Composable
fun SettingsScreen(navViewModel: NavViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = { navViewModel.onNavEvent(com.example.ludico_app.navigation.NavEvent.ToSupport) }) {
            Text("Contact Support")
        }
        // ... other settings items
    }
}