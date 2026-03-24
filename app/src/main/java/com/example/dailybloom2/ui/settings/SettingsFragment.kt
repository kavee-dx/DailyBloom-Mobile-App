package com.example.dailybloom2.ui.settings

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailybloom2.R
import com.example.dailybloom2.data.UserPreferences
import com.example.dailybloom2.ui.auth.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class SettingsFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var firstNameText: TextView
    private lateinit var lastNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView
    private lateinit var editFirstNameButton: MaterialButton
    private lateinit var editLastNameButton: MaterialButton
    private lateinit var changePasswordButton: MaterialButton
    private lateinit var logoutButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        loadUserData()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        profileImage = view.findViewById(R.id.profileImage)
        firstNameText = view.findViewById(R.id.firstNameText)
        lastNameText = view.findViewById(R.id.lastNameText)
        emailText = view.findViewById(R.id.emailText)
        phoneText = view.findViewById(R.id.phoneText)
        editFirstNameButton = view.findViewById(R.id.editFirstNameButton)
        editLastNameButton = view.findViewById(R.id.editLastNameButton)
        changePasswordButton = view.findViewById(R.id.changePasswordButton)
        logoutButton = view.findViewById(R.id.logoutButton)
    }

    private fun loadUserData() {
        // Load user details from UserPreferences
        val firstName = UserPreferences.getUserFirstName(requireContext()) ?: ""
        val lastName = UserPreferences.getUserLastName(requireContext()) ?: ""
        val email = UserPreferences.getUserEmail(requireContext()) ?: ""
        val phone = UserPreferences.getUserPhone(requireContext()) ?: ""
        val profilePicPath = UserPreferences.getUserProfilePic(requireContext())

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
        firstNameText.text = firstName.ifEmpty { "Not set" }
        lastNameText.text = lastName.ifEmpty { "Not set" }
        emailText.text = email.ifEmpty { "Not set" }
        phoneText.text = phone.ifEmpty { "Not set" }
    }

    private fun setupClickListeners() {
        // Edit first name click
        editFirstNameButton.setOnClickListener {
            showEditFirstNameDialog()
        }

        // Edit last name click
        editLastNameButton.setOnClickListener {
            showEditLastNameDialog()
        }

        // Logout button
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        // Change password button
        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun showEditFirstNameDialog() {
        val currentFirstName = UserPreferences.getUserFirstName(requireContext()) ?: ""

        val input = TextInputEditText(requireContext())
        input.setText(currentFirstName)
        input.hint = "Enter your first name"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit First Name")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newFirstName = input.text.toString().trim()
                if (newFirstName.isNotEmpty()) {
                    // Update UserPreferences with new first name
                    val lastName = UserPreferences.getUserLastName(requireContext()) ?: ""
                    val email = UserPreferences.getUserEmail(requireContext()) ?: ""
                    val phone = UserPreferences.getUserPhone(requireContext()) ?: ""
                    val password = UserPreferences.getUserPassword(requireContext()) ?: ""
                    val profilePic = UserPreferences.getUserProfilePic(requireContext()) ?: ""

                    UserPreferences.saveUser(requireContext(), newFirstName, lastName, email, phone, password, profilePic)

                    // Update UI
                    firstNameText.text = newFirstName
                    Toast.makeText(requireContext(), "First name updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid first name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditLastNameDialog() {
        val currentLastName = UserPreferences.getUserLastName(requireContext()) ?: ""

        val input = TextInputEditText(requireContext())
        input.setText(currentLastName)
        input.hint = "Enter your last name"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Last Name")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newLastName = input.text.toString().trim()
                if (newLastName.isNotEmpty()) {
                    // Update UserPreferences with new last name
                    val firstName = UserPreferences.getUserFirstName(requireContext()) ?: ""
                    val email = UserPreferences.getUserEmail(requireContext()) ?: ""
                    val phone = UserPreferences.getUserPhone(requireContext()) ?: ""
                    val password = UserPreferences.getUserPassword(requireContext()) ?: ""
                    val profilePic = UserPreferences.getUserProfilePic(requireContext()) ?: ""

                    UserPreferences.saveUser(requireContext(), firstName, newLastName, email, phone, password, profilePic)

                    // Update UI
                    lastNameText.text = newLastName
                    Toast.makeText(requireContext(), "Last name updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid last name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                // Clear user session and navigate to login
                UserPreferences.clearUser(requireContext())
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        // Create TextInputLayouts with password visibility toggle
        val newPasswordLayout = com.google.android.material.textfield.TextInputLayout(requireContext())
        newPasswordLayout.hint = "New Password"
        newPasswordLayout.endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
        newPasswordLayout.boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
        
        val newPasswordInput = TextInputEditText(requireContext())
        newPasswordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        newPasswordLayout.addView(newPasswordInput)
        
        val confirmPasswordLayout = com.google.android.material.textfield.TextInputLayout(requireContext())
        confirmPasswordLayout.hint = "Re-enter Password"
        confirmPasswordLayout.endIconMode = com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
        confirmPasswordLayout.boxBackgroundMode = com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE
        
        val confirmPasswordInput = TextInputEditText(requireContext())
        confirmPasswordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        confirmPasswordLayout.addView(confirmPasswordInput)

        // Create a layout to hold all inputs
        val layout = android.widget.LinearLayout(requireContext())
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)
        
        layout.addView(newPasswordLayout)
        layout.addView(confirmPasswordLayout)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Password")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val newPassword = newPasswordInput.text.toString().trim()
                val confirmPassword = confirmPasswordInput.text.toString().trim()

                // Validate inputs
                if (newPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a new password", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Please re-enter your password", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword.length < 6) {
                    Toast.makeText(requireContext(), "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Update password and redirect to login
                updatePasswordAndRedirect(newPassword)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updatePasswordAndRedirect(newPassword: String) {
        // Get current user data
        val firstName = UserPreferences.getUserFirstName(requireContext()) ?: ""
        val lastName = UserPreferences.getUserLastName(requireContext()) ?: ""
        val email = UserPreferences.getUserEmail(requireContext()) ?: ""
        val phone = UserPreferences.getUserPhone(requireContext()) ?: ""
        val profilePic = UserPreferences.getUserProfilePic(requireContext()) ?: ""
        
        // Save user data with new password
        UserPreferences.saveUser(requireContext(), firstName, lastName, email, phone, newPassword, profilePic)
        
        Toast.makeText(requireContext(), "Password changed successfully! Please login again.", Toast.LENGTH_LONG).show()
        
        // Redirect to login page
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
