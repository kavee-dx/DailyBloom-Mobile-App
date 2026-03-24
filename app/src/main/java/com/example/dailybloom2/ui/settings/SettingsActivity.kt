package com.example.dailybloom2.ui.settings

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailybloom2.R
import com.example.dailybloom2.data.UserPreferences
import com.example.dailybloom2.ui.auth.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var profileImage: ImageView
    private lateinit var fullNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView
    private lateinit var editNameButton: MaterialButton
    private lateinit var changePasswordButton: MaterialButton
    private lateinit var logoutButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        setupToolbar()
        loadUserData()
        setupClickListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        profileImage = findViewById(R.id.profileImage)
        fullNameText = findViewById(R.id.fullNameText)
        emailText = findViewById(R.id.emailText)
        phoneText = findViewById(R.id.phoneText)
        editNameButton = findViewById(R.id.editNameButton)
        changePasswordButton = findViewById(R.id.changePasswordButton)
        logoutButton = findViewById(R.id.logoutButton)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Settings"
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadUserData() {
        // Load user details from UserPreferences
        val firstName = UserPreferences.getUserFirstName(this) ?: ""
        val lastName = UserPreferences.getUserLastName(this) ?: ""
        val email = UserPreferences.getUserEmail(this) ?: ""
        val phone = UserPreferences.getUserPhone(this) ?: ""
        val profilePicPath = UserPreferences.getUserProfilePic(this)

        // Combine first and last name
        val fullName = if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            "$firstName $lastName"
        } else if (firstName.isNotEmpty()) {
            firstName
        } else {
            "User"
        }

        // Load profile picture
        if (!profilePicPath.isNullOrEmpty() && profilePicPath != "profile_pic_path_here") {
            try {
                val bitmap = BitmapFactory.decodeFile(profilePicPath)
                if (bitmap != null) {
                    profileImage.setImageBitmap(bitmap)
                } else {
                    profileImage.setImageResource(R.drawable.ic_profile_placeholder)
                }
            } catch (e: Exception) {
                profileImage.setImageResource(R.drawable.ic_profile_placeholder)
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_profile_placeholder)
        }

        // Load user details
        fullNameText.text = fullName
        emailText.text = email
        phoneText.text = phone
    }

    private fun setupClickListeners() {
        // Edit name click
        editNameButton.setOnClickListener {
            showEditNameDialog()
        }

        // Logout button
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        // Change password button
        changePasswordButton.setOnClickListener {
            Toast.makeText(this, "Change Password button clicked!", Toast.LENGTH_SHORT).show()
            showChangePasswordDialog()
        }
    }

    private fun showEditNameDialog() {
        val firstName = UserPreferences.getUserFirstName(this) ?: ""
        val lastName = UserPreferences.getUserLastName(this) ?: ""
        val currentFullName = if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            "$firstName $lastName"
        } else if (firstName.isNotEmpty()) {
            firstName
        } else {
            ""
        }

        val input = TextInputEditText(this)
        input.setText(currentFullName)
        input.hint = "Enter your full name"

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Full Name")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    // Split the name into first and last name
                    val nameParts = newName.split(" ")
                    val newFirstName = nameParts.firstOrNull() ?: ""
                    val newLastName = if (nameParts.size > 1) {
                        nameParts.drop(1).joinToString(" ")
                    } else {
                        ""
                    }

                    // Update UserPreferences with new names
                    val email = UserPreferences.getUserEmail(this) ?: ""
                    val phone = UserPreferences.getUserPhone(this) ?: ""
                    val password = UserPreferences.getUserPassword(this) ?: ""
                    val profilePic = UserPreferences.getUserProfilePic(this) ?: ""

                    UserPreferences.saveUser(this, newFirstName, newLastName, email, phone, password, profilePic)

                    // Update UI
                    fullNameText.text = newName
                    Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                // Clear user session and navigate to login
                UserPreferences.clearUser(this)
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        // Test with a simple dialog first
        MaterialAlertDialogBuilder(this)
            .setTitle("Change Password")
            .setMessage("This is a test dialog. Click OK to proceed.")
            .setPositiveButton("OK") { _, _ ->
                Toast.makeText(this, "Dialog is working!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updatePasswordAndRedirect(newPassword: String) {
        // Get current user data
        val firstName = UserPreferences.getUserFirstName(this) ?: ""
        val lastName = UserPreferences.getUserLastName(this) ?: ""
        val email = UserPreferences.getUserEmail(this) ?: ""
        val phone = UserPreferences.getUserPhone(this) ?: ""
        val profilePic = UserPreferences.getUserProfilePic(this) ?: ""
        
        // Save user data with new password
        UserPreferences.saveUser(this, firstName, lastName, email, phone, newPassword, profilePic)
        
        Toast.makeText(this, "Password changed successfully! Please login again.", Toast.LENGTH_LONG).show()
        
        // Redirect to login page
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
