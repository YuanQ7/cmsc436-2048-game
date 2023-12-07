package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.GridLayout
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import kotlin.properties.Delegates
import kotlin.system.exitProcess

class GameActivity : AppCompatActivity() {

    private lateinit var model: Game
    private lateinit var gameGl : GridLayout
    private lateinit var detector: GestureDetector
    private var statusBarHeight : Int = 0
    private var gameSize : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        model = MainActivity.getGameModel()
        gameGl = findViewById(R.id.grid_board)
        gameSize = model.getBoardSize()

        initBoard()

        val statusBarId : Int = resources.getIdentifier( "status_bar_height", "dimen", "android" )
        statusBarHeight= resources.getDimensionPixelSize( statusBarId )

        val handler = TouchHandler()
        detector = GestureDetector(this, handler)
        detector.setOnDoubleTapListener(handler)

        // For Testing
        model.logBoard()

        // TODO: timer
    }

    private fun initBoard() {
        gameGl.rowCount = model.getBoardSize()
        gameGl.columnCount = model.getBoardSize()

        // TODO: initialize grid with puzzle pieces

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if( event != null )
            detector.onTouchEvent( event )
        return super.onTouchEvent(event)
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
            }

            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }
}
