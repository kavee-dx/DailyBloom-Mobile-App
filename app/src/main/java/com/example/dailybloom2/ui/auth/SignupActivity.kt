package com.example.dailybloom2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import com.example.dailybloom2.R
import com.example.dailybloom2.data.UserPreferences
import com.example.dailybloom2.ui.auth.LoginActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var profilePicImageView: ImageView
    private lateinit var termsCheckBox: CheckBox
    private lateinit var signupButton: Button
    private lateinit var loginText: TextView
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        profilePicImageView = findViewById(R.id.profilePicImageView)
        termsCheckBox = findViewById(R.id.termsCheckBox)
        signupButton = findViewById(R.id.signupButton)
        loginText = findViewById(R.id.loginText)
        loginButton = findViewById(R.id.loginButton)
    }

    private fun setupClickListeners() {
        profilePicImageView.setOnClickListener {
            handleProfilePicUpload()
        }

        signupButton.setOnClickListener {
            handleSignup()
        }

        loginButton.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleProfilePicUpload() {
        Toast.makeText(this, "Profile picture upload clicked", Toast.LENGTH_SHORT).show()
        // TODO: Implement profile picture upload logic
    }

    private fun handleSignup() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
            phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Phone number validation (10 digits only)
        if (!phone.matches(Regex("^[0-9]{10}$"))) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (!termsCheckBox.isChecked) {
            Toast.makeText(this, "Please agree to terms and conditions", Toast.LENGTH_SHORT).show()
            return
        }

        // Save user data in SharedPreferences
        UserPreferences.saveUser(this, firstName, lastName, email, phone, password, "profile_pic_path_here")

        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()

        // Navigate to login
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }



    private fun handleLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
