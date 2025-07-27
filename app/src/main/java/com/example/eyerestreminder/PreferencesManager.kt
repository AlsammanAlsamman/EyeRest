package com.example.eyerestreminder

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "eye_rest_prefs"
        private const val KEY_WORK_MINUTES = "work_minutes"
        private const val KEY_REST_MINUTES = "rest_minutes"
        private const val KEY_WARNING_MINUTES = "warning_minutes"
        private const val KEY_IS_TIMER_RUNNING = "is_timer_running"
        private const val KEY_TIMER_START_TIME = "timer_start_time"
        private const val KEY_CURRENT_PHASE = "current_phase" // "work" or "rest"
        private const val KEY_WARNING_SHOWN = "warning_shown"
        
        private const val DEFAULT_WORK_MINUTES = 20
        private const val DEFAULT_REST_MINUTES = 2
        private const val DEFAULT_WARNING_MINUTES = 2
    }
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    var workMinutes: Int
        get() = sharedPreferences.getInt(KEY_WORK_MINUTES, DEFAULT_WORK_MINUTES)
        set(value) = sharedPreferences.edit().putInt(KEY_WORK_MINUTES, value).apply()
    
    var restMinutes: Int
        get() = sharedPreferences.getInt(KEY_REST_MINUTES, DEFAULT_REST_MINUTES)
        set(value) = sharedPreferences.edit().putInt(KEY_REST_MINUTES, value).apply()
    
    var warningMinutes: Int
        get() = sharedPreferences.getInt(KEY_WARNING_MINUTES, DEFAULT_WARNING_MINUTES)
        set(value) = sharedPreferences.edit().putInt(KEY_WARNING_MINUTES, value).apply()
    
    var isTimerRunning: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_TIMER_RUNNING, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_TIMER_RUNNING, value).apply()
    
    var timerStartTime: Long
        get() = sharedPreferences.getLong(KEY_TIMER_START_TIME, 0L)
        set(value) = sharedPreferences.edit().putLong(KEY_TIMER_START_TIME, value).apply()
    
    var currentPhase: String
        get() = sharedPreferences.getString(KEY_CURRENT_PHASE, "work") ?: "work"
        set(value) = sharedPreferences.edit().putString(KEY_CURRENT_PHASE, value).apply()
    
    var warningShown: Boolean
        get() = sharedPreferences.getBoolean(KEY_WARNING_SHOWN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_WARNING_SHOWN, value).apply()
    
    fun resetTimer() {
        isTimerRunning = false
        timerStartTime = 0L
        currentPhase = "work"
        warningShown = false
    }
}
