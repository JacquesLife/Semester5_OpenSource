package com.example.budgettrackerapp.data

import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Firebase service for expense operations, replaces ExpenseDao
class FirebaseExpenseService {
    private val database = FirebaseDatabase.getInstance()
    private val expensesRef = database.getReference("expenses")
    
    // Insert a new expense into Firebase
    suspend fun insert(expense: Expense): String {
        return suspendCancellableCoroutine { continuation ->
            // Generate a new key if expense doesn't have one
            val expenseId = if (expense.id.isEmpty()) expensesRef.push().key!! else expense.id
            val expenseWithId = expense.copy(id = expenseId)
            
            expensesRef.child(expenseId).setValue(expenseWithId)
                .addOnSuccessListener { 
                    continuation.resume(expenseId)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    
    // Get all expenses for a specific user
    suspend fun getAllExpenses(userId: String): List<Expense> {
        return suspendCancellableCoroutine { continuation ->
            expensesRef.orderByChild("userOwnerId").equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val expenses = mutableListOf<Expense>()
                        for (expenseSnapshot in snapshot.children) {
                            val expense = expenseSnapshot.getValue(Expense::class.java)
                            expense?.let { expenses.add(it) }
                        }
                        continuation.resume(expenses)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
        }
    }
    
    // Get total amount for a specific category and user
    suspend fun getTotalForCategory(category: String, userId: String): Double {
        return suspendCancellableCoroutine { continuation ->
            expensesRef.orderByChild("userOwnerId").equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var total = 0.0
                        for (expenseSnapshot in snapshot.children) {
                            val expense = expenseSnapshot.getValue(Expense::class.java)
                            if (expense?.category == category) {
                                total += expense.amount
                            }
                        }
                        continuation.resume(total)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
        }
    }
    
    // Delete an expense
    suspend fun deleteExpense(expenseId: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            expensesRef.child(expenseId).removeValue()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    
    // Update an expense
    suspend fun updateExpense(expense: Expense): Boolean {
        return suspendCancellableCoroutine { continuation ->
            expensesRef.child(expense.id).setValue(expense)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
} 