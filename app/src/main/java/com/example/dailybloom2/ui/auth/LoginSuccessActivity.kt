package com.example.dailybloom2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.dailybloom2.R
import com.example.dailybloom2.ui.home.HomeActivity

class LoginSuccessActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginSuccessActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_success)
        
        setupFragment()
        navigateToHome()
    }

    private fun setupFragment() {
        val fragment = LoginSuccessFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    private fun navigateToHome() {
        // Auto navigate to home page after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                // Handle error silently
            }
        }, 3000)
    }
}
