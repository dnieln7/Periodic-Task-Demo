package com.dnieln7.periodictask

import android.content.Intent
import android.os.Bundle
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

    fun startImproveActivity(view: View) {
        startActivity(Intent(this, ImproveActivity::class.java))
    }
}