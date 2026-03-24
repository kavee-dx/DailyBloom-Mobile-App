package com.example.dailybloom2.ui.hydration

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dailybloom2.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import java.text.SimpleDateFormat
import java.util.*

class HydrationFragment : Fragment() {

    companion object {
        private const val PREFS_NAME = "hydration_prefs"
        private const val KEY_GLASSES_CONSUMED = "glasses_consumed"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_INTERVAL = "reminder_interval"
        private const val KEY_LAST_RESET_DATE = "last_reset_date"
        private const val NOTIFICATION_CHANNEL_ID = "hydration_reminder_channel"
        private const val NOTIFICATION_ID = 1001
        private const val GLASS_SIZE_ML = 250 // Standard glass size
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var alarmManager: AlarmManager
    private lateinit var notificationManager: NotificationManager

    // UI Components
    private lateinit var waterImage: ImageView
    private lateinit var progressCircle: CircularProgressIndicator
    private lateinit var progressText: TextView
    private lateinit var addWaterButton: MaterialButton
    private lateinit var glassesConsumed: TextView
    private lateinit var waterAmount: TextView
    private lateinit var percentageText: TextView
    private lateinit var reminderSwitch: SwitchMaterial
    private lateinit var interval30min: MaterialButton
    private lateinit var interval1hour: MaterialButton
    private lateinit var interval2hour: MaterialButton
    private lateinit var customIntervalInput: TextInputEditText
    private lateinit var setCustomIntervalButton: MaterialButton
    private lateinit var nextReminderText: TextView
    private lateinit var resetDayButton: MaterialButton
    private lateinit var viewHistoryButton: MaterialButton

    private var glassesConsumedToday = 0
    private var dailyGoal = 8
    private var reminderIntervalMinutes = 60
    private var selectedIntervalButton: MaterialButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initServices()
        loadSavedData()
        setupClickListeners()
        updateUI()
    }

    private fun initViews(view: View) {
        waterImage = view.findViewById(R.id.waterImage)
        progressCircle = view.findViewById(R.id.progressCircle)
        progressText = view.findViewById(R.id.progressText)
        addWaterButton = view.findViewById(R.id.addWaterButton)
        glassesConsumed = view.findViewById(R.id.glassesConsumed)
        waterAmount = view.findViewById(R.id.waterAmount)
        percentageText = view.findViewById(R.id.percentageText)
        reminderSwitch = view.findViewById(R.id.reminderSwitch)
        interval30min = view.findViewById(R.id.interval30min)
        interval1hour = view.findViewById(R.id.interval1hour)
        interval2hour = view.findViewById(R.id.interval2hour)
        customIntervalInput = view.findViewById(R.id.customIntervalInput)
        setCustomIntervalButton = view.findViewById(R.id.setCustomIntervalButton)
        nextReminderText = view.findViewById(R.id.nextReminderText)
        resetDayButton = view.findViewById(R.id.resetDayButton)
        viewHistoryButton = view.findViewById(R.id.viewHistoryButton)
    }

    private fun initServices() {
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun loadSavedData() {
        glassesConsumedToday = sharedPreferences.getInt(KEY_GLASSES_CONSUMED, 0)
        dailyGoal = sharedPreferences.getInt(KEY_DAILY_GOAL, 8)
        reminderIntervalMinutes = sharedPreferences.getInt(KEY_REMINDER_INTERVAL, 60)

        // Check if it's a new day and reset if needed
        val lastResetDate = sharedPreferences.getString(KEY_LAST_RESET_DATE, "")
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (lastResetDate != today) {
            // Save yesterday's data before resetting
            if (!lastResetDate.isNullOrEmpty()) {
                saveDailyHydrationData(lastResetDate, glassesConsumedToday)
            }
            
            glassesConsumedToday = 0
            sharedPreferences.edit()
                .putInt(KEY_GLASSES_CONSUMED, 0)
                .putString(KEY_LAST_RESET_DATE, today)
                .apply()
        }

        reminderSwitch.isChecked = sharedPreferences.getBoolean(KEY_REMINDER_ENABLED, false)
        
        // Restore selected interval button state
        restoreIntervalButtonState()
    }

    private fun setupClickListeners() {
        addWaterButton.setOnClickListener {
            addGlassOfWater()
        }

        reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_REMINDER_ENABLED, isChecked).apply()
            if (isChecked) {
                scheduleReminder()
            } else {
                cancelReminder()
            }
        }

        interval30min.setOnClickListener { selectInterval(30, interval30min) }
        interval1hour.setOnClickListener { selectInterval(60, interval1hour) }
        interval2hour.setOnClickListener { selectInterval(120, interval2hour) }

        setCustomIntervalButton.setOnClickListener {
            val customMinutes = customIntervalInput.text.toString().toIntOrNull()
            if (customMinutes != null && customMinutes > 0) {
                selectInterval(customMinutes, null)
                Toast.makeText(context, "Custom interval set to $customMinutes minutes", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "Please enter a valid number of minutes",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        resetDayButton.setOnClickListener {
            resetDay()
        }

        viewHistoryButton.setOnClickListener {
            showHydrationHistoryDialog()
        }
    }

    private fun addGlassOfWater() {
        glassesConsumedToday++
        sharedPreferences.edit().putInt(KEY_GLASSES_CONSUMED, glassesConsumedToday).apply()
        
        // Save today's data
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        saveDailyHydrationData(today, glassesConsumedToday)

        // Update UI
        updateUI()

        // Show success message
        Toast.makeText(context, "Great! Keep hydrating! 💧", Toast.LENGTH_SHORT).show()
    }

    private fun selectInterval(minutes: Int, button: MaterialButton?) {
        reminderIntervalMinutes = minutes
        sharedPreferences.edit().putInt(KEY_REMINDER_INTERVAL, minutes).apply()

        // Update button states
        selectedIntervalButton?.isSelected = false
        button?.isSelected = true
        selectedIntervalButton = button

        // Clear custom input if a preset button was selected
        if (button != null) {
            customIntervalInput.text?.clear()
        }

        // Schedule reminder if enabled
        if (reminderSwitch.isChecked) {
            scheduleReminder()
        } else {
            updateNextReminderText()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, schedule the reminder
                scheduleReminder()
            } else {
                Toast.makeText(context, "Notification permission is required for reminders", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scheduleReminder() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permission without leaving the app
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
                return
            }

            // Cancel any existing reminders first
            cancelReminder()

            val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val intervalMillis = reminderIntervalMinutes * 60 * 1000L
            val triggerTime = System.currentTimeMillis() + intervalMillis

            // Use setRepeating for proper repeating alarms
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                intervalMillis,
                pendingIntent
            )

            updateNextReminderText()
            Toast.makeText(context, "Reminder set for every $reminderIntervalMinutes minutes", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error setting reminder: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun cancelReminder() {
        val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        nextReminderText.text = "Next reminder: Not set"
    }

    private fun updateNextReminderText() {
        if (reminderSwitch.isChecked) {
            val nextReminderTime =
                System.currentTimeMillis() + (reminderIntervalMinutes * 60 * 1000L)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val nextTime = timeFormat.format(Date(nextReminderTime))
            nextReminderText.text = "Next reminder: $nextTime"
        } else {
            nextReminderText.text = "Next reminder: Not set"
        }
    }

    private fun resetDay() {
        glassesConsumedToday = 0
        sharedPreferences.edit()
            .putInt(KEY_GLASSES_CONSUMED, 0)
            .putString(
                KEY_LAST_RESET_DATE,
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            .apply()

        updateUI()
        Toast.makeText(context, "Day reset successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        val percentage = (glassesConsumedToday * 100) / dailyGoal
        val waterAmountMl = glassesConsumedToday * GLASS_SIZE_ML

        // Update progress circle
        progressCircle.progress = percentage

        // Update text displays
        progressText.text = "$glassesConsumedToday/$dailyGoal"
        glassesConsumed.text = glassesConsumedToday.toString()
        waterAmount.text = "${waterAmountMl}ml"
        percentageText.text = "${percentage}%"

        // Update button state based on goal
        if (glassesConsumedToday >= dailyGoal) {
            addWaterButton.text = "🎉 Goal Reached!"
            addWaterButton.isEnabled = false
        } else {
            addWaterButton.text = "💧 Add Glass of Water"
            addWaterButton.isEnabled = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun restoreIntervalButtonState() {
        // Reset all buttons first
        interval30min.isSelected = false
        interval1hour.isSelected = false
        interval2hour.isSelected = false
        
        // Select the appropriate button based on saved interval
        when (reminderIntervalMinutes) {
            30 -> {
                interval30min.isSelected = true
                selectedIntervalButton = interval30min
            }
            60 -> {
                interval1hour.isSelected = true
                selectedIntervalButton = interval1hour
            }
            120 -> {
                interval2hour.isSelected = true
                selectedIntervalButton = interval2hour
            }
            else -> {
                // Custom interval - show in input field
                customIntervalInput.setText(reminderIntervalMinutes.toString())
                selectedIntervalButton = null
            }
        }
        
        updateNextReminderText()
    }
    
    private fun saveDailyHydrationData(date: String, glassesConsumed: Int) {
        val key = "hydration_$date"
        sharedPreferences.edit().putInt(key, glassesConsumed).apply()
    }
    
    private fun getDailyHydrationData(date: String): Int {
        val key = "hydration_$date"
        return sharedPreferences.getInt(key, 0)
    }
    
    private fun showHydrationHistoryDialog() {
        val historyData = getLast5DaysHydrationData()
        
        // Create dialog layout
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)
        
        // Add title
        val titleText = TextView(requireContext())
        titleText.text = "💧 Hydration History (Last 5 Days)"
        titleText.textSize = 18f
        titleText.setPadding(0, 0, 0, 20)
        layout.addView(titleText)
        
        // Add history entries
        for ((date, glasses) in historyData) {
            val dayLayout = LinearLayout(requireContext())
            dayLayout.orientation = LinearLayout.HORIZONTAL
            dayLayout.setPadding(0, 10, 0, 10)
            
            val dateText = TextView(requireContext())
            dateText.text = date
            dateText.textSize = 16f
            dateText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            dayLayout.addView(dateText)
            
            val glassesText = TextView(requireContext())
            glassesText.text = "$glasses glasses"
            glassesText.textSize = 16f
            glassesText.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
            dayLayout.addView(glassesText)
            
            layout.addView(dayLayout)
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hydration History")
            .setView(layout)
            .setPositiveButton("Close", null)
            .show()
    }
    
    private fun getLast5DaysHydrationData(): List<Pair<String, Int>> {
        val historyData = mutableListOf<Pair<String, Int>>()
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val storageFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        for (i in 4 downTo 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = storageFormat.format(calendar.time)
            val displayDate = dateFormat.format(calendar.time)
            val glasses = getDailyHydrationData(date)
            
            historyData.add(Pair(displayDate, glasses))
            calendar.add(Calendar.DAY_OF_YEAR, i) // Reset
        }
        
        return historyData
    }
}
