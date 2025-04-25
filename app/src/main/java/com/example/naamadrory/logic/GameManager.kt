package com.example.naamadrory.logic

class GameManager(
    private val rowsNum: Int = 4,
    private val colsNum: Int = 3,
    private val lifeCount: Int = 3,
) {

    private val theMatrix = Array(rowsNum) { IntArray(colsNum) { 0 } }

    private var firstMove = true

    var carCol: Int = colsNum / 2
        private set

    var wrongAnswers: Int = 0
        private set

    val isGameOver: Boolean
        get() = wrongAnswers >= lifeCount

    fun mRight() {
        if (carCol < colsNum - 1) carCol++
    }

    fun mLeft() {
        if (carCol > 0) carCol--
    }

    fun gameMove() {
        if (!firstMove) {
            mDown()
            checkIsStop()
        }
        firstMove = false
    }

    fun getMatrix(): Array<IntArray> = theMatrix

    private fun checkIsStop() {
        if (theMatrix[rowsNum - 1][carCol] == 1) {
            wrongAnswers++
            theMatrix[rowsNum - 1][carCol] = 3
        }
    }

    private fun mDown() {
        for (row in rowsNum - 1 downTo 1) {
            for (col in 0 until colsNum) {
                theMatrix[row][col] = theMatrix[row - 1][col]
            }
        }

        for (col in 0 until colsNum) {
            theMatrix[0][col] = 0
        }

        val randomCol = (0 until colsNum).random()
        theMatrix[0][randomCol] = 1
    }
}
