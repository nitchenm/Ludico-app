package com.example.ludico_app.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.ludico_app.data.repository.EventRepository

class SyncWorkerFactory(private val eventRepository: EventRepository) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name -> {
                SyncWorker(appContext, workerParameters, eventRepository)
            }
            else -> null
        }
    }
}
