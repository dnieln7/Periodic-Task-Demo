package com.dnieln7.periodictask.work

import android.app.job.JobService
import android.content.Context
import android.content.ContextWrapper
import androidx.work.*
import com.dnieln7.periodictask.NotificationUtils
import java.util.concurrent.TimeUnit

/**
 * Initializer class to configure a job of type [ReminderWork].
 * @author dnieln7
 */
class ReminderWorkInitializer(context: Context) : ContextWrapper(context) {

    private lateinit var reminderWork: PeriodicWorkRequest

    companion object {
        const val WORK_TAG = "REMINDER"
    }

    /**
     * Creates a new instance of [ReminderWork] with the supplied message in a [Data] object.
     * @param message Message for the reminder.
     * @return The current instance of [ReminderWorkInitializer] for method chaining purposes.
     */
    fun setUp(message: String): ReminderWorkInitializer {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val data = Data.Builder()
            .putString("MESSAGE", "Reminder: $message")
            .build()

                                                                    // Must be 15 minutes at least
        reminderWork = PeriodicWorkRequestBuilder<ReminderWork>(15, TimeUnit.MINUTES)
            .addTag(WORK_TAG)
            .setInputData(data)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                10,
                TimeUnit.SECONDS,
            )
            .build()

        return this
    }

    /**
     * Schedules the previously configured work.
     * Does nothing if the current [ReminderWork] property is not initialized.
     */
    fun start() {
        if(this::reminderWork.isInitialized) {
            WorkManager.getInstance(this).enqueue(reminderWork)
        }
    }

    /**
     * [Worker] implementation to display a notification each 15 minutes.
     * @author dnieln7
     */
    internal class ReminderWork(context: Context, workerParameters: WorkerParameters) :
        Worker(context, workerParameters) {

        override fun doWork(): Result {
            val result = Result.success()
            val message = inputData.getString("MESSAGE")!!
            val utils = NotificationUtils(applicationContext)
            val notification = utils.createReminder(message)
            utils.launchNotification(notification)

            return result
        }
    }
}