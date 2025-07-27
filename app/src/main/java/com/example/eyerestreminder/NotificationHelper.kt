package com.example.eyerestreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "eye_rest_channel"
        private const val WORK_NOTIFICATION_ID = 1
        private const val REST_NOTIFICATION_ID = 2
        private const val WARNING_NOTIFICATION_ID = 3
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Eye Rest Reminders"
            val descriptionText = "Notifications for eye rest reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                setSound(getCustomSound(), null)
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun getCustomSound(): Uri {
        return try {
            // Try to use custom sound from assets
            val assetManager = context.assets
            val inputStream = assetManager.open("sounds/chill_alert.mp3")
            val file = File(context.filesDir, "chill_alert.mp3")
            val outputStream = FileOutputStream(file)
            
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            
            Uri.fromFile(file)
        } catch (e: IOException) {
            // Fallback to default notification sound
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }
    
    fun showWorkTimeNotification(minutesRemaining: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val cancelIntent = Intent(context, EyeRestReceiver::class.java).apply {
            action = "CANCEL_TIMER"
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, 1, cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_eye_work)
            .setContentTitle("Work Time")
            .setContentText("$minutesRemaining minutes remaining until eye rest")
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_cancel, "Cancel", cancelPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(WORK_NOTIFICATION_ID, notification)
        }
    }
    
    fun showRestTimeNotification(restMinutes: Int) {
        playCustomSound()
        
        val intent = Intent(context, RestActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val cancelIntent = Intent(context, EyeRestReceiver::class.java).apply {
            action = "CANCEL_REST"
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, 2, cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_eye_rest)
            .setContentTitle("Eye Rest Time!")
            .setContentText("Take a $restMinutes minute break and look at something 20 feet away")
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_cancel, "Cancel", cancelPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(REST_NOTIFICATION_ID, notification)
        }
    }
    
    fun showWarningNotification(minutesRemaining: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_eye_rest)
            .setContentTitle("⚠️ Eye Rest Warning")
            .setContentText("Eye rest break in $minutesRemaining minutes! Prepare to take a break.")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(getCustomSound())
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(WARNING_NOTIFICATION_ID, notification)
        }
    }
    
    fun playCustomSound() {
        try {
            val mediaPlayer = MediaPlayer()
            val file = File(context.filesDir, "chill_alert.mp3")
            if (file.exists()) {
                mediaPlayer.setDataSource(file.absolutePath)
                mediaPlayer.prepare()
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener { mp ->
                    mp.release()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun cancelWorkNotification() {
        with(NotificationManagerCompat.from(context)) {
            cancel(WORK_NOTIFICATION_ID)
        }
    }
    
    fun cancelRestNotification() {
        with(NotificationManagerCompat.from(context)) {
            cancel(REST_NOTIFICATION_ID)
        }
    }
    
    fun cancelWarningNotification() {
        with(NotificationManagerCompat.from(context)) {
            cancel(WARNING_NOTIFICATION_ID)
        }
    }
    
    fun cancelAllNotifications() {
        with(NotificationManagerCompat.from(context)) {
            cancelAll()
        }
    }
}
