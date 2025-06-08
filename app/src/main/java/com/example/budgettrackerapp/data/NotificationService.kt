package com.example.budgettrackerapp.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.budgettrackerapp.MainActivity
import com.example.budgettrackerapp.R

class NotificationService(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "expense_due_notifications"
        private const val CHANNEL_NAME = "Expense Due Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for expenses that are due soon"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun sendExpenseDueNotification(expense: Expense, daysUntilDue: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = when {
            daysUntilDue == 0 -> "Expense Due Today!"
            daysUntilDue == 1 -> "Expense Due Tomorrow!"
            else -> "Expense Due in $daysUntilDue Days"
        }
        
        val message = "${expense.description} (${expense.category}) - $${expense.amount} is due ${
            when {
                daysUntilDue == 0 -> "today"
                daysUntilDue == 1 -> "tomorrow"
                else -> "in $daysUntilDue days"
            }
        }"
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.bell)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(expense.id.hashCode(), builder.build())
            }
        } catch (e: SecurityException) {
            // Handle permission denied
            e.printStackTrace()
        }
    }
    
    fun cancelNotification(expenseId: String) {
        with(NotificationManagerCompat.from(context)) {
            cancel(expenseId.hashCode())
        }
    }
} 