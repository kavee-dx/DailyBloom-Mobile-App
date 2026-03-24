package com.example.dailybloom2.ui.habits

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybloom2.R
import com.example.dailybloom2.data.HabitHistory
import com.google.android.material.progressindicator.LinearProgressIndicator

class HabitHistoryAdapter(
    private var histories: MutableList<HabitHistory>
) : RecyclerView.Adapter<HabitHistoryAdapter.HabitHistoryViewHolder>() {

    class HabitHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitName: TextView = itemView.findViewById(R.id.habitName)
        val completionPercentage: TextView = itemView.findViewById(R.id.completionPercentage)
        val progressBar: LinearProgressIndicator = itemView.findViewById(R.id.progressBar)
        val completedDays: TextView = itemView.findViewById(R.id.completedDays)
        val streakDays: TextView = itemView.findViewById(R.id.streakDays)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.habit_history_item, parent, false)
        return HabitHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitHistoryViewHolder, position: Int) {
        val history = histories[position]
        
        Log.d("HabitHistoryAdapter", "Binding history for habit: ${history.habitName}")
        
        holder.habitName.text = history.habitName
        holder.completionPercentage.text = "${history.completionPercentage}%"
        holder.progressBar.progress = history.completionPercentage
        holder.completedDays.text = "${history.completedDays}/${history.totalDays} days"
        
        // Calculate streak (simplified - consecutive completed days from recent records)
        val streak = calculateStreak(history.recentRecords)
        holder.streakDays.text = "🔥 $streak day streak"
    }

    override fun getItemCount(): Int = histories.size

    fun setItems(newHistories: List<HabitHistory>) {
        Log.d("HabitHistoryAdapter", "Setting ${newHistories.size} history items")
        histories.clear()
        histories.addAll(newHistories)
        notifyDataSetChanged()
    }

    private fun calculateStreak(records: List<com.example.dailybloom2.data.HabitCompletionRecord>): Int {
        var streak = 0
        for (record in records.reversed()) {
            if (record.isCompleted) {
                streak++
            } else {
                break
            }
        }
        return streak
    }
}
