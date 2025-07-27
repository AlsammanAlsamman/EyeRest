package com.example.eyerestreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class EyeRestReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val preferencesManager = PreferencesManager(context)
        val notificationHelper = NotificationHelper(context)
        
        when (intent.action) {
            "TIMER_TICK" -> handleTimerTick(context, preferencesManager, notificationHelper)
            "CANCEL_TIMER" -> handleCancelTimer(context, preferencesManager, notificationHelper)
            "CANCEL_REST" -> handleCancelRest(context, preferencesManager, notificationHelper)
            else -> handleTimerTick(context, preferencesManager, notificationHelper)
        }
    }
    
    private fun handleTimerTick(context: Context, preferencesManager: PreferencesManager, notificationHelper: NotificationHelper) {
        if (!preferencesManager.isTimerRunning) {
            return
        }
        
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - preferencesManager.timerStartTime
        
        val totalDurationMs = when (preferencesManager.currentPhase) {
            "work" -> preferencesManager.workMinutes * 60 * 1000L
            "rest" -> preferencesManager.restMinutes * 60 * 1000L
            else -> preferencesManager.workMinutes * 60 * 1000L
        }
        
        if (elapsedTime >= totalDurationMs) {
            // Time's up! Switch phases
            when (preferencesManager.currentPhase) {
                "work" -> startRestPhase(context, preferencesManager, notificationHelper)
                "rest" -> startWorkPhase(context, preferencesManager, notificationHelper)
            }
        } else {
            // Update progress notification
            val remainingMinutes = ((totalDurationMs - elapsedTime) / 60000).toInt()
            
            if (preferencesManager.currentPhase == "work") {
                notificationHelper.showWorkTimeNotification(remainingMinutes)
                
                // Check if we should show warning notification
                if (remainingMinutes == preferencesManager.warningMinutes && remainingMinutes > 0) {
                    notificationHelper.showWarningNotification(remainingMinutes)
                }
            }
            
            // Schedule next tick
            scheduleNextTick(context)
        }
    }
    
    private fun startRestPhase(context: Context, preferencesManager: PreferencesManager, notificationHelper: NotificationHelper) {
        preferencesManager.currentPhase = "rest"
        preferencesManager.timerStartTime = System.currentTimeMillis()
        
        notificationHelper.cancelWorkNotification()
        notificationHelper.showRestTimeNotification(preferencesManager.restMinutes)
        
        // Start rest activity
        val restIntent = Intent(context, RestActivity::class.java)
        restIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(restIntent)
        
        scheduleNextTick(context)
    }
    
    private fun startWorkPhase(context: Context, preferencesManager: PreferencesManager, notificationHelper: NotificationHelper) {
        preferencesManager.currentPhase = "work"
        preferencesManager.timerStartTime = System.currentTimeMillis()
        
        notificationHelper.cancelRestNotification()
        notificationHelper.showWorkTimeNotification(preferencesManager.workMinutes)
        
        scheduleNextTick(context)
    }
    
    private fun handleCancelTimer(context: Context, preferencesManager: PreferencesManager, notificationHelper: NotificationHelper) {
        preferencesManager.resetTimer()
        cancelAllAlarms(context)
        notificationHelper.cancelAllNotifications()
    }
    
    private fun handleCancelRest(context: Context, preferencesManager: PreferencesManager, notificationHelper: NotificationHelper) {
        // Skip rest and go back to work
        startWorkPhase(context, preferencesManager, notificationHelper)
    }
    
    private fun scheduleNextTick(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EyeRestReceiver::class.java)
        intent.action = "TIMER_TICK"
        
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (60 * 1000) // Every minute
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
    
    private fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EyeRestReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}