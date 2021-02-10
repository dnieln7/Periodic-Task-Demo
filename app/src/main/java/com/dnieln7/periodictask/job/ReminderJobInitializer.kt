package com.dnieln7.periodictask.job

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.os.PersistableBundle
import com.dnieln7.periodictask.NotificationUtils
import java.util.concurrent.TimeUnit

/**
 * Initializer class to configure a job of type [ReminderJob].
 * @author dnieln7
 */
class ReminderJobInitializer(context: Context) : ContextWrapper(context) {

    private lateinit var reminderJob: JobInfo

    companion object {
        const val JOB_ID = 1
    }

    /**
     * Creates a new instance of [ReminderJob] with the supplied message in a [PersistableBundle] object.
     * @param message Message for the reminder.
     * @return The current instance of [ReminderJobInitializer] for method chaining purposes.
     */
    fun setUp(message: String): ReminderJobInitializer {
        val extras = PersistableBundle()
        extras.putString("MESSAGE", message)

        reminderJob = JobInfo.Builder(JOB_ID, ComponentName(this, ReminderJob::class.java))
            .setExtras(extras)
            .setPeriodic(TimeUnit.MINUTES.toMillis(15)) // Must be 15 minutes at least
            .setPersisted(true)
            .build()

        return this
    }

    /**
     * Schedules the previously configured job.
     * Does nothing if the current [ReminderJob] property is not initialized.
     */
    fun start() {
        if (this::reminderJob.isInitialized) {
            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(reminderJob)
        }
    }

    /**
     * [JobService] implementation to display a notification each 15 minutes.
     * @author dnieln7
     */
    internal class ReminderJob : JobService() {
        override fun onStartJob(params: JobParameters?): Boolean {

            sendReminder(params)

            return true
        }

        override fun onStopJob(params: JobParameters?): Boolean {
            return false
        }

        private fun sendReminder(params: JobParameters?) {
            val message = "Reminder: ${params!!.extras.getString("MESSAGE")}"
            val utils = NotificationUtils(applicationContext)
            val notification = utils.createReminder(message)
            utils.launchNotification(notification)

            jobFinished(params, false)
        }
    }
}