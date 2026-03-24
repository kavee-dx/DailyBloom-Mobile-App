package com.example.dailybloom2.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailybloom2.R
import com.example.dailybloom2.data.UserPreferences
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
        private const val PREFS_NAME = "home_prefs"
        private const val KEY_HABITS_COMPLETED = "habits_completed"
        private const val KEY_TOTAL_HABITS = "total_habits"
        private const val KEY_GLASSES_TODAY = "glasses_today"
        private const val KEY_TARGET_GLASSES = "target_glasses"
        private const val KEY_MOOD_STREAK = "mood_streak"
        private const val KEY_CURRENT_MOOD = "current_mood"
    }

    private lateinit var profileImageView: ImageView
    private lateinit var userNameText: TextView
    private lateinit var greetingText: TextView
    private lateinit var motivationQuote: TextView
    private lateinit var appLogoImageView: ImageView
    private lateinit var appNameTextView: TextView
    
    // Summary boxes
    private lateinit var habitsCard: MaterialCardView
    private lateinit var hydrationCard: MaterialCardView
    private lateinit var moodCard: MaterialCardView
    
    // Habits
    private lateinit var habitsProgressText: TextView
    private lateinit var habitsPercentageText: TextView
    private lateinit var habitsProgressBar: ProgressBar
    
    // Hydration
    private lateinit var hydrationStatusText: TextView
    private lateinit var hydrationPercentageText: TextView
    private lateinit var hydrationProgressBar: ProgressBar
    
    // Mood
    private lateinit var moodStatusText: TextView
    private lateinit var moodStreakText: TextView
    private lateinit var moodProgressBar: ProgressBar

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "HomeFragment onViewCreated called")

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)

        initViews(view)
        updateUserProfile()
        updateGreeting()
        updateMotivationQuote()
        updateHabitsSummary()
        updateHydrationSummary()
        updateMoodSummary()
        setupClickListeners()

        Log.d(TAG, "HomeFragment setup completed")
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh all summaries when returning to home page
        updateHabitsSummary()
        updateHydrationSummary()
        updateMoodSummary()
        Log.d(TAG, "HomeFragment resumed - all summaries refreshed")
    }

    private fun initViews(view: View) {
        profileImageView = view.findViewById(R.id.profileImageView)
        userNameText = view.findViewById(R.id.userNameText)
        greetingText = view.findViewById(R.id.greetingText)
        motivationQuote = view.findViewById(R.id.motivationQuote)
        appLogoImageView = view.findViewById(R.id.appLogoImageView)
        appNameTextView = view.findViewById(R.id.appNameTextView)
        
        // Summary boxes
        habitsCard = view.findViewById(R.id.habitsCard)
        hydrationCard = view.findViewById(R.id.hydrationCard)
        moodCard = view.findViewById(R.id.moodCard)
        
        // Habits
        habitsProgressText = view.findViewById(R.id.habitsProgressText)
        habitsPercentageText = view.findViewById(R.id.habitsPercentageText)
        habitsProgressBar = view.findViewById(R.id.habitsProgressBar)
        
        // Hydration
        hydrationStatusText = view.findViewById(R.id.hydrationStatusText)
        hydrationPercentageText = view.findViewById(R.id.hydrationPercentageText)
        hydrationProgressBar = view.findViewById(R.id.hydrationProgressBar)
        
        // Mood
        moodStatusText = view.findViewById(R.id.moodStatusText)
        moodStreakText = view.findViewById(R.id.moodStreakText)
        moodProgressBar = view.findViewById(R.id.moodProgressBar)
    }

    private fun updateUserProfile() {
        val firstName = UserPreferences.getUserFirstName(requireContext())
        val lastName = UserPreferences.getUserLastName(requireContext())
        val profilePic = UserPreferences.getUserProfilePic(requireContext())
        
        Log.d(TAG, "User profile - FirstName: $firstName, LastName: $lastName")
        
        if (firstName != null && lastName != null) {
            userNameText.text = "$firstName $lastName"
            Log.d(TAG, "User name set: $firstName $lastName")
        } else {
            userNameText.text = "Welcome!"
            Log.d(TAG, "No user data found, using default welcome")
        }
        
        // TODO: Load actual profile picture if available
        // For now, using placeholder
        profileImageView.setImageResource(R.drawable.ic_profile_placeholder)
    }

    private fun updateGreeting() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val greeting = when (hour) {
            in 5..11 -> getString(R.string.home_good_morning)
            in 12..17 -> getString(R.string.home_good_afternoon)
            in 18..21 -> getString(R.string.home_good_evening)
            else -> getString(R.string.home_good_night)
        }
        
        greetingText.text = greeting
    }

    private fun updateMotivationQuote() {
        val motivationQuotes = listOf(
            "Every small step towards your goals is progress worth celebrating! 🌟",
            "You are stronger than you think and more capable than you imagine! 💪",
            "Today is a new opportunity to be better than yesterday! ✨",
            "Believe in yourself and all that you are! 🌈",
            "Success is not final, failure is not fatal: it is the courage to continue that counts! 🚀",
            "Your only limit is your mind! 🧠",
            "Dream big, work hard, stay focused! 🎯",
            "Every expert was once a beginner! 🌱"
        )
        val randomQuote = motivationQuotes.random()
        motivationQuote.text = randomQuote
    }

    private fun updateHabitsSummary() {
        // Get actual habit data from the habits repository
        val habitsRepository = com.example.dailybloom2.data.HabitRepository(requireContext())
        val allHabits = habitsRepository.getAllHabits()
        
        val totalHabits = allHabits.size
        val completedHabits = allHabits.count { it.isCompleted }
        val progressPercentage = if (totalHabits > 0) (completedHabits * 100) / totalHabits else 0
        
        habitsProgressText.text = "$completedHabits/$totalHabits habits completed"
        habitsPercentageText.text = "$progressPercentage% Complete"
        habitsProgressBar.progress = progressPercentage
        
        Log.d(TAG, "Habits summary updated: $completedHabits/$totalHabits ($progressPercentage%)")
    }

    private fun updateHydrationSummary() {
        // Get actual hydration data from hydration preferences
        val hydrationPrefs = requireContext().getSharedPreferences("hydration_prefs", android.content.Context.MODE_PRIVATE)
        val glassesToday = hydrationPrefs.getInt("glasses_consumed", 0)
        val targetGlasses = hydrationPrefs.getInt("daily_goal", 8)
        val progressPercentage = if (targetGlasses > 0) (glassesToday * 100) / targetGlasses else 0
        
        // TODO: Add last drink time tracking to hydration fragment
        // For now, show a placeholder message
        val lastDrinkTime = if (glassesToday > 0) "Recently" else "Not today"
        
        hydrationStatusText.text = "$glassesToday/$targetGlasses glasses today"
        hydrationPercentageText.text = "$progressPercentage% Complete • Last drink: $lastDrinkTime"
        hydrationProgressBar.progress = progressPercentage
        
        Log.d(TAG, "Hydration summary updated: $glassesToday/$targetGlasses ($progressPercentage%)")
    }

    private fun updateMoodSummary() {
        // Get actual mood data from mood repository
        val moodRepository = com.example.dailybloom2.data.MoodRepository(requireContext())
        val todayDate = getTodayDateString()
        val todayMoodEntries = moodRepository.getMoodEntriesForDate(todayDate)
        
        if (todayMoodEntries.isNotEmpty()) {
            // Get the most recent mood entry
            val latestMood = todayMoodEntries.maxByOrNull { it.timestamp }
            val currentMood = latestMood?.emoji ?: "😊"
            val moodCount = todayMoodEntries.size
            
            moodStatusText.text = "Today's mood: $currentMood"
            moodStreakText.text = "$moodCount mood${if (moodCount != 1) "s" else ""} logged today!"
            moodProgressBar.progress = 100 // Mood is always 100% when logged
        } else {
            moodStatusText.text = "Today's mood: Not logged yet"
            moodStreakText.text = "Log your first mood!"
            moodProgressBar.progress = 0
        }
        
        Log.d(TAG, "Mood summary updated: ${todayMoodEntries.size} entries today")
    }
    
    private fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }

    private fun setupClickListeners() {
        // Habits card click - navigate to habits page
        habitsCard.setOnClickListener {
            if (activity is HomeActivity) {
                (activity as HomeActivity).switchToTab(1) // Habits tab
            }
        }

        // Hydration card click - navigate to hydration page
        hydrationCard.setOnClickListener {
            if (activity is HomeActivity) {
                (activity as HomeActivity).switchToTab(2) // Hydration tab
            }
        }

        // Mood card click - navigate to mood page
        moodCard.setOnClickListener {
            if (activity is HomeActivity) {
                (activity as HomeActivity).switchToTab(3) // Mood tab
            }
        }
    }

    // Method to update hydration data when user drinks water - now gets actual data from hydration prefs
    fun updateHydrationData(glassesConsumed: Int) {
        // No longer needed since we get data directly from hydration preferences
        updateHydrationSummary()
    }
    
    // Method to refresh hydration summary with latest data
    fun refreshHydrationSummary() {
        updateHydrationSummary()
    }

    // Method to update habits data - now gets actual data from repository
    fun updateHabitsData(completed: Int, total: Int) {
        // No longer needed since we get data directly from repository
        updateHabitsSummary()
    }
    
    // Method to refresh habits summary with latest data
    fun refreshHabitsSummary() {
        updateHabitsSummary()
    }

    // Method to update mood data - now gets actual data from mood repository
    fun updateMoodData(mood: String, streak: Int) {
        // No longer needed since we get data directly from mood repository
        updateMoodSummary()
    }
    
    // Method to refresh mood summary with latest data
    fun refreshMoodSummary() {
        updateMoodSummary()
    }
}
