package com.example.ludico_app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ludico_app.LudicoApplication

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val application = applicationContext as LudicoApplication
        val repository = application.eventRepository
        return try {
            repository.syncEvents()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
