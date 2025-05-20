package com.example.naamadrory.utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.naamadrory.interfaces.TiltCallback
import kotlin.math.abs

class TiltDetector(context: Context, private val tiltCallback: TiltCallback?) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private lateinit var sensorEventListener: SensorEventListener

    var tiltCounterX: Int = 0
        private set

    var tiltCounterY: Int = 0
        private set

    var lastX: Float = 0f
        private set

    var lastY: Float = 0f
        private set

    private var timestamp: Long = 0L

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                calculateTilt(x, y)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //not used
            }
        }
    }

    private fun calculateTilt(x: Float, y: Float) {
        if (System.currentTimeMillis() - timestamp >= 500) {
            timestamp = System.currentTimeMillis()

            lastX = x
            lastY = y

            if (abs(x) >= 3.0) {
                tiltCounterX++
                tiltCallback?.tiltX()
            }

            if (abs(y) >= 3.0) {
                tiltCounterY++
                tiltCallback?.tiltY()
            }
        }
    }

    fun start() {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(sensorEventListener, sensor)
    }
}
