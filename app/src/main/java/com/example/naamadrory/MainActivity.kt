package com.example.naamadrory

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.button.MaterialButton
import com.example.naamadrory.logic.GameManager
import com.example.naamadrory.utilities.Constants
import com.example.naamadrory.utilities.SignalManager

class MainActivity : AppCompatActivity() {

    private lateinit var main_BTN_left: MaterialButton
    private lateinit var main_BTN_right: MaterialButton
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_IMG_matrix: GridLayout
    private lateinit var main_IMG_stopsMatrix: Array<Array<AppCompatImageView>>
    private lateinit var gameManager: GameManager
    private val handler: Handler = Handler(Looper.getMainLooper())

    private val gameTickRunnable = object : Runnable {
        override fun run() {
            val currentWrong = gameManager.wrongAnswers
            gameManager.gameMove()
            refreshMatrixUI()
            refreshUI()

            // If crashed - vibrate and message
            if (gameManager.wrongAnswers > currentWrong) {
                SignalManager.getInstance().vibrate()
                SignalManager.getInstance().toast("Crash! 💥")
            }

            // If gameOver - send a message
            if (gameManager.isGameOver) {
                changeActivity("Game Over!!")
            } else {
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()

        val colsNum = 3
        val rowsNum = 5

        gameManager = GameManager(rowsNum = rowsNum, colsNum = colsNum, lifeCount = 3)
        main_IMG_matrix.removeAllViews()

        initMatrix()
        initViews()

        gameManager.gameMove()
        refreshMatrixUI()

        handler.postDelayed(gameTickRunnable, 1000)
    }

    // Init the matrix
    private fun initMatrix() {
        val rowsNum = gameManager.getMatrix().size
        val colsNum = gameManager.getMatrix()[0].size

        main_IMG_stopsMatrix = Array(rowsNum) { row ->
            Array(colsNum) { col ->
                val img = AppCompatImageView(this)
                img.layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(col, 1f)
                    rowSpec = GridLayout.spec(row, 1f)
                    setMargins(4, 4, 4, 4)
                }
                img.scaleType = ImageView.ScaleType.FIT_CENTER
                img.adjustViewBounds = true
                img.setImageResource(0)
                main_IMG_matrix.addView(img)
                img
            }
        }
    }

    // Update matrix display - visible or invisible (car/stop/empty)
    private fun refreshMatrixUI() {
        val theMatrix = gameManager.getMatrix()

        for (row in theMatrix.indices) {
            for (col in theMatrix[0].indices) {
                val images = main_IMG_stopsMatrix[row][col]
                when (theMatrix[row][col]) {
                    0 -> {
                        images.setImageResource(0)
                        images.visibility = View.INVISIBLE
                    }
                    1 -> {
                        images.setImageResource(R.drawable.stop)
                        images.visibility = View.VISIBLE
                    }
                    2 -> {
                        images.setImageResource(R.drawable.car)
                        images.visibility = View.VISIBLE
                    }
                    3 -> { // Crash - show the car
                        images.setImageResource(R.drawable.car)
                        images.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    // Update hearts
    private fun refreshUI() {
        for (i in main_IMG_hearts.indices) {
            main_IMG_hearts[i].visibility = if (i < 3 - gameManager.wrongAnswers) View.VISIBLE else View.INVISIBLE
        }
    }

    // Sets up the right or left button
    private fun initViews() {
        main_BTN_right.setOnClickListener {
            gameManager.mRight()
            refreshMatrixUI()
        }
        main_BTN_left.setOnClickListener {
            gameManager.mLeft()
            refreshMatrixUI()
        }
        refreshMatrixUI()
    }

    // Move to another screen and send gameOver if necessary
    private fun changeActivity(message: String) {
        val intent = Intent(this, ScoreActivity::class.java)
        intent.putExtra(Constants.BundleKeys.MESSAGE_KEY, message)
        startActivity(intent)
        finish()
    }

    // Connects buttons, matrix grid, and hearts to their XML components
    private fun findViews() {
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_IMG_matrix = findViewById(R.id.main_IMG_matrix)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
    }

    // Stops the game loop
    override fun onDestroy() {
        handler.removeCallbacks(gameTickRunnable)
        super.onDestroy()
    }
}
