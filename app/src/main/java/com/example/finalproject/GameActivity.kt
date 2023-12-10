package com.example.finalproject

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.GridLayout.TEXT_ALIGNMENT_CENTER
import android.widget.GridLayout.spec
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
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

    private lateinit var gridTvs : Array<Array<TextView?>>

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

        gridTvs = Array(model.getBoardSize()) {
            Array(model.getBoardSize()) {
                null
            }
        }

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

        for (i in 0 until gameGl.rowCount) {

            for (j in 0 until gameGl.rowCount) {
                val text = TextView(this)

                gridTvs[i][j] = text

                if(model.getValueAt(i, j) != 0) {
                    text.text = model.getValueAt(i, j).toString()
                } else {
                    text.text = ""
                    model.setZeroIndices(i, j)
                }

                // Customize button properties if needed
                text.layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.MATCH_PARENT
                    height = GridLayout.LayoutParams.MATCH_PARENT
                    text.setBackgroundColor(Color.LTGRAY)
                    text.textSize = 30f
                    text.gravity = Gravity.CENTER
                    text.id = i * gameSize + j
                }

                val params = GridLayout.LayoutParams().apply {
                    rowSpec = spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                    columnSpec = spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                    width = 0
                    height = 0
                    setMargins(4, 4, 4, 4)
                }

                gameGl.addView(text, params)
            }
        }
    }

    private fun initOnClickListeners() {
        startBtn.setOnClickListener {
            if (!model.gameStarted) {
                model.gameStarted = true
                timer.start()
            }
        }

        resetBtn.setOnClickListener {
            model.resetGame()
            resetGrid()
            timer.cancel()
            timer.onFinish()
            initTimer()
        }

        timeTv.doOnTextChanged { text, _, _, _ ->
            model.updateTime(text.toString())
        }
    }

    private fun resetGrid() {
        for (i in gridTvs.indices) {
            for (j in gridTvs.indices) {
                val tv = gridTvs[i][j]!!

                tv.text = if (model.getValueAt(i, j) != 0) {
                    "${model.getValueAt(i, j)}"
                } else {
                    ""
                }
            }
        }
    }

    private fun initTimer() {
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
        if( event != null && model.gameStarted)
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
                val horizontalDist: Float = e2.x - e1.x
                val verticalDist: Float = e2.y - e1.y
                val direction: String = if (abs(horizontalDist) > abs(verticalDist)) {
                    if (horizontalDist > 0) "RIGHT" else "LEFT"
                } else {
                    if (verticalDist > 0) "DOWN" else "UP"
                }
                Log.w("test", "Fling direction: $direction")

                var oldRowIdx = model.getRowIdx()
                var oldColIdx = model.getColIdx()
                if (model.makeMove(direction)) {
                    Log.w("test", "MOVE SUCCESS")
                    var textSwap: TextView =
                        gameGl.findViewById(oldRowIdx * gameSize + oldColIdx)
                    textSwap.text = model.getValueAt(oldRowIdx, oldColIdx).toString()
                    var textZero: TextView =
                        gameGl.findViewById(model.getRowIdx() * gameSize + model.getColIdx())
                    textZero.text = ""

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
