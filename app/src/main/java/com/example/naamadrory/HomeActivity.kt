package com.example.naamadrory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.naamadrory.utilities.Constants

class HomeActivity : AppCompatActivity() {

    private lateinit var home_SWITCH_control: Switch
    private lateinit var home_SWITCH_speed: Switch
    private lateinit var home_BTN_start: Button
    private lateinit var home_BTN_highScores: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        home_SWITCH_control = findViewById(R.id.home_SWITCH_control)
        home_SWITCH_speed = findViewById(R.id.home_SWITCH_speed)
        home_BTN_start = findViewById(R.id.home_BTN_start)
        home_BTN_highScores = findViewById(R.id.home_BTN_highScores)

        home_BTN_highScores.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            intent.putExtra("EXTRA_FROM_HOME", true)
            startActivity(intent)
        }

        home_BTN_start.setOnClickListener {
            val useSensor = home_SWITCH_control.isChecked
            val isFast = home_SWITCH_speed.isChecked

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(Constants.BundleKeys.USE_SENSOR_KEY, useSensor)
            intent.putExtra(Constants.BundleKeys.IS_FAST_KEY, isFast)
            startActivity(intent)
            finish()
        }
    }
}
