package com.example.budgettrackerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Data Access Object (DAO) for interacting with the 'users' table
@Dao
interface UserDao {

    // Inserts a new user into the database
    // If a user with the same primary key exists, it will be replaced
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Retrieves a user by their username (used during login)
    // Returns null if no matching user is found
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun login(username: String): User?
}
