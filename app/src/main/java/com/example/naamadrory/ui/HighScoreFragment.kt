package com.example.naamadrory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.naamadrory.R
import com.example.naamadrory.adapters.HighScoreAdapter
import com.example.naamadrory.interfaces.HighScoreCallback
import com.example.naamadrory.utilities.HighScoreManager

class HighScoreFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    var highScoreItemClicked: HighScoreCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_highscore, container, false)
        findViews(v)
        initViews()
        return v
    }

    private fun findViews(v: View) {
        recyclerView = v.findViewById(R.id.highScore_LST_scores)
    }

    private fun initViews() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val scores = HighScoreManager.getHighScores(requireContext())

        val adapter = HighScoreAdapter(scores) { score ->
            val parts = score.location.split(",")
            val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
            val lon = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
            itemClicked(lat, lon)
        }

        recyclerView.adapter = adapter
    }

    private fun itemClicked(lat: Double, lon: Double) {
        highScoreItemClicked?.highScoreItemClicked(lat, lon)
    }
}
