package com.example.ludico_app.data.db.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ludico_app.data.db.dao.EventDao
import com.example.ludico_app.data.entities.Event
import com.example.ludico_app.data.entities.User
import com.example.ludico_app.data.entities.Comment

/**
 * Clase principal de la base de datos de Room.
 * Une las entidades (tablas) y los DAOs (consultas).
 */
@Database(
    entities = [User::class, Event::class, Comment::class], // Lista de todas las tablas
    version = 1, // Si cambias la estructura de una tabla, debes incrementar la versión.
    exportSchema = false // No exportar el esquema de la BD a un archivo JSON.
)
abstract class LudicoDatabase : RoomDatabase() {

    // Provee una instancia del DAO para que el resto de la app pueda usarlo.
    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao
    // abstract fun userDao(): UserDao  // Descomentarás esto cuando crees el UserDao.
    // abstract fun commentDao(): CommentDao

    companion object {
        // La anotación @Volatile asegura que la instancia sea siempre la más reciente y visible para todos los hilos.
        @Volatile
        private var INSTANCE: LudicoDatabase? = null

        /**
         * Obtiene la instancia de la base de datos.
         * Usa el patrón Singleton para asegurar que solo exista una instancia de la BD en toda la app,
         * lo cual es crucial para el rendimiento y la consistencia de los datos.
         */
        fun getDatabase(context: Context): LudicoDatabase {
            // Si la instancia ya existe, la devuelve. Si no, crea una nueva.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LudicoDatabase::class.java,
                    "ludico_database" // Nombre del archivo de la base de datos en el dispositivo.
                )
                    .fallbackToDestructiveMigration() // En desarrollo, si cambias la versión, la BD se recrea.
                    // ¡Cuidado! En producción necesitarás un plan de migración real.
                    .build()
                INSTANCE = instance
                instance // Devuelve la instancia recién creada.
            }
        }
    }
}