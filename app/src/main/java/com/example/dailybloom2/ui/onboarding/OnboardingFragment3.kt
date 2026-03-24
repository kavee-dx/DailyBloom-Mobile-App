package com.example.dailybloom2.ui.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.dailybloom2.R

class OnboardingFragment3 : Fragment() {

    companion object {
        private const val TAG = "OnboardingFragment3"
    }

    private var imageView: ImageView? = null
    private var titleView: TextView? = null
    private var descriptionView: TextView? = null
    private var animationsStarted = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_onboarding_3, container, false)

        // find views (these IDs must exist in the layout)
        imageView = view.findViewById<ImageView?>(R.id.onboardingImage)
        titleView = view.findViewById<TextView?>(R.id.onboardingTitle)
        descriptionView = view.findViewById<TextView?>(R.id.onboardingDescription)

        if (imageView == null || titleView == null || descriptionView == null) {
            Log.e(TAG, "One or more views are null. Check layout filename and view IDs.")
            // Return early to avoid NullPointerException
            return view
        }

        // Set initial state for animations
        imageView!!.alpha = 0f
        titleView!!.alpha = 0f
        descriptionView!!.alpha = 0f

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called for OnboardingFragment3")
        
        // Start animations when fragment becomes visible
        if (!animationsStarted && imageView != null && titleView != null && descriptionView != null) {
            Log.d(TAG, "Starting animations in onResume")
            startAnimations()
            animationsStarted = true
        }
    }

    private fun startAnimations() {
        Log.d(TAG, "Starting animations for OnboardingFragment3")
        
        val image = imageView!!
        val title = titleView!!
        val description = descriptionView!!
        
        // Slide up animation for image
        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
            startOffset = 200
        }
        image.startAnimation(slideUp)
        image.animate().alpha(1f).setDuration(800).setStartDelay(200).start()

        val fadeInTitle = AnimationUtils.loadAnimation(context, R.anim.fade_in).apply {
            startOffset = 600
        }
        title.startAnimation(fadeInTitle)
        title.animate().alpha(1f).setDuration(600).setStartDelay(600).start()

        val fadeInDesc = AnimationUtils.loadAnimation(context, R.anim.fade_in).apply {
            startOffset = 1000
        }
        description.startAnimation(fadeInDesc)
        description.animate().alpha(1f).setDuration(600).setStartDelay(1000).start()
    }
}
