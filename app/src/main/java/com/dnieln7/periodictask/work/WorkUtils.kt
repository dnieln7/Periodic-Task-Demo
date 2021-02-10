package com.dnieln7.periodictask.work

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Helper methods for [WorkManager].
 * @author dnieln7
 */
object WorkUtils {

    /**
     * Get All running works info.
     * @return A list of [WorkInfo] objects.
     */
    fun Context.getWorkInfoByTag(tag: String): List<WorkInfo> {
        var works: List<WorkInfo> = ArrayList()

        try {
            works = WorkManager.getInstance(this).getWorkInfosByTag(tag).get()
        } catch (e: ExecutionException) {
            Logger.getLogger(WorkUtils::class.java.name).log(Level.SEVERE, "There was an error", e)
        } catch (e: InterruptedException) {
            Logger.getLogger(WorkUtils::class.java.name).log(Level.SEVERE, "There was an error", e)
        }

        return works
    }

    /**
     * Stops all works with the provided tag.
     * @param tag Identifier of the work.
     */
    fun Context.stopWorkByTag(tag: String) {
        WorkManager.getInstance(this).cancelAllWorkByTag(tag)
    }
}