package com.tejasdev.bunkbuddy.util

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class AppReviewHelper @Inject constructor(
    private val context: Context
){

    private val prefs: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    fun hasReviewed(): Boolean {
        return prefs.getBoolean("hasReviewed", false)
    }

    fun setReviewed() {
        prefs.edit().putBoolean("hasReviewed", true).apply()
    }

    fun clearReviewed() {
        prefs.edit().remove("hasReviewed").apply()
    }
}