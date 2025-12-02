package com.example.ludico_app

import android.app.Application
import android.se.omapi.Session
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ludico_app.data.db.dao.LudicoDatabase
import com.example.ludico_app.data.remote.RetrofitInstance
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.data.repository.SupportRepository
import com.example.ludico_app.data.repository.UserRepository
import com.example.ludico_app.data.session.SessionManager
import com.example.ludico_app.workers.SyncWorker
import com.example.ludico_app.workers.SyncWorkerFactory
import java.util.concurrent.TimeUnit

// 1. Remove Configuration.Provider from the class definition
class LudicoApplication : Application() {

    val database: LudicoDatabase by lazy { LudicoDatabase.getDatabase(this) }

    val supportRepository: SupportRepository by lazy{
        SupportRepository(RetrofitInstance.api)
    }

    val sessionManager: SessionManager by lazy{
        SessionManager(this)
    }


    val eventRepository: EventRepository by lazy {
        EventRepository(database.eventDao(), database.userDao(), RetrofitInstance.api)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(RetrofitInstance.api)
    }

    override fun onCreate() {
        super.onCreate()

        // 2. Manually initialize WorkManager
        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(SyncWorkerFactory())
            .build()
        WorkManager.initialize(this, workManagerConfig)

        // Now schedule the work
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

        WorkManager.getInstance(applicationContext).enqueue(repeatingRequest)
    }

    // 3. The getWorkManagerConfiguration() method is no longer needed.
}
