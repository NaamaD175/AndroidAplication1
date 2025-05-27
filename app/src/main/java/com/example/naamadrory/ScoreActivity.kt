package com.example.naamadrory

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.naamadrory.data.HighScore
import com.example.naamadrory.ui.HighScoreFragment
import com.example.naamadrory.ui.MapFragment
import com.example.naamadrory.utilities.HighScoreManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.naamadrory.interfaces.HighScoreCallback
import com.google.android.gms.location.LocationServices

class ScoreActivity : AppCompatActivity() {

    private lateinit var main_FRAME_list: FrameLayout
    private lateinit var main_FRAME_map: FrameLayout
    private lateinit var mapFragment: MapFragment
    private lateinit var highScoreFragment: HighScoreFragment

    private var tempName: String? = null
    private var tempScore: Int = -1
    private var tempDistance: Int = -1
    private var waitingForPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_highscore)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fromHome = intent.getBooleanExtra("EXTRA_FROM_HOME", false)
        val score = intent.getIntExtra("EXTRA_SCORE", -1)
        val distance = intent.getIntExtra("EXTRA_DISTANCE", -1)
        val highScores = HighScoreManager.getHighScores(this)
        val lowestScore = highScores.minByOrNull { it.score }?.score ?: 0
        val shouldAdd = score != -1 && (highScores.size < 10 || score > lowestScore)

        if (!fromHome && shouldAdd && !waitingForPermission) {
            val input = EditText(this)
            input.hint = "Enter your name"

            AlertDialog.Builder(this)
                .setTitle("New High Score!")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val name = input.text.toString().ifBlank { "Unknown" }
                    tempName = name
                    tempScore = score
                    tempDistance = distance

                    if (ActivityCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        waitingForPermission = true
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            1001
                        )
                        return@setPositiveButton
                    }

                    saveHighScore(name, score, distance)
                }
                .setCancelable(false)
                .show()
        } else {
            findViews()
            initViews()
        }

        val btnBack = findViewById<Button>(R.id.highScores_BTN_back)
        btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveHighScore(name: String, score: Int, distance: Int) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) { fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val lat = location?.latitude ?: 0.0
                val lon = location?.longitude ?: 0.0
                val locationStr = "$lat,$lon"

                val highScore = HighScore(name, score, distance, locationStr)
                HighScoreManager.saveHighScore(this, highScore)

                findViews()
                initViews()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            tempName?.let { name ->
                saveHighScore(name, tempScore, tempDistance)
                waitingForPermission = false
            }
        } else {
            Toast.makeText(this, "Permission denied - location not saved", Toast.LENGTH_SHORT).show()
            findViews()
            initViews()
        }
    }

    private fun findViews() {
        main_FRAME_list = findViewById(R.id.main_FRAME_list)
        main_FRAME_map = findViewById(R.id.main_FRAME_map)
    }

    private fun initViews() {
        mapFragment = MapFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_FRAME_map, mapFragment)
            .commit()

        highScoreFragment = HighScoreFragment()
        highScoreFragment.highScoreItemClicked = object : HighScoreCallback {
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                mapFragment.zoom(lat, lon)
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_FRAME_list, highScoreFragment)
            .commit()
    }
}
