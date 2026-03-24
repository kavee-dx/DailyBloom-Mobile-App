package com.example.dailybloom2.ui.habits

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybloom2.R
import com.example.dailybloom2.data.Habit

class HabitsAdapter(
    private var habits: MutableList<Habit>,
    private val onToggleCompleted: (Int, Boolean) -> Unit,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitName: TextView = itemView.findViewById(R.id.habitName)
        val habitCheckbox: CheckBox = itemView.findViewById(R.id.habitCheckbox)
        val completedTime: TextView = itemView.findViewById(R.id.completedTime)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.habit_item, parent, false)
        return HabitViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        
        Log.d("HabitsAdapter", "Binding habit at position $position: ${habit.name}")
        
        holder.habitName.text = habit.name
        holder.habitCheckbox.isChecked = habit.isCompleted
        
        // Show completion time if completed
        if (habit.isCompleted && habit.completedAt != null) {
            val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(habit.completedAt))
            holder.completedTime.text = "Completed at $time"
            holder.completedTime.visibility = View.VISIBLE
        } else {
            holder.completedTime.visibility = View.GONE
        }
        
        // Visual feedback for completion
        if (habit.isCompleted) {
            holder.itemView.alpha = 0.7f
            holder.habitName.alpha = 0.6f
        } else {
            holder.itemView.alpha = 1f
            holder.habitName.alpha = 1f
        }
        
        // Set up click listeners
        holder.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
            Log.d("HabitsAdapter", "Checkbox clicked for position $position: $isChecked")
            onToggleCompleted(position, isChecked)
        }
        
        holder.editButton.setOnClickListener {
            Log.d("HabitsAdapter", "Edit button clicked for position $position")
            onEdit(position)
        }
        
        holder.deleteButton.setOnClickListener {
            Log.d("HabitsAdapter", "Delete button clicked for position $position")
            onDelete(position)
        }
    }
    
    override fun getItemCount(): Int = habits.size
    
    fun setItems(newHabits: List<Habit>) {
        Log.d("HabitsAdapter", "Setting ${newHabits.size} items")
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
        Log.d("HabitsAdapter", "Items set successfully")
    }
}