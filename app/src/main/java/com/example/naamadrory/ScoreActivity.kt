package com.example.naamadrory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textview.MaterialTextView
import com.example.naamadrory.utilities.Constants
import kotlin.apply

class ScoreActivity : AppCompatActivity()
{
    private lateinit var score_status: MaterialTextView
    private lateinit var high_scores_list: MaterialTextView

    private fun saveScore(score: Int) {
        val prefs = getSharedPreferences("scores", MODE_PRIVATE)
        val editor = prefs.edit()

        // Retrieve previous scores or start empty
        val scoresList = prefs.getStringSet("high_scores", mutableSetOf())!!.toMutableSet()

        //Add the current score
        scoresList.add(score.toString())

        //Save the update list
        editor.putStringSet("high_scores", scoresList)
        editor.apply()
    }

    private fun showHighScores() {
        val prefs = getSharedPreferences("scores", MODE_PRIVATE)
        val scores = prefs.getStringSet("high_scores", mutableSetOf())!!
            .map { it.toInt() }
            .sortedDescending()
            .take(5)

        val result = scores.mapIndexed { index, value ->
            "${index + 1}. $value"
        }.joinToString("\n")

        high_scores_list.text = "High Scores:\n$result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main))
        {
            v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        initViews()
    }

    private fun findViews()
    {
        score_status = findViewById(R.id.score_status)
        high_scores_list = findViewById(R.id.high_scores_list)
    }

    private fun initViews()
    {
        val bundle: Bundle? = intent.extras
        val message = bundle?.getString(Constants.BundleKeys.MESSAGE_KEY, "Unknown status")
        val score = bundle?.getInt("FINAL_SCORE", 0) ?: 0

        saveScore(score)

        val displayText = "$message\nScore: $score 🪙"
        score_status.text = displayText
        showHighScores()
    }
}




