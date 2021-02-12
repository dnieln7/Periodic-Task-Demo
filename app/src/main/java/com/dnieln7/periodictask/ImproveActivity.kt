package com.dnieln7.periodictask

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View

class ImproveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_improve)
    }


    /**
     * Displays a dialog to disable battery optimizations.
     */
    fun disableBatteryOptimizations(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val settingsIntent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)

            settingsIntent.data = Uri.parse("package:$packageName")

            startActivity(settingsIntent)
        }
    }

    /**
     * Shows the AutoStart enabled and disabled apps list.
     */
    fun openAutoStart(view: View) {
        if (Build.BRAND.equals("xiaomi", ignoreCase = true)) {
            val intent = Intent()
            intent.component = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } else if (Build.MANUFACTURER.equals("oppo", ignoreCase = true)) {
            try {
                val intent = Intent()
                intent.setClassName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
                startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent()
                    intent.setClassName(
                        "com.oppo.safe",
                        "com.oppo.safe.permission.startup.StartupAppListActivity"
                    )
                    startActivity(intent)
                } catch (ex: Exception) {
                    try {
                        val intent = Intent()
                        intent.setClassName(
                            "com.coloros.safecenter",
                            "com.coloros.safecenter.startupapp.StartupAppListActivity"
                        )
                        startActivity(intent)
                    } catch (exx: Exception) {
                    }
                }
            }
        }
    }
}