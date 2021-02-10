package com.dnieln7.periodictask.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dnieln7.periodictask.R

/**
 * [RecyclerView.Adapter] implementation to display active jobs and works.
 * @param tasks A list of [CustomTask] objects.
 * @author dnieln7
 */
class TaskAdapter(private val tasks: List<CustomTask>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    /**
     * Logic side of a card that will be displayed in a [RecyclerView].
     * @author dnieln7
     */
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message1: TextView? = null
        var message2: TextView? = null
        var message3: TextView? = null

        init {
            message1 = itemView.findViewById(R.id.task_message_1)
            message2 = itemView.findViewById(R.id.task_message_2)
            message3 = itemView.findViewById(R.id.task_message_3)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.tile_task, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        println(tasks[position].toString())
        holder.message1!!.text = tasks[position].message1
        holder.message2!!.text = tasks[position].message2
        holder.message3!!.text = tasks[position].message3
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}