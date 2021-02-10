package com.dnieln7.periodictask.service

import android.app.Service
import android.content.Context
import android.content.Intent

/**
 * Helper methods for [Service].
 * @author dnieln7
 */
object ServiceUtils {

    /**
     * Stops a service of the provided class.
     * @param clazz The [Class] of the service to be stopped. It must extend from [Service].
     */
    fun Context.stopServiceByClass(clazz: Class<out Service>) {
        val service = Intent(this, clazz)
        stopService(service)
    }
}