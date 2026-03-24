package com.example.dailybloom2.ui.habits

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailybloom2.R

class SensorFragment : Fragment(), SensorEventListener {
    
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var lastShakeTime = 0L
    private var shakeCount = 0
    private var onShakeDetected: (() -> Unit)? = null
    
    companion object {
        private const val SHAKE_THRESHOLD = 15f
        private const val SHAKE_TIMEOUT = 1000L
        private const val SHAKES_NEEDED = 2
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sensor, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSensor()
    }
    
    private fun setupSensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        if (accelerometer != null) {
            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    fun setOnShakeDetected(callback: () -> Unit) {
        onShakeDetected = callback
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            
            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            
            if (acceleration > SHAKE_THRESHOLD) {
                val currentTime = System.currentTimeMillis()
                
                if (currentTime - lastShakeTime > SHAKE_TIMEOUT) {
                    shakeCount = 1
                } else {
                    shakeCount++
                }
                
                lastShakeTime = currentTime
                
                if (shakeCount >= SHAKES_NEEDED) {
                    shakeCount = 0
                    onShakeDetected?.invoke()
                    showShakeToast()
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }
    
    private fun showShakeToast() {
        Toast.makeText(requireContext(), "📱 Shake detected! Quick complete activated", Toast.LENGTH_SHORT).show()
    }
    
    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }
    
    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
    }
}