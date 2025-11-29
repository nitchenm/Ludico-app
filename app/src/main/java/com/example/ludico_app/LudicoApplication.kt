package com.example.ludico_app

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ludico_app.data.db.dao.LudicoDatabase
import com.example.ludico_app.data.remote.RetrofitInstance
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.workers.SyncWorker
import java.util.concurrent.TimeUnit

class LudicoApplication : Application(), Configuration.Provider {

    val database: LudicoDatabase by lazy { LudicoDatabase.getDatabase(this) }

    val eventRepository: EventRepository by lazy {
        EventRepository(database.eventDao(), database.userDao(), RetrofitInstance.api)
    }

    override fun onCreate() {
        super.onCreate()
        setupRecurringWork()
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(
            repeatingRequest
        )
    }

    override val workManagerConfiguration:
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}