package com.dnieln7.periodictask

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

/**
 * Helper class to create [Notification] instances.
 * @author dnieln7
 */
class NotificationUtils(context: Context) : ContextWrapper(context) {
    private val remindersId = "com.dnieln7.periodictask.REMINDERS"
    private val remindersName = "Reminders"
    private val foregroundId = "com.dnieln7.periodictask.FOREGROUND"
    private val foregroundName = "Foreground service"

    init {
        createChannels()
    }

    private fun getManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remindersChannel = NotificationChannel(
                remindersId,
                remindersName,
                NotificationManager.IMPORTANCE_HIGH
            )

            remindersChannel.importance = NotificationManager.IMPORTANCE_HIGH
            remindersChannel.enableLights(true)
            remindersChannel.enableVibration(true)
            remindersChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            getManager().createNotificationChannel(remindersChannel)

            val foregroundChannel = NotificationChannel(
                foregroundId,
                foregroundName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            foregroundChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
            foregroundChannel.enableLights(false)
            foregroundChannel.enableVibration(false)
            foregroundChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            getManager().createNotificationChannel(foregroundChannel)
        }
    }

    /**
     * Creates a notification to be displayed while a [Service] is running.
     * @return An instance of [Notification]
     */
    fun createForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val notificationBuilder = NotificationCompat.Builder(this, foregroundId)

        notificationBuilder.setContentTitle(getString(R.string.app_name))
            .setContentIntent(pendingIntent)
            .setContentText("Reminders running...")
            .setSmallIcon(R.drawable.ic_reminder)

        return notificationBuilder.build()
    }

    /**
     * Creates a notification to show a reminder.
     * @param message The reminder to be shown.
     * @return An instance of [Notification]
     */
    fun createReminder(message: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val notificationBuilder = NotificationCompat.Builder(this, remindersId)

        notificationBuilder
            .setSmallIcon(R.drawable.ic_reminder)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        return notificationBuilder.build()
    }

    /**
     * Displays a notification to the user with a random id between 1 and 1000.
     * @param notification A configured instance of [Notification].
     */
    fun launchNotification(notification: Notification) {
        NotificationManagerCompat.from(this).notify((Random().nextInt(1001) + 1), notification)
    }
}