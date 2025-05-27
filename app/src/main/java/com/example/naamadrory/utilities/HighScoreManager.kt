package com.example.naamadrory.utilities

import com.example.naamadrory.data.HighScore
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object HighScoreManager {

    private const val PREFS_NAME = "high_scores_prefs"
    private const val KEY_SCORES = "high_scores_list"

    fun saveHighScore(context: Context, newScore: HighScore) {
        val scores = getHighScores(context).toMutableList()
        scores.add(newScore)

        val sorted = if (scores.size <= 10) {
            scores.sortedByDescending { it.score }
        } else {
            scores.sortedByDescending { it.score }.take(10)
        }

        val json = Gson().toJson(sorted)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_SCORES, json)
            }
    }

    fun getHighScores(context: Context): List<HighScore> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SCORES, null) ?: return emptyList()
        val type = object : TypeToken<List<HighScore>>() {}.type
        return Gson().fromJson(json, type)
    }
}
