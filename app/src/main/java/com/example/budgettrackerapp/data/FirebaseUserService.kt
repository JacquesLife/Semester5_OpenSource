package com.example.budgettrackerapp.data

import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Firebase service for user operations, replaces UserDao
class FirebaseUserService {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")
    
    // Insert or update a user in Firebase
    suspend fun insertUser(user: User): Boolean {
        return suspendCancellableCoroutine { continuation ->
            // Generate a new key if user doesn't have one
            val userId = if (user.userId.isEmpty()) usersRef.push().key!! else user.userId
            val userWithId = user.copy(userId = userId)
            
            usersRef.child(userId).setValue(userWithId)
                .addOnSuccessListener { 
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    
    // Login user by username - searches all users for matching username
    suspend fun login(username: String): User? {
        return suspendCancellableCoroutine { continuation ->
            usersRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val user = userSnapshot.getValue(User::class.java)
                                continuation.resume(user)
                                return
                            }
                        }
                        continuation.resume(null)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
        }
    }
    
    // Check if username already exists
    suspend fun isUsernameExists(username: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            usersRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        continuation.resume(snapshot.exists())
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
        }
    }
} 