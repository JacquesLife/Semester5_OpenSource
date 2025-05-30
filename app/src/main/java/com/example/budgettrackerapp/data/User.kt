package com.example.budgettrackerapp.data

// User data class for Firebase Realtime Database
data class User(
    // User ID will be generated by Firebase
    val userId: String = "",

    // Username used for login; should be unique
    val username: String = "",

    // Hashed password stored securely
    val password: String = "",

    // Optional profile picture stored as a base64 string or URL
    val profilePicture: String? = null
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", null)
}
