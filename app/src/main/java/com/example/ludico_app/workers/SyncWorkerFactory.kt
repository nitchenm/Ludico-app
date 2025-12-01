package com.example.ludico_app.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.ludico_app.LudicoApplication

/**
 * A factory that avoids taking dependencies in its constructor. Instead, it gets them from the
 * Application instance when it needs to create the worker. This is crucial for avoiding
 * race conditions during app startup.
 */
class SyncWorkerFactory : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return if (workerClassName == SyncWorker::class.java.name) {
            // Get the repository from the application instance only when the worker is being created.
            val app = appContext.applicationContext as LudicoApplication
            SyncWorker(appContext, workerParameters, app.eventRepository)
        } else {
            null
        }
    }
}
