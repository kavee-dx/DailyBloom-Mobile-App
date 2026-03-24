package com.example.dailybloom2.ui.habits

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailybloom2.R
import com.example.dailybloom2.data.HabitCompletionRecord
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import java.util.*

class HabitsFragment : Fragment() {

    private val viewModel: HabitsViewModel by viewModels()
    private lateinit var adapter: HabitsAdapter
    private lateinit var habitsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var habitsProgressBar: CircularProgressIndicator
    private lateinit var habitsProgressText: TextView
    private lateinit var emptyStateText: View
    private lateinit var customHabitInput: EditText
    private lateinit var addCustomHabitBtn: MaterialButton
    
    // 5-day progress views
    private lateinit var day1Date: TextView
    private lateinit var day1Progress: com.google.android.material.progressindicator.LinearProgressIndicator
    private lateinit var day1Percentage: TextView
    private lateinit var day2Date: TextView
    private lateinit var day2Progress: com.google.android.material.progressindicator.LinearProgressIndicator
    private lateinit var day2Percentage: TextView
    private lateinit var day3Date: TextView
    private lateinit var day3Progress: com.google.android.material.progressindicator.LinearProgressIndicator
    private lateinit var day3Percentage: TextView
    private lateinit var day4Date: TextView
    private lateinit var day4Progress: com.google.android.material.progressindicator.LinearProgressIndicator
    private lateinit var day4Percentage: TextView
    private lateinit var day5Date: TextView
    private lateinit var day5Progress: com.google.android.material.progressindicator.LinearProgressIndicator
    private lateinit var day5Percentage: TextView
    
    // Sample habit buttons
    private lateinit var sampleHabit1: MaterialButton
    private lateinit var sampleHabit2: MaterialButton
    private lateinit var sampleHabit3: MaterialButton
    private lateinit var sampleHabit4: MaterialButton
    private lateinit var sampleHabit5: MaterialButton
    private lateinit var sampleHabit6: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HabitsFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        Log.d("HabitsFragment", "onViewCreated called")
        
        initViews(view)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        // Load initial data
        viewModel.loadHabits()
        
        Log.d("HabitsFragment", "HabitsFragment setup completed")
    }

    private fun initViews(view: View) {
        habitsRecyclerView = view.findViewById(R.id.habitsRecyclerView)
        habitsProgressBar = view.findViewById(R.id.habitsProgressBar)
        habitsProgressText = view.findViewById(R.id.habitsProgressText)
        emptyStateText = view.findViewById(R.id.emptyStateText)
        customHabitInput = view.findViewById(R.id.customHabitInput)
        addCustomHabitBtn = view.findViewById(R.id.addCustomHabitBtn)
        
        // 5-day progress views
        day1Date = view.findViewById(R.id.day1Date)
        day1Progress = view.findViewById(R.id.day1Progress)
        day1Percentage = view.findViewById(R.id.day1Percentage)
        day2Date = view.findViewById(R.id.day2Date)
        day2Progress = view.findViewById(R.id.day2Progress)
        day2Percentage = view.findViewById(R.id.day2Percentage)
        day3Date = view.findViewById(R.id.day3Date)
        day3Progress = view.findViewById(R.id.day3Progress)
        day3Percentage = view.findViewById(R.id.day3Percentage)
        day4Date = view.findViewById(R.id.day4Date)
        day4Progress = view.findViewById(R.id.day4Progress)
        day4Percentage = view.findViewById(R.id.day4Percentage)
        day5Date = view.findViewById(R.id.day5Date)
        day5Progress = view.findViewById(R.id.day5Progress)
        day5Percentage = view.findViewById(R.id.day5Percentage)
        
        // Sample habit buttons
        sampleHabit1 = view.findViewById(R.id.sampleHabit1)
        sampleHabit2 = view.findViewById(R.id.sampleHabit2)
        sampleHabit3 = view.findViewById(R.id.sampleHabit3)
        sampleHabit4 = view.findViewById(R.id.sampleHabit4)
        sampleHabit5 = view.findViewById(R.id.sampleHabit5)
        sampleHabit6 = view.findViewById(R.id.sampleHabit6)
        
        Log.d("HabitsFragment", "Views initialized")
    }

    private fun setupRecyclerView() {
        Log.d("HabitsFragment", "Setting up RecyclerView")
        habitsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = HabitsAdapter(
            habits = mutableListOf(),
            onToggleCompleted = { index, isCompleted ->
                Log.d("HabitsFragment", "Toggle completed: $index, $isCompleted")
                viewModel.toggleCompleted(index, isCompleted)
            },
            onEdit = { index ->
                Log.d("HabitsFragment", "Edit clicked: $index")
                showEditDialog(index)
            },
            onDelete = { index ->
                Log.d("HabitsFragment", "Delete clicked: $index")
                showDeleteConfirm(index)
            }
        )
        
        habitsRecyclerView.adapter = adapter
        Log.d("HabitsFragment", "RecyclerView setup completed")
    }

    private fun setupObservers() {
        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            Log.d("HabitsFragment", "Habits updated: ${habits.size} habits")
            adapter.setItems(habits)
            updateEmptyState(habits.isEmpty())
            
            // Load 5-day progress data whenever habits change
            load5DayProgress()
        }

        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            Log.d("HabitsFragment", "Progress updated: $progress%")
            habitsProgressBar.progress = progress
            habitsProgressText.text = "$progress%"
            
            // Also refresh 5-day progress to update today's percentage
            load5DayProgress()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("HabitsFragment", "Loading state: $isLoading")
            habitsProgressBar.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        Log.d("HabitsFragment", "Setting up click listeners")
        
        // Custom habit input
        addCustomHabitBtn.setOnClickListener {
            Log.d("HabitsFragment", "Add custom habit clicked")
            addCustomHabit()
        }
        
        // Sample habit buttons
        sampleHabit1.setOnClickListener { addSampleHabit("💧 Drink Water") }
        sampleHabit2.setOnClickListener { addSampleHabit("🧘 Meditate") }
        sampleHabit3.setOnClickListener { addSampleHabit("🏃 Exercise") }
        sampleHabit4.setOnClickListener { addSampleHabit("📚 Read") }
        sampleHabit5.setOnClickListener { addSampleHabit("💊 Vitamins") }
        sampleHabit6.setOnClickListener { addSampleHabit("😴 Sleep Early") }
        
        Log.d("HabitsFragment", "Click listeners setup completed")
    }
    
    private fun addCustomHabit() {
        val habitName = customHabitInput.text.toString().trim()
        Log.d("HabitsFragment", "Adding custom habit: '$habitName'")
        
        if (habitName.isNotEmpty()) {
            viewModel.addHabit(habitName)
            customHabitInput.text.clear()
            showSnackbar("✅ Custom habit added!")
        } else {
            showSnackbar("Please enter a habit name")
        }
    }
    
    private fun addSampleHabit(habitName: String) {
        Log.d("HabitsFragment", "Adding sample habit: '$habitName'")
        viewModel.addHabit(habitName)
        showSnackbar("✅ $habitName added!")
    }

    private fun showEditDialog(index: Int) {
        Log.d("HabitsFragment", "Showing edit dialog for index: $index")
        try {
            val currentHabits = viewModel.habits.value ?: return
            if (index !in currentHabits.indices) {
                Log.w("HabitsFragment", "Index $index out of bounds")
                return
            }
            
            val habit = currentHabits[index]
            val editText = EditText(requireContext()).apply {
                setText(habit.name)
                setPadding(32, 32, 32, 32)
            }
            
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Edit Habit")
                .setView(editText)
                .setPositiveButton("Save") { _, _ ->
                    val newName = editText.text.toString().trim()
                    Log.d("HabitsFragment", "Editing habit to: '$newName'")
                    if (newName.isNotEmpty()) {
                        viewModel.editHabit(index, newName)
                        showSnackbar("✅ Habit updated!")
                    } else {
                        showSnackbar("Please enter a habit name")
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        } catch (e: Exception) {
            Log.e("HabitsFragment", "Error showing edit dialog", e)
            showSnackbar("Error showing edit dialog")
        }
    }

    private fun showDeleteConfirm(index: Int) {
        Log.d("HabitsFragment", "Showing delete confirm for index: $index")
        try {
            val currentHabits = viewModel.habits.value ?: return
            if (index !in currentHabits.indices) {
                Log.w("HabitsFragment", "Index $index out of bounds")
                return
            }
            
            val habit = currentHabits[index]
            
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete '${habit.name}'?")
                .setPositiveButton("Delete") { _, _ ->
                    Log.d("HabitsFragment", "Deleting habit: '${habit.name}'")
                    viewModel.deleteHabit(index)
                    showSnackbar("❌ Habit deleted")
                }
                .setNegativeButton("Cancel", null)
                .show()
        } catch (e: Exception) {
            Log.e("HabitsFragment", "Error showing delete dialog", e)
            showSnackbar("Error showing delete dialog")
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        emptyStateText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        habitsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
    
    private fun load5DayProgress() {
        Log.d("HabitsFragment", "Loading 5-day progress data")
        try {
            val habits = viewModel.habits.value ?: emptyList()
            
            // Get actual completion data for last 5 days
            val last5Days = getActualLast5DaysData(habits.size)
            
            // Update each day's progress
            val dayViews = listOf(
                Triple(day1Date, day1Progress, day1Percentage),
                Triple(day2Date, day2Progress, day2Percentage),
                Triple(day3Date, day3Progress, day3Percentage),
                Triple(day4Date, day4Progress, day4Percentage),
                Triple(day5Date, day5Progress, day5Percentage)
            )
            
            last5Days.forEachIndexed { index, dayData ->
                if (index < dayViews.size) {
                    val (dateView, progressView, percentageView) = dayViews[index]
                    
                    // Set date
                    dateView.text = dayData.date
                    
                    // Set progress
                    progressView.progress = dayData.percentage
                    percentageView.text = "${dayData.percentage}%"
                }
            }
            
            Log.d("HabitsFragment", "5-day progress data loaded successfully")
        } catch (e: Exception) {
            Log.e("HabitsFragment", "Error loading 5-day progress data", e)
        }
    }
    
    private fun getActualLast5DaysData(totalHabits: Int): List<DayProgressData> {
        val calendar = Calendar.getInstance()
        val last5Days = mutableListOf<DayProgressData>()
        
        for (i in 4 downTo 0) {
            val dayCalendar = Calendar.getInstance()
            dayCalendar.add(Calendar.DAY_OF_YEAR, -i)
            
            val dateString = formatDateForDisplay(dayCalendar)
            
            // Get actual completion data for this day
            val actualProgress = getActualProgressForDay(dayCalendar, totalHabits)
            
            last5Days.add(DayProgressData(dateString, actualProgress))
        }
        
        return last5Days
    }
    
    private fun getActualProgressForDay(dayCalendar: Calendar, totalHabits: Int): Int {
        if (totalHabits == 0) return 0
        
        // Get the date string for this day
        val dateString = getDateString(dayCalendar)
        val todayString = getDateString(Calendar.getInstance())
        
        // For today, use current habit completion status
        if (dateString == todayString) {
            val habits = viewModel.habits.value ?: emptyList()
            val completedCount = habits.count { it.isCompleted }
            return if (totalHabits > 0) (completedCount * 100) / totalHabits else 0
        }
        
        // For other days, get completion history
        val completionHistory = getCompletionHistoryForDay(dateString)
        val completedCount = completionHistory.count { it.isCompleted }
        return if (totalHabits > 0) (completedCount * 100) / totalHabits else 0
    }
    
    private fun getCompletionHistoryForDay(dateString: String): List<HabitCompletionRecord> {
        // Get actual completion history from repository
        return viewModel.getCompletionHistoryForDay(dateString)
    }
    
    private fun getDateString(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }
    
    private fun formatDateForDisplay(calendar: Calendar): String {
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return "${monthNames[month]} $dayOfMonth"
    }
    
    private data class DayProgressData(
        val date: String,
        val percentage: Int
    )
    
    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: java.util.Date())
        } catch (e: Exception) {
            Log.e("HabitsFragment", "Error formatting date: $dateString", e)
            dateString
        }
    }
}