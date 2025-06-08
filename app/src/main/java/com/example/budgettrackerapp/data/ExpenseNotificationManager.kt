package com.example.budgettrackerapp.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ExpenseNotificationManager(private val context: Context) {
    
    companion object {
        private const val WORK_NAME = "expense_notification_check"
    }
    
    fun scheduleNotificationChecks() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<ExpenseNotificationWorker>(
            12, TimeUnit.HOURS  // Check every 12 hours
        )
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    fun cancelNotificationChecks() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
    
    fun scheduleImmediateCheck() {
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<ExpenseNotificationWorker>()
            .build()
        
        WorkManager.getInstance(context).enqueue(workRequest)
    }
    
    // For testing - send a test notification immediately
    fun sendTestNotification() {
        val notificationService = NotificationService(context)
        val testExpense = Expense(
            id = "test",
            description = "Test Bill",
            category = "Utilities",
            amount = 100.0,
            dueDate = "2025-01-10",
            userOwnerId = "test"
        )
        notificationService.sendExpenseDueNotification(testExpense, 1)
    }
} 