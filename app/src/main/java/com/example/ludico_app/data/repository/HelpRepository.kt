// In app/src/main/java/com/example/ludico_app/data/repository/HelpRepository.kt
package com.example.ludico_app.data.repository

import com.example.ludico_app.data.remote.ApiService

/**
 * Repository for handling help-related data operations.
 * It communicates with the ApiService to fetch data from the remote server.
 */
class HelpRepository(private val apiService: ApiService) {

    // You can add methods here to interact with the ApiService
    // For example:
    // suspend fun getHelpTopics(): List<HelpTopic> {
    //     return apiService.fetchHelpTopics()
    // }
}
