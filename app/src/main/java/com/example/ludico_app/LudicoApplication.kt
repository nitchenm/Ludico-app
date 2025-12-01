package com.example.ludico_app

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ludico_app.data.db.dao.LudicoDatabase
import com.example.ludico_app.data.remote.ApiService
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.data.repository.UserRepository
import com.example.ludico_app.data.session.AuthInterceptor
import com.example.ludico_app.data.session.SessionManager
import com.example.ludico_app.workers.SyncWorker
import com.example.ludico_app.workers.SyncWorkerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class LudicoApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized = _isInitialized.asStateFlow()

    lateinit var database: LudicoDatabase
    lateinit var sessionManager: SessionManager
    lateinit var apiService: ApiService
    lateinit var eventRepository: EventRepository
    lateinit var userRepository: UserRepository

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize WorkManager immediately on the main thread.
        // This uses the new factory which doesn't need dependencies, avoiding the crash.
        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(SyncWorkerFactory())
            .build()
        WorkManager.initialize(this, workManagerConfig)

        // 2. Start other async initializations.
        initializeDependencies()

        // 3. Schedule the recurring work.
        setupRecurringWork()
    }

    private fun initializeDependencies() {
        applicationScope.launch(Dispatchers.IO) {
            sessionManager = SessionManager(applicationContext)
            val authInterceptor = AuthInterceptor(sessionManager)
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)

            database = LudicoDatabase.getDatabase(this@LudicoApplication)
            eventRepository = EventRepository(database.eventDao(), database.userDao(), apiService)
            userRepository = UserRepository(apiService)

            // Signal to the UI that it can now load.
            _isInitialized.value = true
        }
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
}
