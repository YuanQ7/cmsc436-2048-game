package com.example.finalproject

import android.content.Context
import android.util.Log

class Game {

    private var boardSize : Int
    private lateinit var board : Array<Array<Int>>

    var currTime = 0
    var bestTime = 0
        private set

    // 0 is the empty square. keeping track of its
    // index makes it easier to figure out what values we need to swap.
    // We need to update these indexes after each swap
    private var zeroRowIndex : Int = 0
    private var zeroColIndex : Int = 0

    companion object {
        private const val BEST_TIME_KEY = "BEST_TIME_KEY"
    }

    constructor(context: Context, boardSize : Int) {
        this.boardSize = boardSize
        newBoard(boardSize)
        Log.w("test", "BoardSize: $boardSize")

        val pref = context.getSharedPreferences("XYZ", Context.MODE_PRIVATE)
        bestTime = pref.getInt(BEST_TIME_KEY, 0)
    }

    // Creates a 2d array of shuffled numbers from 0 to 8 inclusive
    private fun newBoard( size : Int ) {
        val list = (0..<size*size).toList().shuffled()
        val idxZero = list.indexOf(0)
        zeroRowIndex = idxZero/size
        zeroColIndex = idxZero%size
        board = Array( size ) { i -> Array(size){ j -> list[size * i + j] } }

        // to print the board's values
        /*for(i in board.indices) {
            Log.w("board", board[i].contentToString())
        }*/

        // to check that the indexes for zero work
        /*Log.w("asd", zeroRowIndex.toString())
        Log.w("asd", zeroColIndex.toString())*/
    }

    // For testing
    fun logBoard() : Unit {
        for(i in board.indices) {
            Log.w("test", board[i].contentToString())
        }
    }

    fun getBoardSize() : Int {
        return boardSize
    }

    fun getValueAt(row: Int, col: Int) : Int {
        return board[row][col]
    }


    fun updateTime(time: String) {
        currTime = timeStrToInt(time)
    }
    private fun saveBestTime(context: Context) {
        if (currTime > bestTime) {
            val pref = context.getSharedPreferences("XYZ", Context.MODE_PRIVATE).edit()
            pref.putInt(BEST_TIME_KEY, currTime)
            pref.apply()

            bestTime = currTime
        }
    }

    private fun timeStrToInt(time: String) : Int {
        val arr = time.split(":")
        return arr[0].toInt() * 60 + arr[1].toInt()
    }

    // returns the best time as a string, to update the bestTimeTv in GameActivity.
    // Todo: need to call this when user wins, will update when that's all sorted out
    fun getBestTimeStr() : String {
        var hr = "${bestTime / 60}"
        var sec = "${bestTime % 60}"

        if (hr.length < 2) {
            hr += "0"
        }

        if (sec.length < 2) {
            sec = "0$sec"
        }

        return "$hr:$sec"
    }

    // Attempts to move a piece in the given direction to the empty spot
    // Returns true if the move was made, false if the move was not possible
    fun makeMove(direction: String) : Boolean {
        if (direction == "RIGHT" && zeroColIndex != 0) {
            board[zeroRowIndex][zeroColIndex] = board[zeroRowIndex][zeroColIndex - 1]
            board[zeroRowIndex][zeroColIndex - 1] = 0
            zeroColIndex--
        } else if (direction == "LEFT" && zeroColIndex != boardSize - 1) {
            board[zeroRowIndex][zeroColIndex] = board[zeroRowIndex][zeroColIndex + 1]
            board[zeroRowIndex][zeroColIndex + 1] = 0
            zeroColIndex++
        } else if (direction == "UP" && zeroRowIndex != boardSize - 1) {
            board[zeroRowIndex][zeroColIndex] = board[zeroRowIndex + 1][zeroColIndex]
            board[zeroRowIndex + 1][zeroColIndex] = 0
            zeroRowIndex++
        } else if (direction == "DOWN" && zeroRowIndex != 0) {
            board[zeroRowIndex][zeroColIndex] = board[zeroRowIndex - 1][zeroColIndex]
            board[zeroRowIndex - 1][zeroColIndex] = 0
            zeroRowIndex--
        } else {
            return false
        }
        return true
    }

    // Checks if the puzzle has been solved
    fun isComplete() : Boolean {
        var num = 1
        for (i in 0..<boardSize) {
            for ( j in 0..<boardSize) {
                // If at the bottom right corner, check that the value is 0
                if (i == boardSize - 1 && j == boardSize - 1) {
                    return board[i][j] == 0

                // Else check that the values of the puzzle pieces are increasing incrementally
                } else if (board[i][j] != num) {
                    return false
                }
                num++
            }
        }

        return true
    }

    fun userWon(context: Context) {
        saveBestTime(context)
    }

    fun resetGame() {
        newBoard(boardSize)
    }

    fun getRowIdx() : Int {
        return zeroRowIndex
    }

    fun getColIdx() : Int {
        return zeroColIndex
    }
}
