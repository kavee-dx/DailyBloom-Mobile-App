package com.example.dailybloom2.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.dailybloom2.R
import com.example.dailybloom2.ui.auth.LoginActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var nextButton: Button
    private lateinit var getStartedButton: Button
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        initViews()
        setupViewPager()
        setupTabLayout()
        setupNavigationButtons()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        nextButton = findViewById(R.id.nextButton)
        getStartedButton = findViewById(R.id.getStartedButton)
    }

    private fun setupViewPager() {
        adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter

        // Start with the first screen
        viewPager.currentItem = 0

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonVisibility(position)
            }
        })
    }

    private fun updateButtonVisibility(position: Int) {
        when (position) {
            0, 1 -> {
                // Show Next button for screens 1 and 2
                nextButton.visibility = View.VISIBLE
                getStartedButton.visibility = View.GONE
                nextButton.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start()
            }
            2 -> {
                // Show Get Started button for screen 3
                nextButton.visibility = View.GONE
                getStartedButton.visibility = View.VISIBLE
                getStartedButton.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start()
            }
        }
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
    }

    private fun setupNavigationButtons() {
        // Initialize button states
        nextButton.visibility = View.VISIBLE
        nextButton.alpha = 1f
        getStartedButton.visibility = View.GONE
        getStartedButton.alpha = 0f
        
        nextButton.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < adapter.itemCount - 1) {
                viewPager.currentItem = currentItem + 1
            }
        }

        getStartedButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
