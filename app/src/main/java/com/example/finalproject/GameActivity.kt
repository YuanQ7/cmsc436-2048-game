package com.example.finalproject

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Timer
import kotlin.math.abs


class GameActivity : AppCompatActivity() {

    private lateinit var model: Game
    private lateinit var gameGl : GridLayout
    private lateinit var startBtn : Button
    private lateinit var resetBtn : Button
    private lateinit var timeTv : TextView
    private lateinit var bestTimeTv : TextView

    private lateinit var detector: GestureDetector
    private var statusBarHeight : Int = 0
    private var gameSize : Int = 0

    private lateinit var timer : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        model = MainActivity.getGameModel()
        gameGl = findViewById(R.id.grid_board)
        startBtn = findViewById(R.id.btn_start)
        resetBtn = findViewById(R.id.btn_reset)
        timeTv = findViewById(R.id.time_tv)
        bestTimeTv = findViewById(R.id.best_time_tv)

        bestTimeTv.text = model.getBestTimeStr()

        gameSize = model.getBoardSize()

        val statusBarId : Int = resources.getIdentifier( "status_bar_height", "dimen", "android" )
        statusBarHeight= resources.getDimensionPixelSize( statusBarId )

        val handler = TouchHandler()
        detector = GestureDetector(this, handler)
        detector.setOnDoubleTapListener(handler)

        // For Testing
        model.logBoard()

        initBoard()
        initTimer()
        initOnClickListeners()
    }

    private fun initBoard() {
        gameGl.rowCount = model.getBoardSize()
        gameGl.columnCount = model.getBoardSize()

        // TODO: initialize grid with puzzle pieces
        for (i in 0 until gameGl.rowCount) {

            for (j in 0 until gameGl.rowCount) {
                val button = Button(this)
                button.text = "${model.getValueAt(i, j)}"

                // Customize button properties if needed
                button.layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    setGravity(Gravity.CENTER_HORIZONTAL)
                    setMargins(8, 8, 8, 8)
                }

                gameGl.addView(button)
            }
        }
    }

    private fun initOnClickListeners() {
        startBtn.setOnClickListener {
            timer.start()
        }

        resetBtn.setOnClickListener {
            // there may be a better way to reset timer
            model.resetGame()
            timer.cancel()
            timer.onFinish()
            initTimer()
        }

        timeTv.doOnTextChanged { text, _, _, _ ->
//            Log.d("TESTING", "text changed $text")
            model.updateTime(text.toString())
        }
    }

    fun initTimer() {
        val timerDurationMillis: Long = 6000000000

        timer = object : CountDownTimer(timerDurationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the timer TextView with the remaining time
                val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())
                val formattedTime: String = sdf.format(timerDurationMillis - millisUntilFinished).trim()
                timeTv.text = "$formattedTime"
            }

            override fun onFinish() {
                // Timer finished, perform actions if needed
                timeTv.text = "00:00"
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if( event != null )
            detector.onTouchEvent( event )
        return super.onTouchEvent(event)
    }

    private fun userWon() {
        model.userWon(this)
    }

    inner class TouchHandler : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return super.onDown(e)
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 != null) {
                val horizontalDist : Float = e2.x - e1.x
                val verticalDist : Float = e2.y - e1.y
                val direction : String = if (abs(horizontalDist) > abs(verticalDist)) {
                    if (horizontalDist > 0) "RIGHT" else "LEFT"
                } else {
                    if (verticalDist > 0) "DOWN" else "UP"
                }
                Log.w("test", "Fling direction: $direction")

                if (model.makeMove(direction)) {
                    Log.w("test", "MOVE SUCCESS")
                    // TODO: update view with moved piece
                } else {
                    Log.w("test", "MOVE FAIL")
                }

                // For testing
                model.logBoard()
            }

            if (model.isComplete()) {
                // TODO: Puzzle complete, display result and possibly update best time
                Log.w("test", "GAME WON")
                userWon()
                bestTimeTv.text = model.getBestTimeStr()
            }

            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }
}
