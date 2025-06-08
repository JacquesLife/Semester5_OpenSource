package com.example.budgettrackerapp.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val TAG = "DateUtils"
    
    // Standard date formats used throughout the app
    private val INPUT_FORMATS = listOf(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
        SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()),
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    )
    
    private val OUTPUT_FORMAT_STORAGE = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val OUTPUT_FORMAT_DISPLAY = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val OUTPUT_FORMAT_DISPLAY_LONG = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    
    /**
     * Safely parse a date string using multiple formats
     * Returns the parsed Date or current date as fallback
     */
    fun parseDate(dateStr: String): Date {
        if (dateStr.isBlank()) {
            Log.w(TAG, "Empty date string, using current date")
            return Date()
        }
        
        val trimmedDate = dateStr.trim()
        
        for (format in INPUT_FORMATS) {
            try {
                val parsedDate = format.parse(trimmedDate)
                if (parsedDate != null) {
                    Log.d(TAG, "Successfully parsed date: $trimmedDate with format: ${format.toPattern()}")
                    return parsedDate
                }
            } catch (e: Exception) {
                // Continue to next format
            }
        }
        
        Log.e(TAG, "Failed to parse date: $trimmedDate, using current date as fallback")
        return Date()
    }
    
    /**
     * Safely parse a date string, returns null if parsing fails
     */
    fun parseDateOrNull(dateStr: String): Date? {
        if (dateStr.isBlank()) return null
        
        val trimmedDate = dateStr.trim()
        
        for (format in INPUT_FORMATS) {
            try {
                val parsedDate = format.parse(trimmedDate)
                if (parsedDate != null) {
                    return parsedDate
                }
            } catch (e: Exception) {
                // Continue to next format
            }
        }
        
        return null
    }
    
    /**
     * Format date for storage (yyyy-MM-dd)
     */
    fun formatForStorage(date: Date): String {
        return try {
            OUTPUT_FORMAT_STORAGE.format(date)
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting date for storage", e)
            OUTPUT_FORMAT_STORAGE.format(Date())
        }
    }
    
    /**
     * Format date for display (dd MMM yyyy)
     */
    fun formatForDisplay(dateStr: String): String {
        return try {
            val date = parseDate(dateStr)
            OUTPUT_FORMAT_DISPLAY.format(date)
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting date for display: $dateStr", e)
            dateStr.ifBlank { "Invalid Date" }
        }
    }
    
    /**
     * Format date for display with day of week (EEE, dd MMM yyyy)
     */
    fun formatForDisplayLong(dateStr: String): String {
        return try {
            val date = parseDate(dateStr)
            OUTPUT_FORMAT_DISPLAY_LONG.format(date)
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting date for long display: $dateStr", e)
            dateStr.ifBlank { "Invalid Date" }
        }
    }
    
    /**
     * Format date for display with day of week using Date object
     */
    fun formatForDisplayLong(date: Date): String {
        return try {
            OUTPUT_FORMAT_DISPLAY_LONG.format(date)
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting date for long display", e)
            "Invalid Date"
        }
    }
    
    /**
     * Check if a date string is within a date range (inclusive)
     */
    fun isDateInRange(dateStr: String, startDateStr: String, endDateStr: String): Boolean {
        return try {
            val date = parseDateOrNull(dateStr) ?: return false
            val startDate = parseDateOrNull(startDateStr) ?: return false
            val endDate = parseDateOrNull(endDateStr) ?: return false
            
            // Normalize times for accurate comparison
            val normalizedDate = normalizeDate(date)
            val normalizedStart = normalizeDate(startDate, isStartOfDay = true)
            val normalizedEnd = normalizeDate(endDate, isStartOfDay = false)
            
            normalizedDate >= normalizedStart && normalizedDate <= normalizedEnd
        } catch (e: Exception) {
            Log.e(TAG, "Error checking date range", e)
            false
        }
    }
    
    /**
     * Format due date with relative information
     */
    fun formatDueDate(dateStr: String): String {
        return try {
            val dueDate = parseDate(dateStr)
            val today = Date()
            val daysDiff = ((dueDate.time - today.time) / (1000 * 60 * 60 * 24)).toInt()
            
            when {
                daysDiff < 0 -> "Overdue (${formatForDisplay(dateStr)})"
                daysDiff == 0 -> "Due Today"
                daysDiff == 1 -> "Due Tomorrow"
                daysDiff <= 7 -> "Due in $daysDiff days"
                else -> "Due ${formatForDisplay(dateStr)}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting due date: $dateStr", e)
            dateStr.ifBlank { "Invalid Date" }
        }
    }
    
    /**
     * Get current date formatted for storage
     */
    fun getCurrentDateForStorage(): String {
        return formatForStorage(Date())
    }
    
    /**
     * Get current date formatted for display
     */
    fun getCurrentDateForDisplay(): String {
        return formatForDisplay(getCurrentDateForStorage())
    }
    
    /**
     * Normalize date for comparison (set to noon for date, start/end of day for ranges)
     */
    private fun normalizeDate(date: Date, isStartOfDay: Boolean? = null): Date {
        val calendar = Calendar.getInstance().apply { time = date }
        
        when (isStartOfDay) {
            true -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            false -> {
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
            }
            null -> {
                calendar.set(Calendar.HOUR_OF_DAY, 12)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }
        
        return calendar.time
    }
} 