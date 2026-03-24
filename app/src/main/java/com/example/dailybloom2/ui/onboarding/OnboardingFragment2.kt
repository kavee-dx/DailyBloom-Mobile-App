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

class OnboardingFragment2 : Fragment() {

    companion object {
        private const val TAG = "OnboardingFragment2"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_onboarding_2, container, false)

        // find views (these IDs must exist in the layout)
        val imageView = view.findViewById<ImageView?>(R.id.onboardingImage)
        val titleView = view.findViewById<TextView?>(R.id.onboardingTitle)
        val descriptionView = view.findViewById<TextView?>(R.id.onboardingDescription)

        if (imageView == null || titleView == null || descriptionView == null) {
            Log.e(TAG, "One or more views are null. Check layout filename and view IDs.")
            // Return early to avoid NullPointerException
            return view
        }

        // Image is already set in the layout XML
        // No need to load it programmatically unless you want to change it dynamically

        // Animate elements (these anim resource files must exist)
        animateElements(imageView, titleView, descriptionView)

        return view
    }

    private fun animateElements(imageView: ImageView, titleView: TextView, descriptionView: TextView) {
        // Set initial alpha to 0 for animation
        imageView.alpha = 0f
        titleView.alpha = 0f
        descriptionView.alpha = 0f
        
        // Slide up animation for image
        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
            startOffset = 200
        }
        imageView.startAnimation(slideUp)
        imageView.animate().alpha(1f).setDuration(800).setStartDelay(200).start()

        val fadeInTitle = AnimationUtils.loadAnimation(context, R.anim.fade_in).apply {
            startOffset = 600
        }
        titleView.startAnimation(fadeInTitle)
        titleView.animate().alpha(1f).setDuration(600).setStartDelay(600).start()

        val fadeInDesc = AnimationUtils.loadAnimation(context, R.anim.fade_in).apply {
            startOffset = 1000
        }
        descriptionView.startAnimation(fadeInDesc)
        descriptionView.animate().alpha(1f).setDuration(600).setStartDelay(1000).start()
    }
}
