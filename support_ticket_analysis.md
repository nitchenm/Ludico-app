# Analysis of the "Support Ticket" Feature Implementation

This report outlines the strategy for implementing a feature that allows users to create a support ticket for a specific event.

## 1. Current State & Repurposing Existing Code

The project contains an API endpoint that can be repurposed for this feature:

*   **API Endpoint:** In `ApiService.kt`, the `createTicket` endpoint is generic enough to be used for creating a support ticket.
    ```kotlin
    @POST("api/tickets/create")
    suspend fun createTicket(@Body request: CreateTicketRequest): TicketResponse
    ```
*   **UI Location:** The `EventDetailScreen.kt` is the ideal place to initiate this action, as it allows the ticket to be associated with a specific event.

The primary challenge is that `EventDetailScreen` already has a Floating Action Button (FAB) for the RSVP ("Join/Leave Event") functionality. Adding a second FAB is not recommended as it clutters the UI.

## 2. Recommended Implementation Strategy

The best approach is to add a "Report an issue" option to the menu in the top app bar of the event detail screen. This keeps the UI clean and follows standard Android design patterns.

### Step 1: Add a "Report Issue" Menu Item

In `EventDetailScreen.kt`, modify the `DetailTopAppBar` to include a menu item for reporting an issue.

**File to check:** `EventDetailScreen.kt`

```kotlin
// Inside the 'actions' lambda of the TopAppBar
var showMenu by remember { mutableStateOf(false) }

IconButton(onClick = { showMenu = true }) {
    Icon(Icons.Default.MoreVert, contentDescription = "More options")
}

DropdownMenu(
    expanded = showMenu,
    onDismissRequest = { showMenu = false }
) {
    DropdownMenuItem(
        text = { Text("Report an issue") },
        onClick = {
            showMenu = false
            // This is where we will trigger navigation to the new screen
            navViewModel.onNavEvent(NavEvent.ToSupportTicket(uiState.event!!.eventId))
        }
    )
}
```

### Step 2: Create Navigation for the Support Screen

You need to define a new route and navigation event for the support ticket screen.

1.  **`Routes.kt`:** Add a new route.
    ```kotlin
    // Add this inside the Routes sealed class
    data object SupportTicket : Routes("support_ticket/{eventId}") {
        fun createRoute(eventId: String) = "support_ticket/$eventId"
    }
    ```
2.  **`NavEvent.kt`:** Add a new navigation event.
    ```kotlin
    // Add this inside the NavEvent sealed interface
    data class ToSupportTicket(val eventId: String) : NavEvent
    ```
3.  **`MainActivity.kt`:** Handle the new navigation event in `AppContent` and define the new composable in the `NavHost`.
    ```kotlin
    // In the 'when' block of the LaunchedEffect
    is NavEvent.ToSupportTicket -> navController.navigate(Routes.SupportTicket.createRoute(event.eventId))

    // Inside the NavHost builder
    composable(
        route = Routes.SupportTicket.route,
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })
    ) {
        // We will create this screen and viewmodel next
        val supportViewModel: SupportViewModel = viewModel(factory = ludicoViewModelFactory)
        SupportTicketScreen(navViewModel = navViewModel, supportViewModel = supportViewModel)
    }
    ```

### Step 3: Create the Support Ticket Screen & ViewModel

This involves creating new files for the UI and the logic.

1.  **Create `SupportTicketScreen.kt`:**
    *   This new screen will be a simple form with a `TextField` for the user to describe their issue and a "Submit" button.
    *   It will use a new `SupportViewModel`.

2.  **Create `SupportViewModel.kt`:**
    *   This new ViewModel will manage the state of the form (e.g., the text of the description, loading state).
    *   It will have a function `submitTicket(description: String)`.
    *   It will need to receive the `eventId` from the navigation `SavedStateHandle`.

3.  **Update `LudicoViewModelFactory.kt`:** Add the logic to create the new `SupportViewModel`.
    ```kotlin
    // Inside the 'when' block of the factory
    modelClass.isAssignableFrom(SupportViewModel::class.java) -> {
        SupportViewModel(navViewModel, eventRepository, sessionManager, savedStateHandle) as T
    }
    ```

### Step 4: Implement the Repository Logic

Update the `EventRepository.kt` to include a function that calls the `createTicket` endpoint, tailored for support tickets.

*Note: You may need to adjust the `CreateTicketRequest` data class to include a `reason` or `description` field, depending on what your backend API expects.*

**File to check:** `EventRepository.kt`

```kotlin
// Add this new function to EventRepository
suspend fun createSupportTicket(userId: String, eventId: String, reason: String): TicketResponse {
    // Assuming CreateTicketRequest can be adapted or a new DTO is made
    val request = CreateTicketRequest(userId = userId, eventId = eventId, reason = reason)
    return apiService.createTicket(request)
}
```

### Step 5: Implement the ViewModel Logic

In the new `SupportViewModel`, implement the `submitTicket` function to call the repository.

```kotlin
// Inside SupportViewModel.kt
fun submitTicket(description: String) {
    viewModelScope.launch {
        val eventId = savedStateHandle.get<String>("eventId") ?: return@launch
        val userId = sessionManager.fetchUserId() ?: return@launch // Assumes fetchUserId exists

        try {
            // ... set loading state to true ...
            eventRepository.createSupportTicket(userId, eventId, description)
            // ... handle success, maybe navigate back ...
            navViewModel.onNavEvent(NavEvent.Back)
        } catch (e: Exception) {
            // ... handle error, show a message to the user ...
        }
    }
}
```

## 3. Summary of Changes

*   **Modify `EventDetailScreen.kt`**: Add a "Report an issue" option to the top app bar menu.
*   **Modify `Routes.kt` & `NavEvent.kt`**: Add new navigation definitions for the support screen.
*   **Modify `MainActivity.kt`**: Add the new screen to the `NavHost`.
*   **Modify `LudicoViewModelFactory.kt`**: Teach the factory how to create `SupportViewModel`.
*   **Modify `EventRepository.kt`**: Add a `createSupportTicket` function.
*   **Create `SupportTicketScreen.kt`**: New file for the support ticket UI.
*   **Create `SupportViewModel.kt`**: New file for the support ticket logic.
*   **(Potentially) Modify `CreateTicketRequest.kt`**: Add a field for the support message if the API requires it.
