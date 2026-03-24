package com.example.dailybloom2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailybloom2.R
import com.example.dailybloom2.ui.home.HomeActivity

class LoginSuccessFragment : Fragment() {

    private lateinit var appLogo: ImageView
    private lateinit var checkImage: ImageView
    private lateinit var successText: TextView
    private lateinit var loadingText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        startAnimations()

    }

    private fun initViews(view: View) {
        appLogo = view.findViewById(R.id.appLogo)
        checkImage = view.findViewById(R.id.checkImage)
        successText = view.findViewById(R.id.successText)
        loadingText = view.findViewById(R.id.loadingText)

    }



    private fun startAnimations() {
        // Animate app logo with fade in
        appLogo.alpha = 0f
        appLogo.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(200)
            .start()

        // Animate check image with zoom effect
        checkImage.alpha = 0f
        checkImage.scaleX = 0.3f
        checkImage.scaleY = 0.3f
        
        checkImage.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .setStartDelay(800)
            .withEndAction {
                // Add bounce effect after zoom
                val bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce)
                checkImage.startAnimation(bounceAnimation)
            }
            .start()

        // Animate success text with slide up and fade in
        successText.alpha = 0f
        successText.translationY = 50f
        
        successText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(1200)
            .start()

        // Animate loading text with fade in
        loadingText.alpha = 0f
        loadingText.animate()
            .alpha(0.7f)
            .setDuration(600)
            .setStartDelay(1800)
            .start()
    }
}
