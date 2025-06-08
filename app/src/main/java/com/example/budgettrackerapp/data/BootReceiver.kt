package com.example.budgettrackerapp.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Restart notification scheduling after device reboot
            val notificationManager = ExpenseNotificationManager(context)
            notificationManager.scheduleNotificationChecks()
        }
    }
} 