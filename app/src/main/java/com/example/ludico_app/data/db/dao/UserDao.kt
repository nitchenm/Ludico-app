package com.example.ludico_app.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.ludico_app.data.entities.User
import kotlinx.coroutines.flow.Flow
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow<User?> // Usamos Flow para que se actualice si el usuario cambia
}