# Analysis of the "Tickets" Feature Implementation

## 1. Current Implementation

Based on the project analysis, a foundational but incomplete structure for a ticket/RSVP system has been implemented.

### Data & Network Layer:

*   **API Endpoint:** An endpoint to create a ticket is defined in `ApiService.kt`:
    ```kotlin
    @POST("api/tickets/create")
    suspend fun createTicket(@Body request: CreateTicketRequest): TicketResponse
    ```
*   **Data Models:** The project includes `CreateTicketRequest.kt` and `TicketResponse.kt` to model the data sent to and received from the API for ticket creation.
*   **Repository Logic:** There is **no repository function** implemented that calls `apiService.createTicket()`. This is a major gap.
*   **Local Storage:** There is **no local `Ticket` entity or DAO**. This means the app currently has no way to remember or store which events a user has joined after the app closes.

### ViewModel Layer:

*   **`EventDetailViewModel.kt`**: A placeholder function `toggleRsvp()` exists. This function is intended to handle the logic for joining or leaving an event but is currently empty.
    ```kotlin
    fun toggleRsvp() {
        // TODO: Implementar la lógica para unirse/abandonar un evento.
    }
    ```
*   **`EventDetailUiState.kt`**: This UI state model correctly contains an `RsvpState` enum (`JOINED`, `NOT_JOINED`, `FULL`). This is designed to drive the UI, but the logic to update this state is missing.

### UI (Screen) Layer:

*   **`EventDetailScreen.kt`**: This is the primary screen where the feature is visible to the user.
    *   **UI Trigger:** An `ExtendedFloatingActionButton` (FAB) is displayed, which serves as the "Join" or "Leave" button.
    *   **Action Wiring:** The `onClick` of this FAB is correctly wired to call `eventDetailViewModel::toggleRsvp`.
    *   **Visual State:** The FAB's appearance (text, icon, and color) correctly changes based on the `rsvpState` in the `EventDetailUiState`. This part of the UI is ready, but the state itself is never updated from its default `NOT_JOINED` value.

## 2. Gaps and Recommended Changes

The feature is non-functional because the logic connecting the UI trigger to the backend API call is missing.

### Step 1: Implement the Repository Function

In `EventRepository.kt`, you need to create a new function that calls the API service.

```kotlin
// In EventRepository.kt
suspend fun createTicketForEvent(userId: String, eventId: String): TicketResponse {
    val request = CreateTicketRequest(userId = userId, eventId = eventId)
    return apiService.createTicket(request)
}
```

### Step 2: Implement the ViewModel Logic

The `toggleRsvp()` function in `EventDetailViewModel.kt` needs to be implemented. This is the most critical part.

1.  **Get User ID**: You need the current user's ID. This should be retrieved from your `SessionManager`. You will have to inject the `SessionManager` into the `LudicoViewModelFactory` and then into the `EventDetailViewModel`.
2.  **Call the Repository**: Call the new repository function.
3.  **Update UI State**: Based on the result of the API call, update the `rsvpState` in the `_uiState`.
4.  **Handle Errors**: Wrap the call in a `try-catch` block to handle network errors or cases where the ticket cannot be created (e.g., the event is full).

**Example Implementation in `EventDetailViewModel.kt`:**

```kotlin
// You will need to add sessionManager to the constructor
class EventDetailViewModel(
    private val eventRepository: EventRepository,
    private val sessionManager: SessionManager, // <-- ADD THIS
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    //...

    fun toggleRsvp() {
        viewModelScope.launch {
            val eventId = uiState.value.event?.eventId ?: return@launch
            val userId = sessionManager.fetchUserId() // <-- You'll need to implement fetchUserId() in SessionManager

            if (userId == null) {
                // Handle error: user is not logged in
                return@launch
            }

            // Prevent action if already joined
            if (uiState.value.rsvpState == RsvpState.JOINED) {
                // TODO: Implement logic to *delete* a ticket (leave an event)
                Log.d("AppDebug", "User is already joined. Leaving is not implemented yet.")
                return@launch
            }

            try {
                val ticketResponse = eventRepository.createTicketForEvent(userId, eventId)

                // IMPORTANT: Your logic here depends on what your API returns in TicketResponse.
                // Assuming it confirms the creation.
                Log.d("AppDebug", "Successfully created ticket: ${ticketResponse.ticketId}")

                // Update the UI state to reflect the change
                _uiState.update { it.copy(rsvpState = RsvpState.JOINED) }

            } catch (e: Exception) {
                Log.e("AppDebug", "Failed to create ticket", e)
                // TODO: Update UI to show an error message to the user
            }
        }
    }
    // ...
}
```

### Step 3: Add Local Storage (Persistence)

To make the "Joined" state persist after the user closes the app, you must store ticket information locally.

1.  **Create a `Ticket` Entity:** Create a new Room `@Entity` data class for `Ticket`. It should at least contain the `eventId` and `userId`.
2.  **Create a `TicketDao`:** Create a DAO with functions like `insert(ticket: Ticket)` and `getTicketForEvent(userId: String, eventId: String): Ticket?`.
3.  **Update `LudicoDatabase`:** Add the new `Ticket` entity to the `entities` array in your `LudicoDatabase` class.
4.  **Modify Repository & ViewModel:**
    *   When the ViewModel loads, it should check the local database (via the repository) to see if a ticket already exists for that user and event, and set the initial `rsvpState` accordingly.
    *   After a successful API call to create a ticket, save the new `Ticket` to the local database.

## 3. Summary of UI Integration

*   **Location:** `EventDetailScreen.kt`
*   **Component:** The `RsvpFab` (ExtendedFloatingActionButton) at the bottom of the screen.
*   **Current State:** The button's appearance is already wired to react to changes in `uiState.rsvpState`. Once the ViewModel logic is implemented to update this state, the UI will automatically reflect it.
