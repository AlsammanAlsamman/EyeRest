package com.example.eyerestreminder

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RestActivity : AppCompatActivity() {

    private lateinit var tvRestTimer: TextView
    private lateinit var tvRestMessage: TextView
    private lateinit var btnSkipRest: Button
    private lateinit var imgEyeClosed: ImageView
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var notificationHelper: NotificationHelper
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rest)

        initViews()
        initManagers()
        startRestTimer()
    }

    private fun initViews() {
        tvRestTimer = findViewById(R.id.tvRestTimer)
        tvRestMessage = findViewById(R.id.tvRestMessage)
        btnSkipRest = findViewById(R.id.btnSkipRest)
        imgEyeClosed = findViewById(R.id.imgEyeClosed)

        btnSkipRest.setOnClickListener {
            skipRest()
        }
    }

    private fun initManagers() {
        preferencesManager = PreferencesManager(this)
        notificationHelper = NotificationHelper(this)
    }

    private fun startRestTimer() {
        val restDurationMs = preferencesManager.restMinutes * 60 * 1000L
        
        tvRestMessage.text = "Take a ${preferencesManager.restMinutes}-minute break!\nLook at something 20 feet away"
        imgEyeClosed.visibility = ImageView.VISIBLE

        countDownTimer = object : CountDownTimer(restDurationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 60000).toInt()
                val seconds = ((millisUntilFinished % 60000) / 1000).toInt()
                tvRestTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                finishRest()
            }
        }
        countDownTimer?.start()
    }

    private fun finishRest() {
        // Play the sound again
        notificationHelper.playCustomSound()
        
        // Hide the eye image
        imgEyeClosed.visibility = ImageView.GONE
        
        // Update preferences to start work phase
        preferencesManager.currentPhase = "work"
        preferencesManager.timerStartTime = System.currentTimeMillis()
        
        // Cancel rest notification
        notificationHelper.cancelRestNotification()
        
        // Start work notification
        notificationHelper.showWorkTimeNotification(preferencesManager.workMinutes)
        
        // Close this activity
        finish()
    }

    private fun skipRest() {
        countDownTimer?.cancel()
        
        // Update preferences to start work phase
        preferencesManager.currentPhase = "work"
        preferencesManager.timerStartTime = System.currentTimeMillis()
        
        // Cancel rest notification
        notificationHelper.cancelRestNotification()
        
        // Start work notification
        notificationHelper.showWorkTimeNotification(preferencesManager.workMinutes)
        
        // Close this activity
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    override fun onBackPressed() {
        // Don't allow back button during rest - user must skip or wait
        // You can remove this if you want to allow back button
    }
}
