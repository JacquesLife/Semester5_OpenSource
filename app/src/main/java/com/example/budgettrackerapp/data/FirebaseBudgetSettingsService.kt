package com.example.budgettrackerapp.data

import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Firebase service for budget settings operations, replaces BudgetSettingsDao
class FirebaseBudgetSettingsService {
    private val database = FirebaseDatabase.getInstance()
    private val budgetSettingsRef = database.getReference("budgetSettings")
    
    // Insert or update budget settings for a user
    suspend fun insert(settings: BudgetSettings): Boolean {
        return suspendCancellableCoroutine { continuation ->
            // Use userId as the key for budget settings (one per user)
            val settingsId = if (settings.id.isEmpty()) settings.userId else settings.id
            val settingsWithId = settings.copy(id = settingsId)
            
            budgetSettingsRef.child(settingsId).setValue(settingsWithId)
                .addOnSuccessListener { 
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    
    // Get budget settings for a specific user
    suspend fun getSettings(userId: String): BudgetSettings? {
        return suspendCancellableCoroutine { continuation ->
            budgetSettingsRef.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val settings = snapshot.getValue(BudgetSettings::class.java)
                        continuation.resume(settings)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
        }
    }
    
    // Get the latest budget settings (for backwards compatibility)
    // This method can be removed if not needed
    suspend fun getSettings(): BudgetSettings? {
        return suspendCancellableCoroutine { continuation ->
            budgetSettingsRef.limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var latestSettings: BudgetSettings? = null
                        for (settingsSnapshot in snapshot.children) {
                            latestSettings = settingsSnapshot.getValue(BudgetSettings::class.java)
                        }
                        continuation.resume(latestSettings)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
        }
    }
    
    // Update budget settings
    suspend fun updateSettings(settings: BudgetSettings): Boolean {
        return suspendCancellableCoroutine { continuation ->
            budgetSettingsRef.child(settings.id).setValue(settings)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    
    // Delete budget settings
    suspend fun deleteSettings(userId: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            budgetSettingsRef.child(userId).removeValue()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
} 