# Guide to Implementing the Help & Support Ticket System (HomeScreen FAB)

This guide provides a complete, step-by-step plan to implement the support ticket feature. It has been refactored to place the entry point on the `HomeScreen` and to first address and stabilize some inconsistencies in the project's current ViewModel and repository setup.

## 1. Prerequisite: Stabilizing ViewModel and Dependency Setup

An analysis of the project files reveals some contradictions in how repositories and ViewModels are created. To ensure the project is stable and can be built upon, the following foundational refactor is highly recommended.

### Step 1.1: Consolidate Dependencies in `LudicoApplication`

Your `LudicoApplication` class should be the single source of truth for all singleton repositories and managers. The current version is missing `SessionManager` and the new `SupportRepository`.

**File to modify:** `app/src/main/java/com/example/ludico_app/LudicoApplication.kt`

```kotlin
class LudicoApplication : Application() {

    // Centralize all repositories and managers here
    val database: LudicoDatabase by lazy { LudicoDatabase.getDatabase(this) }
    
    // Add SessionManager here for consistency
    val sessionManager: SessionManager by lazy { SessionManager(applicationContext) }
    
    val apiService by lazy { RetrofitInstance.api } // Or your centralized Retrofit instance

    val eventRepository: EventRepository by lazy {
        EventRepository(database.eventDao(), database.userDao(), apiService)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(apiService)
    }

    // New repository for the support feature
    val supportRepository: SupportRepository by lazy {
        SupportRepository(apiService)
    }

    override fun onCreate() {
        super.onCreate()
        // ... your WorkManager setup ...
    }
    // ...
}
```

### Step 1.2: Simplify and Correct the `LudicoViewModelFactory`

The factory should not require a `NavViewModel` in its constructor, as this creates a circular dependency. It should only take data sources (repositories, managers) and be able to create any ViewModel that depends on them.

**File to modify:** `app/src/main/java/com/example/ludico_app/viewmodels/LudicoViewModelFactory.kt`

```kotlin
class LudicoViewModelFactory(
    // The factory should ONLY depend on data sources, not other ViewModels.
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val supportRepository: SupportRepository // <-- New
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // We get the NavViewModel here if needed by the ViewModel being created.
        // This avoids passing it in the constructor.
        val navViewModel = extras[ViewModelProvider.NewInstanceFactory.KEY_VIEW_MODEL_STORE]?.get("NavViewModel") as? NavViewModel
            ?: throw IllegalStateException("NavViewModel must be created first")
            
        val savedStateHandle = extras.createSavedStateHandle()

        return when {
            modelClass.isAssignableFrom(NavViewModel::class.java) -> NavViewModel() as T
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(navViewModel, userRepository, sessionManager) as T
            modelClass.isAssignableFrom(CreateEventViewModel::class.java) -> CreateEventViewModel(eventRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(eventRepository) as T
            

            // Add cases for other ViewModels to standardize their creation
            modelClass.isAssignableFrom(EventDetailViewModel::class.java) -> EventDetailViewModel(eventRepository, savedStateHandle) as T
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(userRepository, savedStateHandle) as T // Assuming UserViewModel needs a handle

            // New case for our SupportViewModel
            modelClass.isAssignableFrom(SupportViewModel::class.java) -> {
                SupportViewModel(supportRepository, navViewModel) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
```

### Step 1.3: Correct `MainActivity` Instantiation Logic

With the factory simplified, `MainActivity` can now correctly instantiate it and provide ViewModels to the UI tree.

**File to modify:** `app/src/main/java/com/example/ludico_app/MainActivity.kt`

```kotlin
// In MainActivity's onCreate method
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val application = application as LudicoApplication

    setContent {
        LudicoappTheme {
            val windowSizeClass = calculateWindowSizeClass(this)
            
            // Create the factory, providing all required dependencies from the Application class.
            val ludicoViewModelFactory = LudicoViewModelFactory(
                eventRepository = application.eventRepository,
                userRepository = application.userRepository,
                sessionManager = application.sessionManager,
                supportRepository = application.supportRepository // <-- New
            )

            AppContent(
                navController = rememberNavController(),
                ludicoViewModelFactory = ludicoViewModelFactory,
                windowSizeClass = windowSizeClass
            )
        }
    }
}

// Update AppContent to get NavViewModel correctly
@Composable
fun AppContent(
    navController: NavHostController,
    ludicoViewModelFactory: LudicoViewModelFactory,
    windowSizeClass: WindowSizeClass
) {
    // NavViewModel has no dependencies, so it can be created simply.
    val navViewModel: NavViewModel = viewModel()

    // Pass the NavViewModel to the factory for ViewModels that need it.
    // This is done via the CreationExtras in the updated factory.
    val factoryWithNav = LudicoViewModelFactory(
        navViewModel = navViewModel,
        /* ... other dependencies ... */
    )
    
    // ... rest of AppContent ...
    
    // Example of using the factory in a composable
    composable(Routes.Home.route) {
        val homeViewModel: HomeViewModel = viewModel(factory = factoryWithNav)
        HomeScreen(navViewModel = navViewModel, homeViewModel = homeViewModel, /*...*/)
    }
}
```

---

## 2. Data Layer: API and DTOs

Define the data structures to match your backend.

### Step 2.1: Define DTOs

Create a new file for your support ticket data classes.

**New File:** `app/src/main/java/com/example/ludico_app/data/dto/SupportTicketDtos.kt`

```kotlin
package com.example.ludico_app.data.dto

// Request body for creating a ticket. Your backend sets userId automatically.
data class CreateSupportTicketRequest(
    val contactEmail: String,
    val subject: String,
    val description: String
)

// The response from the backend
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

### Step 2.2: Update `ApiService`

Add the new endpoint function.

**File to modify:** `app/src/main/java/com/example/ludico_app/data/remote/ApiService.kt`

```kotlin
// Add this to your ApiService interface
@POST("api/v1/support")
suspend fun createSupportTicket(
    @Body request: CreateSupportTicketRequest
): Response<SupportTicketResponse>
```

---

## 3. Repository Layer

Create a dedicated repository and add it to the application dependency graph.

**New File:** `app/src/main/java/com/example/ludico_app/data/repository/SupportRepository.kt`
```kotlin
package com.example.ludico_app.data.repository

import com.example.ludico_app.data.dto.CreateSupportTicketRequest
import com.example.ludico_app.data.remote.ApiService

class SupportRepository(private val apiService: ApiService) {

    suspend fun createTicket(email: String, subject: String, description: String) =
        apiService.createSupportTicket(
            CreateSupportTicketRequest(
                contactEmail = email,
                subject = subject,
                description = description
            )
        )
}
```
*(Remember to add `supportRepository` to `LudicoApplication.kt` as shown in Step 1.1)*

---

## 4. Navigation

Set up the navigation route for the new support screen.

### Step 4.1: Update `Routes.kt` and `NavEvent.kt`

**File to modify:** `app/src/main/java/com/example/ludico_app/navigation/Routes.kt`
```kotlin
// Add to Routes sealed class
data object Support : Routes("support")
```

**File to modify:** `app/src/main/java/com/example/ludico_app/navigation/NavEvent.kt`
```kotlin
// Add to NavEvent sealed interface
data object ToSupport : NavEvent
```

### Step 4.2: Update `MainActivity.kt` `NavHost`

Handle the new route and define the composable.

**File to modify:** `app/src/main/java/com/example/ludico_app/MainActivity.kt`

```kotlin
// In AppContent, inside the 'when' block for navigation
is NavEvent.ToSupport -> navController.navigate(Routes.Support.route)

// In AppContent, inside the NavHost composable
composable(Routes.Support.route) {
    val supportViewModel: SupportViewModel = viewModel(factory = ludicoViewModelFactory)
    SupportScreen(navViewModel = navViewModel, supportViewModel = supportViewModel)
}
```

---

## 5. UI and ViewModel

### Step 5.1: The `HomeScreen` Entry Point (New Requirement)

Modify the `HomeScreen` to display two Floating Action Buttons. Since a `Scaffold` only accepts one FAB, we will wrap them in a `Column`.

**File to modify:** `app/src/main/java/com/example/ludico_app/screens/HomeScreen.kt`

```kotlin
// In HomeScreen.kt, find the Scaffold and replace the floatingActionButton
Scaffold(
    // ... topBar remains the same
    floatingActionButton = {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Support FAB
            FloatingActionButton(
                onClick = { navViewModel.onNavEvent(NavEvent.ToSupport) },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.SupportAgent, "Contact Support") // A more fitting icon
            }

            // Create Event FAB (existing)
            FloatingActionButton(
                onClick = { navViewModel.onNavEvent(NavEvent.ToCreateEvent) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Crear Evento")
            }
        }
    }
) { innerPadding ->
    // ... rest of the Scaffold content
}
```
*(You will need to add the `androidx.compose.material:material-icons-extended` dependency to your `build.gradle.kts` for the `SupportAgent` icon, or use another icon like `HelpOutline`)*

### Step 5.2: Create `SupportViewModel.kt`

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
    val submissionSuccess: Boolean = false,
    val errorMessage: String? = null
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = supportRepository.createTicket(
                    _uiState.value.email,
                    _uiState.value.subject,
                    _uiState.value.description
                )
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, submissionSuccess = true) }
                    kotlinx.coroutines.delay(1500)
                    navViewModel.onNavEvent(com.example.ludico_app.navigation.NavEvent.Back)
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to submit ticket. Please try again.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Network error. Please check your connection.") }
            }
        }
    }
}
```

### Step 5.3: Create `SupportScreen.kt`

**New File:** `app/src/main/java/com/example/ludico_app/screens/SupportScreen.kt`
```kotlin
package com.example.ludico_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
        topBar = {
            TopAppBar(
                title = { Text("Contact Support") },
                navigationIcon = {
                    IconButton(onClick = { navViewModel.onNavEvent(com.example.ludico_app.navigation.NavEvent.Back) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Please fill out the form below and our support team will get back to you shortly.")
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
            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
```