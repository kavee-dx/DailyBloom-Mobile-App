package com.example.dailybloom2

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dailybloom2.ui.onboarding.OnboardingActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val rootLayout: RelativeLayout = findViewById(R.id.main)
        val logo: ImageView = findViewById(R.id.logo)
        val slogan: TextView = findViewById(R.id.slogan)
        val appName: TextView = findViewById(R.id.appName)

        // Start animated gradient
        val animationDrawable = rootLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2000)
        animationDrawable.setExitFadeDuration(2000)
        animationDrawable.start()

        // Animate logo with scale and fade
        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .setStartDelay(500)
            .start()

        // Animate slogan with slide up and fade
        slogan.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(1200)
            .start()

        // Animate app name with slide up and fade
        appName.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(1600)
            .start()

        // Move to next screen after 3.5 sec
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }, 3500)
    }
}

