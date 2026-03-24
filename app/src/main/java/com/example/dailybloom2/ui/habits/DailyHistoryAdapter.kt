package com.example.dailybloom2.ui.habits

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybloom2.R
import com.example.dailybloom2.data.DailyCompletionRecord
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.*

class DailyHistoryAdapter(
    private var dailyRecords: MutableList<DailyCompletionRecord>
) : RecyclerView.Adapter<DailyHistoryAdapter.DailyHistoryViewHolder>() {

    class DailyHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val dailyProgressBar: LinearProgressIndicator = itemView.findViewById(R.id.dailyProgressBar)
        val dailyPercentage: TextView = itemView.findViewById(R.id.dailyPercentage)
        val completionCount: TextView = itemView.findViewById(R.id.completionCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_history_item, parent, false)
        return DailyHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyHistoryViewHolder, position: Int) {
        val record = dailyRecords[position]
        
        Log.d("DailyHistoryAdapter", "Binding daily record: ${record.date}")
        
        // Format date (e.g., "2024-01-15" -> "Jan 15")
        val formattedDate = formatDate(record.date)
        holder.dateText.text = formattedDate
        
        holder.dailyProgressBar.progress = record.dailyPercentage
        holder.dailyPercentage.text = "${record.dailyPercentage}%"
        holder.completionCount.text = "${record.completedHabits}/${record.totalHabits}"
    }

    override fun getItemCount(): Int = dailyRecords.size

    fun setItems(newRecords: List<DailyCompletionRecord>) {
        Log.d("DailyHistoryAdapter", "Setting ${newRecords.size} daily records")
        dailyRecords.clear()
        dailyRecords.addAll(newRecords)
        notifyDataSetChanged()
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            Log.e("DailyHistoryAdapter", "Error formatting date: $dateString", e)
            dateString
        }
    }
}
