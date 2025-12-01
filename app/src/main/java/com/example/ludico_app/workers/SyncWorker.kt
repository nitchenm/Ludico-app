package com.example.ludico_app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ludico_app.data.repository.EventRepository

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val eventRepository: EventRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            eventRepository.refreshEvents()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
