package com.example.dailybloom2.ui.home

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.dailybloom2.R
import com.example.dailybloom2.data.UserPreferences
import com.example.dailybloom2.ui.home.HomeFragment
import com.example.dailybloom2.ui.habits.HabitsFragment
import com.example.dailybloom2.ui.mood.MoodFragment
import com.example.dailybloom2.ui.hydration.HydrationFragment
import com.example.dailybloom2.ui.settings.SettingsFragment
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HomeActivity"
    }

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var welcomeText: TextView
    private lateinit var dateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.d(TAG, "HomeActivity created successfully")

        initViews()
        setupBottomNavigation()
        loadHomeFragment()
        updateWelcomeText()
        updateDateText()
    }

    private fun initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        welcomeText = findViewById(R.id.welcomeText)
        dateText = findViewById(R.id.dateText)
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            Log.d(TAG, "Bottom navigation item clicked: ${item.itemId}")
            when (item.itemId) {
                R.id.nav_home -> {
                    Log.d(TAG, "Home tab clicked")
                    loadHomeFragment()
                    true
                }
                R.id.nav_habits -> {
                    Log.d(TAG, "Habits tab clicked")
                    loadHabitsFragment()
                    true
                }
                R.id.nav_mood -> {
                    Log.d(TAG, "Mood tab clicked")
                    loadMoodFragment()
                    true
                }
                R.id.nav_hydration -> {
                    Log.d(TAG, "Hydration tab clicked")
                    loadHydrationFragment()
                    true
                }
                R.id.nav_settings -> {
                    Log.d(TAG, "Settings tab clicked")
                    loadSettingsFragment()
                    true
                }
                else -> {
                    Log.d(TAG, "Unknown tab clicked: ${item.itemId}")
                    false
                }
            }
        }
    }

    private fun loadHomeFragment() {
        replaceFragment(HomeFragment())
    }

    private fun loadHabitsFragment() {
        Log.d(TAG, "Loading HabitsFragment")
        replaceFragment(HabitsFragment())
    }

    private fun loadMoodFragment() {
        replaceFragment(MoodFragment())
    }

    private fun loadHydrationFragment() {
        replaceFragment(HydrationFragment())
    }

    private fun loadSettingsFragment() {
        replaceFragment(SettingsFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        Log.d(TAG, "Replacing fragment: ${fragment.javaClass.simpleName}")
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commitNow()
            Log.d(TAG, "Fragment replaced successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error replacing fragment", e)
        }
    }

    private fun updateWelcomeText() {
        val firstName = UserPreferences.getUserFirstName(this)
        val lastName = UserPreferences.getUserLastName(this)
        
        Log.d(TAG, "User data - FirstName: $firstName, LastName: $lastName")
        
        val welcomeMessage = if (firstName != null && lastName != null) {
            "Welcome back, $firstName $lastName! 🌸"
        } else {
            "Welcome to DailyBloom! 🌸"
        }
        
        welcomeText.text = welcomeMessage
        Log.d(TAG, "Welcome message set: $welcomeMessage")
    }

    private fun updateDateText() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        val todayDate = dateFormat.format(calendar.time)
        dateText.text = todayDate
        Log.d(TAG, "Date set: $todayDate")
    }

    // Method to switch tabs programmatically (for navigation from home summary cards)
    fun switchToTab(tabIndex: Int) {
        when (tabIndex) {
            0 -> bottomNavigationView.selectedItemId = R.id.nav_home
            1 -> bottomNavigationView.selectedItemId = R.id.nav_habits
            2 -> bottomNavigationView.selectedItemId = R.id.nav_hydration
            3 -> bottomNavigationView.selectedItemId = R.id.nav_mood
            4 -> bottomNavigationView.selectedItemId = R.id.nav_settings
        }
    }
}