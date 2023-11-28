package com.example.finalproject

import android.util.Log

class Game {

    private var boardSize : Int
    private lateinit var board : Array<Array<Int>>

    // 0 is the empty square. keeping track of its
    // index makes it easier to figure out what values we need to swap.
    // We need to update these indexes after each swap
    private var zeroRowIndex : Int = 0
    private var zeroColIndex : Int = 0

    constructor( boardSize : Int ) {
        this.boardSize = boardSize
        newBoard(boardSize)
    }

    // Creates a 2d array of shuffled numbers from 0 to 8 inclusive
    fun newBoard( size : Int ) {
        var list = (0..size*size-1).toList().shuffled()
        var idxZero = list.indexOf(0)
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
}