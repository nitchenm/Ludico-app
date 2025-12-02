# Guide to Implementing the Help & Support Ticket System

This document provides a complete step-by-step guide to integrate the support ticket feature into your Android application, based on the provided backend controller and the current state of the app.

## 1. Data Layer: DTOs & API Service

First, we need to ensure our app can communicate correctly with the backend. This involves defining the data structures and the API endpoint.

### Step 1.1: Update Data Transfer Objects (DTOs)

Your `HelpTicketDtos.kt` file is almost perfect. The `CreateTicketRequest` matches what the backend needs. However, the backend responds with the full `SupportTicket` object, so we should create a DTO for that response.

**File to check:** `app/src/main/java/com/example/ludico_app/data/model/HelpTicketDtos.kt`

```kotlin
// This class is already correct.
data class CreateTicketRequest(
    val contactEmail: String,
    val subject: String,
    val description: String
)

// RECOMMENDATION: Rename 'TicketResponse' to 'SupportTicketResponse' 
// and match the fields from your backend's SupportTicket entity.
data class SupportTicketResponse(
    val id: Long,
    val userId: Long,
    val contactEmail: String,
    val subject: String,
    val description: String,
    val status: String,
    val createdAt: String // Use String for simplicity to parse LocalDateTime
)
```

### Step 1.2: Update the API Service

Add a new function to your `ApiService` interface to call the support ticket endpoint.

**File to check:** `app/src/main/java/com/example/ludico_app/data/remote/ApiService.kt`

```kotlin
// Add this new function to the ApiService interface
@POST("/api/v1/support")
suspend fun createSupportTicket(
    @Body request: CreateTicketRequest
): Response<SupportTicketResponse>
```

## 2. Repository and ViewModel Factory

To keep the code clean, we will create a dedicated repository for this feature and teach the app how to create its ViewModel.

### Step 2.1: Create `SupportRepository.kt`

This new repository will handle all data operations related to support tickets.

**New File:** `app/src/main/java/com/example/ludico_app/data/repository/SupportRepository.kt`

```kotlin
package com.example.ludico_app.data.repository

import com.example.ludico_app.data.model.CreateTicketRequest
import com.example.ludico_app.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupportRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun createTicket(email: String, subject: String, description: String) =
        apiService.createSupportTicket(
            CreateTicketRequest(
                contactEmail = email,
                subject = subject,
                description = description
            )
        )
}
```

### Step 2.2: Update `LudicoApplication.kt`

Your application class is responsible for creating repository instances. We need to add the new `SupportRepository` here.

**File to check:** `app/src/main/java/com/example/ludico_app/LudicoApplication.kt`

```kotlin
// Add the new repository property to LudicoApplication
lateinit var supportRepository: SupportRepository

// ... inside the initializeDependencies() function, after apiService is created:
supportRepository = SupportRepository(apiService) 
```

### Step 2.3: Update `LudicoViewModelFactory.kt`

The ViewModel factory needs to know how to provide the new `SupportRepository` to the `SupportViewModel` we will create later.

**File to check:** `app/src/main/java/com/example/ludico_app/viewmodels/LudicoViewModelFactory.kt`

```kotlin
// Add the new repository to the factory's constructor
class LudicoViewModelFactory(
    //... other repositories
    private val supportRepository: SupportRepository 
) : ViewModelProvider.Factory {

    // ... inside create(), add a new case for SupportViewModel
    modelClass.isAssignableFrom(SupportViewModel::class.java) -> {
        SupportViewModel(supportRepository, navViewModel) as T
    }
}
```
*Note: You will also need to update the factory instantiation in `MainActivity.kt` to pass the new `supportRepository`.*

## 3. Navigation

Now, let's set up the navigation so users can get to the new screen. A logical entry point is the "Settings" screen.

### Step 3.1: Update `Routes.kt` and `NavEvent.kt`

**File to check:** `app/src/main/java/com/example/ludico_app/navigation/Routes.kt`
```kotlin
// Add to Routes sealed class
data object Support : Routes("support")
```

**File to check:** `app/src/main/java/com/example/ludico_app/navigation/NavEvent.kt`
```kotlin
// Add to NavEvent sealed interface
data object ToSupport : NavEvent
```

### Step 3.2: Update `MainActivity.kt`

Handle the new navigation event and add the new screen to the `NavHost`.

**File to check:** `app/src/main/java/com/example/ludico_app/MainActivity.kt`

```kotlin
// In AppContent, inside the 'when' block of the LaunchedEffect
is NavEvent.ToSupport -> navController.navigate(Routes.Support.route)

// In AppContent, inside the NavHost composable
composable(Routes.Support.route) {
    val supportViewModel: SupportViewModel = viewModel(factory = ludicoViewModelFactory)
    SupportScreen(navViewModel = navViewModel, supportViewModel = supportViewModel)
}
```

## 4. UI & ViewModel for the Support Screen

Finally, let's build the screen itself and the logic that powers it.

### Step 4.1: Create `SupportViewModel.kt`

This ViewModel will manage the state of the support form.

**New File:** `app/src/main/java/com/example/ludico_app/viewmodels/SupportViewModel.kt`

```kotlin
package com.example.ludico_app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ludico_app.data.repository.SupportRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SupportUiState(
    val email: String = "",
    val subject: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val submissionSuccess: Boolean = false
)

class SupportViewModel(
    private val supportRepository: SupportRepository,
    private val navViewModel: NavViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) = _uiState.update { it.copy(email = newEmail) }
    fun onSubjectChange(newSubject: String) = _uiState.update { it.copy(subject = newSubject) }
    fun onDescriptionChange(newDesc: String) = _uiState.update { it.copy(description = newDesc) }

    fun submitTicket() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = supportRepository.createTicket(
                    _uiState.value.email,
                    _uiState.value.subject,
                    _uiState.value.description
                )
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, submissionSuccess = true) }
                    // Optionally navigate back after a short delay
                    kotlinx.coroutines.delay(1000)
                    navViewModel.onNavEvent(com.example.ludico_app.navigation.NavEvent.Back)
                } else {
                    // Handle API error
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                // Handle network error
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
```

### Step 4.2: Create `SupportScreen.kt`

This is the UI for submitting a ticket. It should be a simple form.

**New File:** `app/src/main/java/com/example/ludico_app/screens/SupportScreen.kt`

```kotlin
package com.example.ludico_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ludico_app.viewmodels.NavViewModel
import com.example.ludico_app.viewmodels.SupportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(navViewModel: NavViewModel, supportViewModel: SupportViewModel) {
    val uiState by supportViewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Contact Support") }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.email,
                onValueChange = supportViewModel::onEmailChange,
                label = { Text("Your Contact Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.subject,
                onValueChange = supportViewModel::onSubjectChange,
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = supportViewModel::onDescriptionChange,
                label = { Text("Please describe your issue") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
            Button(
                onClick = supportViewModel::submitTicket,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit Ticket")
                }
            }
            if (uiState.submissionSuccess) {
                Text("Ticket submitted successfully!", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
```

### Step 4.3: Add Entry Point in `SettingsScreen.kt`

Finally, give the user a way to get to this new screen.

**File to check:** `app/src/main/java/com/example/ludico_app/screens/SettingsScreen.kt`

```kotlin
// This is a simple implementation for SettingsScreen. You can adapt it.
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
```

This completes the full implementation path from backend to frontend.
