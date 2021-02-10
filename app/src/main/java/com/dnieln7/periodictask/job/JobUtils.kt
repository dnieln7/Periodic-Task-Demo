package com.dnieln7.periodictask.job

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/**
 * Helper methods for [JobScheduler].
 * @author dnieln7
 */
object JobUtils {

    /**
     * Get All running jobs info.
     * @return A list of [JobInfo] objects.
     */
    fun Context.getJobsInfo(): List<JobInfo> {
        val jobScheduler = getSystemService(AppCompatActivity.JOB_SCHEDULER_SERVICE) as JobScheduler

        return jobScheduler.allPendingJobs
    }

    /**
     * Stops a job with the provided id.
     * @param id Identifier of the job.
     */
    fun Context.stopJobById(id: Int) {
        (getSystemService(AppCompatActivity.JOB_SCHEDULER_SERVICE) as JobScheduler).cancel(id)
    }
}