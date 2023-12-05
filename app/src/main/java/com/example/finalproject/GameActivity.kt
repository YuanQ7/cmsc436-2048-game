package com.example.finalproject

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var model: Game
    private lateinit var gameGl : GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        model = MainActivity.getGameModel()
        gameGl = findViewById(R.id.grid_board)

        initBoard()
    }

    private fun initBoard() {
        gameGl.rowCount = model.getBoardSize()
        gameGl.columnCount = model.getBoardSize()

        // TODO: initialize grid of buttons

    }

}