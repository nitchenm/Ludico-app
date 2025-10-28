package com.example.ludico_app

import android.app.Application
import com.example.ludico_app.data.repository.EventRepository
import com.example.ludico_app.data.db.dao.LudicoDatabase

/**
 * Clase principal de la aplicación. Se inicializa una sola vez cuando la app se lanza.
 * Es el lugar ideal para crear instancias de objetos que deben ser compartidos
 * por toda la aplicación, como la base de datos y los repositorios.
 */
class LudicoApplication : Application() {

    // Usamos 'lazy' para que la base de datos y el repositorio se creen solo cuando se necesiten por primera vez.
    // Esto mejora el rendimiento de arranque de la aplicación.

    /**
     * Instancia única de la base de datos para toda la aplicación.
     */
    val database: LudicoDatabase by lazy { LudicoDatabase.getDatabase(this) }

    /**
     * Instancia única del repositorio de eventos.
     * Le pasamos el DAO de la base de datos que necesita para funcionar.
     */
    val eventRepository: EventRepository by lazy { EventRepository(database.eventDao(), database.userDao()) }
}