package com.example.dailybloom2.ui.mood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailybloom2.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class MoodFragment : Fragment() {

    private val viewModel: MoodViewModel by viewModels()
    private lateinit var adapter: MoodAdapter
    private lateinit var todayMoodsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var emptyStateText: View
    private lateinit var moodNoteInput: TextInputEditText
    private lateinit var addMoodBtn: MaterialButton
    private lateinit var shareMoodSummaryBtn: MaterialButton
    
    // Emoji buttons
    private lateinit var emojiHappy: LinearLayout
    private lateinit var emojiExcited: LinearLayout
    private lateinit var emojiNeutral: LinearLayout
    private lateinit var emojiSad: LinearLayout
    private lateinit var emojiAngry: LinearLayout
    
    // Chart
    private lateinit var trendDay1: TextView
    private lateinit var trendEmoji1: TextView
    private lateinit var trendProgress1: com.google.android.material.progressindicator.LinearProgressIndicator
    private lateinit var trendDay2: TextView
    private lateinit var trendEmoji2: TextView
    private lateinit var trendProgress2: com.google.android.material.progressindicator.LinearProgressIndicator
    private lateinit var trendDay3: TextView
    private lateinit var trendEmoji3: TextView
    private lateinit var trendProgress3: com.google.android.material.progressindicator.LinearProgressIndicator
    private lateinit var trendDay4: TextView
    private lateinit var trendEmoji4: TextView
    private lateinit var trendProgress4: com.google.android.material.progressindicator.LinearProgressIndicator
    private lateinit var trendDay5: TextView
    private lateinit var trendEmoji5: TextView
    private lateinit var trendProgress5: com.google.android.material.progressindicator.LinearProgressIndicator
    
    private var selectedEmoji: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MoodFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        Log.d("MoodFragment", "onViewCreated called")
        
        initViews(view)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupChart()
        
        // Load initial data
        viewModel.loadMoodEntries()
        
        Log.d("MoodFragment", "MoodFragment setup completed")
    }

    private fun initViews(view: View) {
        todayMoodsRecyclerView = view.findViewById(R.id.todayMoodsRecyclerView)
        emptyStateText = view.findViewById(R.id.emptyStateText)
        moodNoteInput = view.findViewById(R.id.moodNoteInput)
        addMoodBtn = view.findViewById(R.id.addMoodBtn)
        shareMoodSummaryBtn = view.findViewById(R.id.shareMoodSummaryBtn)
        
        // Emoji buttons
        emojiHappy = view.findViewById(R.id.emojiHappy)
        emojiExcited = view.findViewById(R.id.emojiExcited)
        emojiNeutral = view.findViewById(R.id.emojiNeutral)
        emojiSad = view.findViewById(R.id.emojiSad)
        emojiAngry = view.findViewById(R.id.emojiAngry)
        
        // Chart
        trendDay1 = view.findViewById(R.id.trendDay1)
        trendEmoji1 = view.findViewById(R.id.trendEmoji1)
        trendProgress1 = view.findViewById(R.id.trendProgress1)
        trendDay2 = view.findViewById(R.id.trendDay2)
        trendEmoji2 = view.findViewById(R.id.trendEmoji2)
        trendProgress2 = view.findViewById(R.id.trendProgress2)
        trendDay3 = view.findViewById(R.id.trendDay3)
        trendEmoji3 = view.findViewById(R.id.trendEmoji3)
        trendProgress3 = view.findViewById(R.id.trendProgress3)
        trendDay4 = view.findViewById(R.id.trendDay4)
        trendEmoji4 = view.findViewById(R.id.trendEmoji4)
        trendProgress4 = view.findViewById(R.id.trendProgress4)
        trendDay5 = view.findViewById(R.id.trendDay5)
        trendEmoji5 = view.findViewById(R.id.trendEmoji5)
        trendProgress5 = view.findViewById(R.id.trendProgress5)
        
        Log.d("MoodFragment", "Views initialized")
    }

    private fun setupRecyclerView() {
        Log.d("MoodFragment", "Setting up RecyclerView")
        todayMoodsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = MoodAdapter(
            moodEntries = mutableListOf(),
            onDelete = { entryId ->
                Log.d("MoodFragment", "Delete clicked for entry: $entryId")
                showDeleteConfirm(entryId)
            }
        )
        
        todayMoodsRecyclerView.adapter = adapter
        Log.d("MoodFragment", "RecyclerView setup completed")
    }

    private fun setupObservers() {
        viewModel.todayMoodEntries.observe(viewLifecycleOwner) { entries ->
            Log.d("MoodFragment", "Today's mood entries updated: ${entries.size} entries")
            Log.d("MoodFragment", "Entries: ${entries.map { "${it.emoji} at ${it.time}" }}")
            adapter.setItems(entries)
            updateEmptyState(entries.isEmpty())
            updateTrendDisplay()
            
            // Auto-scroll to show the newly added entry (only if there are entries)
            if (entries.isNotEmpty()) {
                scrollToShowNewEntry()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("MoodFragment", "Loading state: $isLoading")
            addMoodBtn.isEnabled = !isLoading
        }
    }
    
    private fun setupChart() {
        Log.d("MoodFragment", "Setting up mood trend display")
        updateTrendDisplay()
    }
    
    private fun updateTrendDisplay() {
        Log.d("MoodFragment", "Updating mood trend display")
        
        try {
            val recentEntries = viewModel.getRecentMoodEntries(5)
            val trendViews = listOf(
                Triple(trendDay1, trendEmoji1, trendProgress1),
                Triple(trendDay2, trendEmoji2, trendProgress2),
                Triple(trendDay3, trendEmoji3, trendProgress3),
                Triple(trendDay4, trendEmoji4, trendProgress4),
                Triple(trendDay5, trendEmoji5, trendProgress5)
            )
            
            // Get last 5 days
            val calendar = java.util.Calendar.getInstance()
            for (i in 4 downTo 0) {
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -i)
                val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(calendar.time)
                val dayLabel = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(calendar.time)
                
                val dayEntries = recentEntries.filter { it.date == date }
                val (dayView, emojiView, progressView) = trendViews[4 - i]
                
                dayView.text = dayLabel
                
                if (dayEntries.isNotEmpty()) {
                    // Calculate mood intensity percentage based on entries
                    val moodPercentage = calculateMoodPercentage(dayEntries)
                    
                    // Get most frequent emoji for the day
                    val emojiCount = dayEntries.groupingBy { it.emoji }.eachCount()
                    val dominantEmoji = emojiCount.maxByOrNull { it.value }?.key ?: dayEntries.first().emoji
                    
                    emojiView.text = dominantEmoji
                    progressView.progress = moodPercentage
                    
                    Log.d("MoodFragment", "Day $dayLabel: ${dayEntries.size} entries, dominant: $dominantEmoji, percentage: $moodPercentage%")
                } else {
                    emojiView.text = "—"
                    progressView.progress = 0
                    Log.d("MoodFragment", "Day $dayLabel: No entries")
                }
                
                calendar.add(java.util.Calendar.DAY_OF_YEAR, i) // Reset
            }
            
            Log.d("MoodFragment", "Trend display updated")
            
        } catch (e: Exception) {
            Log.e("MoodFragment", "Error updating trend display", e)
        }
    }
    
    private fun calculateMoodPercentage(dayEntries: List<com.example.dailybloom2.data.MoodEntry>): Int {
        if (dayEntries.isEmpty()) return 0
        
        // Calculate average mood intensity for the day
        val totalIntensity = dayEntries.sumOf { emojiToIntensity(it.emoji) }
        val averageIntensity = totalIntensity.toDouble() / dayEntries.size
        
        // Convert to percentage (0-100)
        val percentage = (averageIntensity * 20).toInt() // 1-5 scale * 20 = 20-100%
        
        // Ensure percentage is within valid range
        return percentage.coerceIn(0, 100)
    }
    
    private fun emojiToIntensity(emoji: String): Int {
        return when (emoji) {
            "😠" -> 1 // Angry - Low intensity
            "😢" -> 2 // Sad - Low-medium intensity
            "😐" -> 3 // Neutral - Medium intensity
            "😊" -> 4 // Happy - High intensity
            "🤩" -> 5 // Excited - Very high intensity
            else -> 3 // Default to neutral
        }
    }

    private fun setupClickListeners() {
        Log.d("MoodFragment", "Setting up click listeners")
        
        // Emoji selection
        emojiHappy.setOnClickListener { selectEmoji("😊", emojiHappy) }
        emojiExcited.setOnClickListener { selectEmoji("🤩", emojiExcited) }
        emojiNeutral.setOnClickListener { selectEmoji("😐", emojiNeutral) }
        emojiSad.setOnClickListener { selectEmoji("😢", emojiSad) }
        emojiAngry.setOnClickListener { selectEmoji("😠", emojiAngry) }
        
        // Add mood button
        addMoodBtn.setOnClickListener {
            Log.d("MoodFragment", "Add mood button clicked")
            addMoodEntry()
        }
        
        // Share mood summary button
        shareMoodSummaryBtn.setOnClickListener {
            Log.d("MoodFragment", "Share mood summary button clicked")
            shareMoodSummary()
        }
        
        Log.d("MoodFragment", "Click listeners setup completed")
    }
    
    private fun selectEmoji(emoji: String, button: LinearLayout) {
        Log.d("MoodFragment", "Emoji selected: $emoji")
        selectedEmoji = emoji
        
        // Reset all buttons
        resetEmojiButtons()
        
        // Highlight selected button
        button.isSelected = true
        
        // Enable add button
        addMoodBtn.isEnabled = true
    }
    
    private fun resetEmojiButtons() {
        val buttons = listOf(emojiHappy, emojiExcited, emojiNeutral, emojiSad, emojiAngry)
        buttons.forEach { button ->
            button.isSelected = false
        }
    }
    
    private fun addMoodEntry() {
        if (selectedEmoji.isEmpty()) {
            showSnackbar("Please select a mood emoji")
            return
        }
        
        val note = moodNoteInput.text.toString().trim()
        Log.d("MoodFragment", "Adding mood entry: $selectedEmoji with note: '$note'")
        
        viewModel.addMoodEntry(selectedEmoji, note)
        
        // Clear inputs
        moodNoteInput.text?.clear()
        selectedEmoji = ""
        resetEmojiButtons()
        addMoodBtn.isEnabled = false
        
        showSnackbar("Mood logged successfully! 😊")
    }

    private fun showDeleteConfirm(entryId: String) {
        Log.d("MoodFragment", "Showing delete confirm for entry: $entryId")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this mood entry?")
            .setPositiveButton("Delete") { _, _ ->
                Log.d("MoodFragment", "Deleting mood entry: $entryId")
                viewModel.deleteMoodEntry(entryId)
                showSnackbar("Mood entry deleted")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        Log.d("MoodFragment", "Updating empty state: isEmpty=$isEmpty")
        Log.d("MoodFragment", "RecyclerView visibility before: ${todayMoodsRecyclerView.visibility}")
        Log.d("MoodFragment", "Empty state visibility before: ${emptyStateText.visibility}")
        Log.d("MoodFragment", "Adapter item count: ${adapter.itemCount}")
        
        emptyStateText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        todayMoodsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        
        Log.d("MoodFragment", "RecyclerView visibility after: ${todayMoodsRecyclerView.visibility}")
        Log.d("MoodFragment", "Empty state visibility after: ${emptyStateText.visibility}")
        
        // Force RecyclerView to request layout
        todayMoodsRecyclerView.requestLayout()
    }

    private fun scrollToShowNewEntry() {
        Log.d("MoodFragment", "Scrolling to show new entry")
        
        // Post the scroll action to ensure the RecyclerView is ready
        todayMoodsRecyclerView.post {
            val itemCount = adapter.itemCount
            if (itemCount > 0) {
                // Scroll to the last item (newest entry) to show the newly added one
                todayMoodsRecyclerView.smoothScrollToPosition(itemCount - 1)
                Log.d("MoodFragment", "Scrolled to position ${itemCount - 1}")
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
    
    private fun shareMoodSummary() {
        try {
            val recentEntries = viewModel.getRecentMoodEntries(7) // Last 7 days
            
            if (recentEntries.isEmpty()) {
                showSnackbar("No mood data to share yet. Log some moods first!")
                return
            }
            
            // Generate mood summary text
            val summaryText = generateMoodSummaryText(recentEntries)
            
            // Create share intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, summaryText)
                putExtra(Intent.EXTRA_SUBJECT, "My Mood Summary - DailyBloom")
            }
            
            // Start share activity
            startActivity(Intent.createChooser(shareIntent, "Share Mood Summary"))
            
        } catch (e: Exception) {
            Log.e("MoodFragment", "Error sharing mood summary", e)
            showSnackbar("Error sharing mood summary")
        }
    }
    
    private fun generateMoodSummaryText(entries: List<com.example.dailybloom2.data.MoodEntry>): String {
        val calendar = java.util.Calendar.getInstance()
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(calendar.time)
        
        // Group entries by date
        val entriesByDate = entries.groupBy { it.date }
        
        // Calculate statistics
        val totalEntries = entries.size
        val totalDays = entriesByDate.keys.size
        val avgEntriesPerDay = if (totalDays > 0) String.format("%.1f", totalEntries.toDouble() / totalDays) else "0"
        
        // Find most common mood
        val moodCounts = entries.groupingBy { it.emoji }.eachCount()
        val mostCommonMood = moodCounts.maxByOrNull { it.value }
        val mostCommonMoodText = when (mostCommonMood?.key) {
            "😊" -> "Happy"
            "🤩" -> "Excited"
            "😐" -> "Neutral"
            "😢" -> "Sad"
            "😠" -> "Angry"
            else -> "Mixed"
        }
        
        // Get today's entries count
        val todayEntriesCount = entriesByDate[today]?.size ?: 0
        
        // Build summary text
        val summary = StringBuilder()
        summary.appendLine("📊 My Mood Summary - DailyBloom")
        summary.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        summary.appendLine()
        summary.appendLine("📅 Period: Last ${totalDays} days")
        summary.appendLine("📝 Total mood entries: $totalEntries")
        summary.appendLine("📈 Average per day: $avgEntriesPerDay")
        summary.appendLine("😊 Most common mood: $mostCommonMoodText ${mostCommonMood?.key ?: ""}")
        summary.appendLine("📅 Today's entries: $todayEntriesCount")
        summary.appendLine()
        
        // Add recent mood trend
        summary.appendLine("📈 Recent Mood Trend:")
        val recentDates = entriesByDate.keys.sorted().takeLast(5)
        for (date in recentDates) {
            val dayEntries = entriesByDate[date] ?: emptyList()
            val dayLabel = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).parse(date)!!
            )
            
            if (dayEntries.isNotEmpty()) {
                val dominantMood = dayEntries.groupingBy { it.emoji }.eachCount().maxByOrNull { it.value }?.key ?: "😐"
                summary.appendLine("  $dayLabel: $dominantMood (${dayEntries.size} entries)")
            } else {
                summary.appendLine("  $dayLabel: No entries")
            }
        }
        
        summary.appendLine()
        summary.appendLine("💡 Keep tracking your mood with DailyBloom!")
        summary.appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        
        return summary.toString()
    }
}