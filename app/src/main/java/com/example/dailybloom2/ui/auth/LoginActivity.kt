package com.example.dailybloom2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dailybloom2.R
import com.example.dailybloom2.data.UserPreferences
import com.example.dailybloom2.ui.auth.SignupActivity
import com.example.dailybloom2.ui.auth.LoginSuccessActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordText: TextView
    private lateinit var signupText: TextView
    private lateinit var signupButton: Button
    private lateinit var rememberMeCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupClickListeners()
        loadSavedCredentials()
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)
        signupText = findViewById(R.id.signupText)
        signupButton = findViewById(R.id.signupButton)
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            handleLogin()
        }

        forgotPasswordText.setOnClickListener {
            handleForgotPassword()
        }

        signupButton.setOnClickListener {
            handleSignup()
        }
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val savedEmail = UserPreferences.getUserEmail(this)
        val savedPassword = UserPreferences.getUserPassword(this)

        if (savedEmail == null || savedPassword == null) {
            Toast.makeText(this, "No account found. Please sign up first.", Toast.LENGTH_SHORT).show()
            return
        }

        if (email == savedEmail && password == savedPassword) {
            // Save remember me preference
            UserPreferences.setRememberMe(this, rememberMeCheckBox.isChecked, email, password)
            
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginSuccessActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
        }
    }


    private fun handleForgotPassword() {
        showForgotPasswordDialog()
    }
    
    private fun showForgotPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.newPasswordEditText)
        val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.confirmPasswordEditText)
        
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("Enter your new password")
            .setView(dialogView)
            .setPositiveButton("Reset Password") { _, _ ->
                val newPassword = newPasswordEditText.text.toString().trim()
                val confirmPassword = confirmPasswordEditText.text.toString().trim()
                
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                if (newPassword.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                // Update password in UserPreferences
                updatePassword(newPassword)
            }
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
    }
    
    private fun updatePassword(newPassword: String) {
        // Get current user data
        val firstName = UserPreferences.getUserFirstName(this) ?: ""
        val lastName = UserPreferences.getUserLastName(this) ?: ""
        val email = UserPreferences.getUserEmail(this) ?: ""
        val phone = UserPreferences.getUserPhone(this) ?: ""
        val profilePic = UserPreferences.getUserProfilePic(this) ?: ""
        
        // Save user data with new password
        UserPreferences.saveUser(this, firstName, lastName, email, phone, newPassword, profilePic)
        
        Toast.makeText(this, "Password updated successfully! You can now login with your new password.", Toast.LENGTH_LONG).show()
    }

    private fun handleSignup() {
        startActivity(Intent(this, SignupActivity::class.java))
        finish()
    }
    
    private fun loadSavedCredentials() {
        if (UserPreferences.isRememberMeEnabled(this)) {
            val savedEmail = UserPreferences.getSavedEmail(this)
            val savedPassword = UserPreferences.getSavedPassword(this)
            
            if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
                emailEditText.setText(savedEmail)
                passwordEditText.setText(savedPassword)
                rememberMeCheckBox.isChecked = true
            }
        }
    }
}
