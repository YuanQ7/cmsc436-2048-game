package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    companion object {
        private lateinit var model: Game

        fun getGameModel() : Game {
            return model
        }
    }

    private lateinit var sizeRg : RadioGroup
    private lateinit var startBtn : Button
    private lateinit var bestTimeTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        sizeRg = findViewById(R.id.btns)
        startBtn = findViewById(R.id.btn_start)
        bestTimeTv = findViewById(R.id.time)

        startBtn.setOnClickListener {
            if (sizeRg.checkedRadioButtonId != -1) {
                val boardSize = if (sizeRg.checkedRadioButtonId == R.id.btn_3) {
                    // 3x3
                    3
                } else if (sizeRg.checkedRadioButtonId == R.id.btn_4) {
                    // 4x4
                    4
                } else {
                    // 5x5
                    5
                }
                model = Game(boardSize)
                // start GameActivity
                startActivity(Intent(this, GameActivity::class.java))
            }
        }
    }


}
