package com.arkadii.glagoli.record

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.arkadii.glagoli.extensions.toPx
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs

class MoveFloatingActionButton: FloatingActionButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):
            super(context, attrs, defStyleAttr)

    private val parentLocation: IntArray = IntArray(2)
    private val startLocation: IntArray = IntArray(2)
    private var move: ButtonMove = ButtonMove.DEFAULT
    @Volatile
    private var isButtonDown = false
    var defaultBorder = 0.5
    var leftBorder: Int = 0
        set(value){
            field = value.toPx()
        }
    var upBorder: Int = 0
        set(value){
            field = value.toPx()
        }
    var upListener: ((MoveFloatingActionButton, MotionEvent) -> Unit)? = null
    var leftListener: ((MoveFloatingActionButton, MotionEvent) -> Unit)? = null
    var actionDownListener: ((MoveFloatingActionButton, MotionEvent) -> Unit)? = null
    var actionUpListener: ((MoveFloatingActionButton, MotionEvent) -> Unit)? = null

    private val moveListener: (View, MotionEvent) -> Boolean = { _, event ->
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.i(TAG, "Button ACTION_DOWN")
                eventDown(event)
            }
            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "Button ACTION_UP")
                if(isButtonDown) eventUp(event)
            }
            MotionEvent.ACTION_MOVE -> {
                Log.i(TAG, "Button ACTION_MOVE")
                if(isButtonDown) eventMove(event)
            }
        }
        false
    }

    init {
        super.setOnTouchListener(moveListener)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(TAG, "onLayout")
        val uncheckedParent = parent
        if(uncheckedParent is View) uncheckedParent.getLocationOnScreen(parentLocation)
        else error("Parent is not View")
        getLocationOnScreen(startLocation)
    }

    private fun eventDown(event: MotionEvent) {
        isButtonDown = true
        val listener = actionDownListener
        if(listener != null) listener(this, event)
    }

    private fun eventUp(event: MotionEvent) {
        isButtonDown = false
        val listener = actionUpListener
        if(listener != null) {
            listener(this, event)
        }
        move = ButtonMove.DEFAULT
    }

    private fun eventMove(event: MotionEvent) {
        when(move) {
            ButtonMove.DEFAULT -> defaultMove(event)
            ButtonMove.LEFT -> toLeftMove(event)
            ButtonMove.UP -> toUpMove(event)
        }
    }

    private fun toLeftMove(event: MotionEvent) {
        Log.d(TAG, "LeftMove rawX = ${event.rawX} " +
                "leftBorder = $leftBorder")
        if(getShiftX(event.rawX) + (this.width/2) < defaultBorder) {
            move = ButtonMove.DEFAULT
        } else if(event.rawX > leftBorder) {
            animate(computeCentreX(event.rawX), computeY(startLocation[1].toFloat()))
        } else if(event.rawX < leftBorder) {
            val listener = leftListener
            if(listener != null) listener(this, event)
        }
    }
    private fun toUpMove(event: MotionEvent) {
        Log.d(TAG, "UpMove rawY = ${event.rawY} upBorder = ${startLocation[1] - upBorder}")
        if(getShiftY(event.rawY) + (this.height/2) < defaultBorder) {
            move = ButtonMove.DEFAULT
        } else if(event.rawY > (startLocation[1] - upBorder)) {
            animate(computeX(startLocation[0].toFloat()), computeCentreY(event.rawY))
        } else if(event.rawY < (startLocation[1] - upBorder)) {
            val listener = upListener
            if(listener != null) listener(this, event)
        }
    }

    private fun defaultMove(event: MotionEvent) {
        Log.d(TAG, "DefaultMove")
        val shiftX = getShiftX(event.rawX)
        Log.d(TAG, "ShiftX = $shiftX")
        val shiftY = getShiftY(event.rawY)
        Log.d(TAG, "ShiftY = $shiftY")
        when {
           shiftX > shiftY && shiftX > defaultBorder -> {
               Log.d(TAG, "Left button move ${event.rawX}")
               move = ButtonMove.LEFT
               if(event.rawX > leftBorder) animate(
                   computeCentreX(event.rawX), computeY(startLocation[1].toFloat()))
           }
            shiftY > shiftX && shiftY > defaultBorder -> {
                Log.d(TAG, "Up button move ${event.rawY}")
                move = ButtonMove.UP
                if(event.rawY > upBorder) animate(
                    computeX(startLocation[0].toFloat()), computeCentreY(event.rawY))
            }
        }
    }

    fun returnToStartLocation() {
        Log.d(TAG, "Return to start location")
        Log.d(TAG, "StartLocation x == ${startLocation[0].toFloat()} " +
                "StartLocation y == ${startLocation[1].toFloat()}")
        Log.d(TAG, "ParentLocation x == ${parentLocation[0]} " +
                "ParentLocation y == ${parentLocation[1]}" )
        isButtonDown = false
        animate(computeX(startLocation[0].toFloat()), computeY(startLocation[1].toFloat()))
    }

    private fun animate(x: Float, y: Float, duration: Long = 0) {
        animate().let { animator ->
            animator.x(x)
            animator.y(y)
            animator.duration = duration
            animator.start()
        }
        Log.i(
            TAG, "StartButton animation move " +
                "to x = $x,  y = $y, duration = $duration width = $width height = $height")
    }

    private fun computeX(x: Float): Float = abs(x - parentLocation[0])
    private fun computeY(y: Float): Float = abs(y - parentLocation[1])
    private fun computeCentreX(x: Float) = abs(x - parentLocation[0] - (this.width/2))
    private fun computeCentreY(y: Float) = abs(y - parentLocation[1] - (this.height/2))
    private fun getShiftX(rawX: Float) = startLocation[0] - rawX
    private fun getShiftY(rawY: Float) = startLocation[1] - rawY


    companion object {
        const val TAG = "MoveFloatingActionButton"
    }

}