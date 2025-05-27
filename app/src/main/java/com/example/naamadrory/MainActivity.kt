package com.example.naamadrory

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.example.naamadrory.interfaces.TiltCallback
import com.google.android.material.button.MaterialButton
import com.example.naamadrory.logic.GameManager
import com.example.naamadrory.utilities.BackgroundMusicPlayer
import com.example.naamadrory.utilities.Constants
import com.example.naamadrory.utilities.SignalManager
import com.example.naamadrory.utilities.SingleSoundPlayer
import com.example.naamadrory.utilities.TiltDetector
import com.google.android.material.textview.MaterialTextView
import kotlin.toString

class MainActivity : AppCompatActivity() {

    private lateinit var main_LBL_gameOver: TextView
    private lateinit var main_LBL_distance: TextView
    private lateinit var main_BTN_left: MaterialButton
    private lateinit var main_BTN_right: MaterialButton
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_IMG_matrix: GridLayout
    private lateinit var main_IMG_stopsMatrix: Array<Array<AppCompatImageView>>
    private lateinit var main_LBL_score: TextView
    private lateinit var sensors_LBL_tiltX: MaterialTextView
    private lateinit var sensors_LBL_tiltY: MaterialTextView
    private lateinit var tiltDetector: TiltDetector
    private lateinit var gameManager: GameManager
    private var isFast: Boolean = false
    private var useSensor: Boolean = false
    private val handler: Handler = Handler(Looper.getMainLooper())

    private val gameTickRunnable = object : Runnable {
        override fun run() {
            val currentWrong = gameManager.wrongAnswers
            gameManager.gameMove()

            if (isFast) {
                gameManager.increaseDistanceBy(2)
            } else {
                gameManager.increaseDistanceBy(1)
            }

            main_LBL_score.text = "Score: ${gameManager.score}"
            main_LBL_distance.text = "Distance: ${gameManager.distance}"

            if (gameManager.collectedCoin) {
                SignalManager.getInstance().toast("+10")
                gameManager.collectedCoin = false
            }

            refreshMatrixUI()
            refreshUI()

            // If crashed - vibrate and message
            if (gameManager.wrongAnswers > currentWrong) {
                SignalManager.getInstance().vibrate()
                SignalManager.getInstance().toast("Crash! 💥")
                SingleSoundPlayer(this@MainActivity).playSound(R.raw.boom)
            }

            // If gameOver - send a message
            if (gameManager.isGameOver) {
                changeActivity("Game Over!!")
            } else {
                val delayMillis = if (isFast) 500L else 1000L
                handler.postDelayed(this, delayMillis)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViews()

        isFast = intent.getBooleanExtra(Constants.BundleKeys.IS_FAST_KEY, false)
        useSensor = intent.getBooleanExtra(Constants.BundleKeys.USE_SENSOR_KEY, false)

        if (useSensor) {
            startSensorControl()
            main_BTN_left.visibility = View.GONE
            main_BTN_right.visibility = View.GONE
        } else {
            startButtonControl()
            sensors_LBL_tiltX.visibility = View.GONE
            sensors_LBL_tiltY.visibility = View.GONE
        }


        val colsNum = 5
        val rowsNum = 6

        gameManager = GameManager(rowsNum = rowsNum, colsNum = colsNum, lifeCount = 3)
        main_IMG_matrix.removeAllViews()

        initMatrix()
        refreshMatrixUI()

        val delayMillis = if (isFast) 500L else 1000L
        handler.postDelayed(gameTickRunnable, delayMillis)
    }

    //Sensor Mode
    private fun startSensorControl()
    {
        tiltDetector = TiltDetector(this, object : TiltCallback {
            override fun tiltX(x: Float) {
                if (x > 2) {
                    gameManager.mLeft()
                } else if (x < -2) {
                    gameManager.mRight()
                }
                refreshMatrixUI()
            }

            override fun tiltY(y: Float) {
                isFast = y < -3
            }
        })
        tiltDetector.start()
    }

    //Button mode
    private fun startButtonControl()
    {
        main_BTN_right.setOnClickListener {
            gameManager.mRight()
            gameManager.userMoved = true
            gameManager.gameMove()
            refreshMatrixUI()
            refreshUI()
        }

        main_BTN_left.setOnClickListener {
            gameManager.mLeft()
            gameManager.userMoved = true
            gameManager.gameMove()
            refreshMatrixUI()
            refreshUI()
        }
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
                    4 -> { //fuel
                        images.setImageResource(R.drawable.fuel)
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

    // Move to another screen and send gameOver if necessary
    private fun changeActivity(message: String) {
        val gameOverMessage = "$message\n\nScore: ${gameManager.score}\nDistance: ${gameManager.distance}m"
        main_LBL_gameOver.text = gameOverMessage
        main_LBL_gameOver.visibility = View.VISIBLE

        handler.removeCallbacks(gameTickRunnable)

        main_BTN_left.visibility = View.GONE
        main_BTN_right.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, ScoreActivity::class.java)
            intent.putExtra("EXTRA_SCORE", gameManager.score)
            intent.putExtra("EXTRA_DISTANCE", gameManager.distance)
            startActivity(intent)
            finish()
            gameManager.resetScore()
        }, 1500)
    }



    // Connects buttons, matrix grid, and hearts to their XML components
    private fun findViews() {
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_LBL_distance = findViewById(R.id.main_LBL_distance)
        main_LBL_gameOver = findViewById(R.id.main_LBL_gameOver)
        main_LBL_score = findViewById(R.id.main_LBL_score)
        sensors_LBL_tiltX = findViewById(R.id.sensors_LBL_tiltX)
        sensors_LBL_tiltY = findViewById(R.id.sensors_LBL_tiltY)
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
        BackgroundMusicPlayer.getInstance().stopMusic()
    }

    override fun onResume() {
        super.onResume()
        if (useSensor) {
            tiltDetector.start()
        }
        BackgroundMusicPlayer.getInstance().playMusic()
    }


    override fun onPause() {
        super.onPause()
        if (useSensor) {
            tiltDetector.stop()
        }
        BackgroundMusicPlayer.getInstance().pauseMusic()
    }

}
