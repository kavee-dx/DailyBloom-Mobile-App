package com.example.dailybloom2.ui.mood

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybloom2.R
import com.example.dailybloom2.data.MoodEntry

class MoodAdapter(
    private var moodEntries: MutableList<MoodEntry>,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moodEmoji: TextView = itemView.findViewById(R.id.moodEmoji)
        val moodNote: TextView = itemView.findViewById(R.id.moodNote)
        val moodTime: TextView = itemView.findViewById(R.id.moodTime)
        val deleteMoodBtn: ImageButton = itemView.findViewById(R.id.deleteMoodBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mood_entry_item, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val entry = moodEntries[position]
        
        Log.d("MoodAdapter", "Binding mood entry at position $position: ${entry.emoji}")
        
        holder.moodEmoji.text = entry.emoji
        holder.moodNote.text = entry.note.ifEmpty { "No note" }
        holder.moodTime.text = entry.time
        
        holder.deleteMoodBtn.setOnClickListener {
            Log.d("MoodAdapter", "Delete button clicked for entry: ${entry.id}")
            onDelete(entry.id)
        }
    }

    override fun getItemCount(): Int = moodEntries.size

    fun setItems(newEntries: List<MoodEntry>) {
        Log.d("MoodAdapter", "Setting ${newEntries.size} mood entries")
        Log.d("MoodAdapter", "Previous item count: ${moodEntries.size}")
        
        moodEntries.clear()
        moodEntries.addAll(newEntries)
        
        Log.d("MoodAdapter", "New item count: ${moodEntries.size}")
        Log.d("MoodAdapter", "Calling notifyDataSetChanged()")
        
        notifyDataSetChanged()
        
        Log.d("MoodAdapter", "notifyDataSetChanged() completed")
    }
}
