package com.dnieln7.periodictask.service

import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.dnieln7.periodictask.NotificationUtils
import com.dnieln7.periodictask.service.ReminderServiceInitializer.ReminderService
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Initializer class to configure a service of type [ReminderService].
 * @author dnieln7
 */
class ReminderServiceInitializer(context: Context) : ContextWrapper(context) {

    private lateinit var reminderService: Intent

    /**
     * Creates a new instance of [ReminderService] with the supplied message as an intent extra.
     * @param message Message for the reminder.
     * @param repeatInterval The amount of milliseconds between each reminder.
     * @return The current instance of [ReminderServiceInitializer] for method chaining purposes.
     */
    fun setUp(message: String, repeatInterval: Long): ReminderServiceInitializer {
        reminderService = Intent(this, ReminderService::class.java)
        reminderService.putExtra("MESSAGE", message)
        reminderService.putExtra("INTERVAL", repeatInterval)

        return this
    }

    /**
     * Starts the previously configured service.
     * Does nothing if the current [ReminderService] property is not initialized.
     */
    fun start() {
        if (this::reminderService.isInitialized) {
            startService(reminderService)
        }
    }

    /**
     * [Service] implementation to display a notification each x minutes.
     * @author dnieln7
     */
    internal class ReminderService : Service() {
        private var repeatInterval = TimeUnit.MINUTES.toMillis(15)
        private lateinit var message: String
        lateinit var timer: Timer

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            message = "Reminder: ${intent!!.getStringExtra("MESSAGE")}"
            repeatInterval = intent.getLongExtra("INTERVAL", TimeUnit.MINUTES.toMillis(15))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val utils = NotificationUtils(this)
                val notification = utils.createForegroundNotification()

                startForeground(1, notification)
            }

            val timerTask: TimerTask = object : TimerTask() {
                override fun run() {
                    val utils = NotificationUtils(applicationContext)
                    val notification = utils.createReminder(message)
                    utils.launchNotification(notification)
                }
            }

            timer = Timer()
            timer.schedule(timerTask, 0, repeatInterval)// Without limits

            return START_STICKY
        }

        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

        override fun onDestroy() {
            stopTimer()
            super.onDestroy()
        }

        private fun stopTimer() {
            timer.cancel()
        }
    }
}