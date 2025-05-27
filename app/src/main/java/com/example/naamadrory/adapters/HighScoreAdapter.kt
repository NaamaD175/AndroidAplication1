package com.example.naamadrory.adapters

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.naamadrory.R
import com.example.naamadrory.data.HighScore
import java.util.Locale

class HighScoreAdapter(
    private val scores: List<HighScore>,
    private val callback: ((HighScore) -> Unit)? = null
) : RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder>() {

    class HighScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.item_LBL_name)
        val scoreText: TextView = view.findViewById(R.id.item_LBL_score)
        val distanceText: TextView = view.findViewById(R.id.item_LBL_distance)
        val locationText: TextView = view.findViewById(R.id.item_LBL_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.highscore, parent, false)
        return HighScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: HighScoreViewHolder, position: Int) {
        val highScore = scores[position]

        holder.nameText.text = highScore.name
        holder.scoreText.text = highScore.score.toString()
        holder.distanceText.text = "${highScore.distance}m"

        val parts = highScore.location.split(",")
        val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
        val lon = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0

        val geocoder = Geocoder(holder.itemView.context, Locale.ENGLISH)
        val city = try {
            geocoder.getFromLocation(lat, lon, 1)?.getOrNull(0)?.locality ?: highScore.location
        } catch (e: Exception) {
            highScore.location
        }

        holder.locationText.text = city

        holder.itemView.setOnClickListener {
            callback?.invoke(highScore)
        }
    }

    override fun getItemCount(): Int = scores.size
}
