package com.example.budgettrackerapp.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ExpenseNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            checkForDueExpenses()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun checkForDueExpenses() {
        val notificationService = NotificationService(applicationContext)
        val currentUser = getCurrentUser() ?: return
        
        // Get all expenses for current user from Firebase
        val expenses = getExpensesFromFirebase(currentUser.userId)
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        
        expenses.forEach { expense ->
            if (expense.date.isNotEmpty() && expense.notificationEnabled) {
                try {
                    val dueDate = LocalDate.parse(expense.date, formatter)
                    val daysUntilDue = ChronoUnit.DAYS.between(today, dueDate).toInt()
                    
                    // Send notification if expense is due within the specified notification period
                    if (daysUntilDue >= 0 && daysUntilDue <= expense.notificationDaysBefore) {
                        notificationService.sendExpenseDueNotification(expense, daysUntilDue)
                    }
                } catch (e: Exception) {
                    // Handle date parsing errors
                    e.printStackTrace()
                }
            }
        }
    }
    
    private suspend fun getCurrentUser(): User? {

        val sharedPrefs = applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("current_user_id", null)
        val username = sharedPrefs.getString("current_username", null)
        
        return if (userId != null && username != null) {
            User(userId = userId, username = username, password = "")
        } else {
            null
        }
    }
    
    private suspend fun getExpensesFromFirebase(userId: String): List<Expense> {
        return try {
            val firebaseService = FirebaseExpenseService()
            firebaseService.getAllExpenses(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }
} 