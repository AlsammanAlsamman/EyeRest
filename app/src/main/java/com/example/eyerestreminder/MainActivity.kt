package com.example.eyerestreminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnStartStop: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvTimeRemaining: TextView
    private lateinit var tvWorkMinutes: TextView
    private lateinit var tvRestMinutes: TextView
    private lateinit var tvWarningMinutes: TextView
    private lateinit var seekBarWork: SeekBar
    private lateinit var seekBarRest: SeekBar
    private lateinit var seekBarWarning: SeekBar
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var notificationHelper: NotificationHelper
    private val handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkExactAlarmPermission()
        } else {
            Toast.makeText(this, "Notification permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initManagers()
        setupSeekBars()
        updateUI()
        startUIUpdateLoop()

        btnStartStop.setOnClickListener {
            if (preferencesManager.isTimerRunning) {
                stopEyeRestTimer()
            } else {
                startEyeRestTimer()
            }
        }
    }

    private fun initViews() {
        btnStartStop = findViewById(R.id.btnStartStop)
        tvStatus = findViewById(R.id.tvStatus)
        tvTimeRemaining = findViewById(R.id.tvTimeRemaining)
        tvWorkMinutes = findViewById(R.id.tvWorkMinutes)
        tvRestMinutes = findViewById(R.id.tvRestMinutes)
        tvWarningMinutes = findViewById(R.id.tvWarningMinutes)
        seekBarWork = findViewById(R.id.seekBarWork)
        seekBarRest = findViewById(R.id.seekBarRest)
        seekBarWarning = findViewById(R.id.seekBarWarning)
    }

    private fun initManagers() {
        preferencesManager = PreferencesManager(this)
        notificationHelper = NotificationHelper(this)
    }

    private fun setupSeekBars() {
        // Work time seek bar (5-60 minutes)
        seekBarWork.max = 55 // 60 - 5
        seekBarWork.progress = preferencesManager.workMinutes - 5
        updateWorkMinutesText()

        seekBarWork.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    preferencesManager.workMinutes = progress + 5
                    updateWorkMinutesText()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Rest time seek bar (1-10 minutes)
        seekBarRest.max = 9 // 10 - 1
        seekBarRest.progress = preferencesManager.restMinutes - 1
        updateRestMinutesText()

        seekBarRest.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    preferencesManager.restMinutes = progress + 1
                    updateRestMinutesText()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Warning time seek bar (1-10 minutes)
        seekBarWarning.max = 9 // 10 - 1
        seekBarWarning.progress = preferencesManager.warningMinutes - 1
        updateWarningMinutesText()

        seekBarWarning.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    preferencesManager.warningMinutes = progress + 1
                    updateWarningMinutesText()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateWorkMinutesText() {
        tvWorkMinutes.text = "Work Time: ${preferencesManager.workMinutes} minutes"
    }

    private fun updateRestMinutesText() {
        tvRestMinutes.text = "Rest Time: ${preferencesManager.restMinutes} minutes"
    }

    private fun updateWarningMinutesText() {
        tvWarningMinutes.text = "Warning Time: ${preferencesManager.warningMinutes} minutes before"
    }

    private fun startEyeRestTimer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        
        checkExactAlarmPermission()
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }
        
        checkBatteryOptimization()
    }

    private fun checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName")
                try {
                    startActivity(intent)
                    Toast.makeText(this, "Please allow app to ignore battery optimization for reliable alarms", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Please disable battery optimization for this app in settings", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        startTimer()
    }

    private fun startTimer() {
        preferencesManager.isTimerRunning = true
        preferencesManager.timerStartTime = System.currentTimeMillis()
        preferencesManager.currentPhase = "work"
        
        scheduleNextAlarm()
        updateUI()
        
        Toast.makeText(this, "Eye rest timer started!", Toast.LENGTH_SHORT).show()
    }

    private fun scheduleNextAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, EyeRestReceiver::class.java)
        intent.action = "TIMER_TICK"
        
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
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

    private fun stopEyeRestTimer() {
        preferencesManager.resetTimer()
        
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, EyeRestReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        
        notificationHelper.cancelAllNotifications()
        updateUI()
        
        Toast.makeText(this, "Eye rest timer stopped!", Toast.LENGTH_SHORT).show()
    }

    private fun startUIUpdateLoop() {
        updateRunnable = object : Runnable {
            override fun run() {
                updateTimeRemaining()
                handler.postDelayed(this, 1000) // Update every second
            }
        }
        updateRunnable?.let { handler.post(it) }
    }

    private fun updateTimeRemaining() {
        if (!preferencesManager.isTimerRunning) {
            tvTimeRemaining.text = "Timer not running"
            return
        }

        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - preferencesManager.timerStartTime
        
        val totalDurationMs = when (preferencesManager.currentPhase) {
            "work" -> preferencesManager.workMinutes * 60 * 1000L
            "rest" -> preferencesManager.restMinutes * 60 * 1000L
            else -> preferencesManager.workMinutes * 60 * 1000L
        }
        
        val remainingMs = totalDurationMs - elapsedTime
        
        if (remainingMs <= 0) {
            tvTimeRemaining.text = "00:00"
            return
        }
        
        val minutes = (remainingMs / 60000).toInt()
        val seconds = ((remainingMs % 60000) / 1000).toInt()
        
        tvTimeRemaining.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateUI() {
        if (preferencesManager.isTimerRunning) {
            btnStartStop.text = "Stop Eye Rest Timer"
            tvStatus.text = when (preferencesManager.currentPhase) {
                "work" -> "Working Time - Focus on your tasks"
                "rest" -> "Rest Time - Look away from screen"
                else -> "Timer Running"
            }
            seekBarWork.isEnabled = false
            seekBarRest.isEnabled = false
        } else {
            btnStartStop.text = "Start Eye Rest Timer"
            tvStatus.text = "Timer Stopped"
            tvTimeRemaining.text = "Ready to start"
            seekBarWork.isEnabled = true
            seekBarRest.isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        updateRunnable?.let { handler.removeCallbacks(it) }
    }
}