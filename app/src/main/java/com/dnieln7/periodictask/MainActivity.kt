package com.dnieln7.periodictask

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dnieln7.periodictask.job.JobUtils.getJobsInfo
import com.dnieln7.periodictask.job.JobUtils.stopJobById
import com.dnieln7.periodictask.work.WorkUtils.getWorkInfoByTag
import com.dnieln7.periodictask.work.WorkUtils.stopWorkByTag
import com.dnieln7.periodictask.job.ReminderJobInitializer
import com.dnieln7.periodictask.recycler.CustomTask
import com.dnieln7.periodictask.recycler.TaskAdapter
import com.dnieln7.periodictask.service.ReminderServiceInitializer
import com.dnieln7.periodictask.service.ServiceUtils.stopServiceByClass
import com.dnieln7.periodictask.work.ReminderWorkInitializer
import com.google.android.material.textfield.TextInputEditText
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var mode = 1

    private lateinit var tasksList: RecyclerView
    private lateinit var message: TextInputEditText
    private lateinit var modeOptions: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initWidgets()
        initList()
    }

    private fun initWidgets() {
        message = findViewById(R.id.demo_message)
        modeOptions = findViewById(R.id.demo_mode)
        modeOptions.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.demo_work -> {
                    mode = 1
                    refreshList()
                }
                R.id.demo_job -> {
                    mode = 2
                    refreshList()
                }
                R.id.demo_foreground -> {
                    mode = 3
                    refreshList()
                }
            }
        }
    }

    private fun initList() {
        tasksList = findViewById(R.id.demo_tasks)
        tasksList.setHasFixedSize(true)

        refreshList()
    }

    private fun refreshList() {
        var adapter = TaskAdapter(listOf())

        when (mode) {
            1 -> {
                val customTaskList = arrayListOf<CustomTask>()
                this.getWorkInfoByTag(ReminderWorkInitializer.WORK_TAG).forEach { workInfo ->
                    customTaskList.add(
                        CustomTask(
                            "UUID: ${workInfo.id}",
                            "State name: ${workInfo.state.name}",
                            "Run attempt count: ${workInfo.runAttemptCount}"
                        )
                    )
                }
                adapter = TaskAdapter(customTaskList)
            }
            2 -> {
                val customTaskList = arrayListOf<CustomTask>()
                this.getJobsInfo().forEach { jobInfo ->
                    customTaskList.add(
                        CustomTask(
                            "ID: ${jobInfo.id}",
                            "Class name: ${jobInfo.service.className}",
                            "Periodic? ${jobInfo.isPeriodic}"
                        )
                    )
                }
                adapter = TaskAdapter(customTaskList)
            }
            3 -> {
                adapter = TaskAdapter(
                    listOf(
                        CustomTask(
                            "You are using",
                            "Foreground service",
                            "List is not available"
                        )
                    )
                )
            }
        }

        tasksList.adapter = adapter
    }

    fun start(view: View) {
        var currentTask = ""

        when (mode) {
            1 -> {
                val work = ReminderWorkInitializer(this)
                work.setUp(message.text.toString()).start()
                currentTask = "Work"
            }
            2 -> {
                val job = ReminderJobInitializer(this)
                job.setUp(message.text.toString()).start()
                currentTask = "Job"
            }
            3 -> {
                val service = ReminderServiceInitializer(this)
                service.setUp(message.text.toString(), TimeUnit.MINUTES.toMillis(15)).start()
                currentTask = "Foreground Service"
            }
        }

        Toast.makeText(this, "$currentTask Started", Toast.LENGTH_LONG).show()
        refreshList()
    }

    fun stop(view: View) {
        var currentTask = ""

        when (mode) {
            1 -> {
                this.stopWorkByTag(ReminderWorkInitializer.WORK_TAG)
                currentTask = "Work"
            }
            2 -> {
                this.stopJobById(ReminderJobInitializer.JOB_ID)
                currentTask = "Job"
            }
            3 -> {
                this.stopServiceByClass(ReminderServiceInitializer.ReminderService::class.java)
                currentTask = "Foreground Service"
            }
        }

        Toast.makeText(this, "$currentTask Stopped", Toast.LENGTH_LONG).show()
        refreshList()
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