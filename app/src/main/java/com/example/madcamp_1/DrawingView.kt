package com.example.madcamp_1

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import kotlin.properties.Delegates

class ColoredPath(val path: Path, var color: Int, var strokeWidth: Float)

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private val paint = Paint()
    private var paths: MutableList<Path> = mutableListOf()
    private var coloredPaths: MutableList<ColoredPath> = mutableListOf()
    private var undonePaths: MutableList<ColoredPath> = mutableListOf()

    private var isEraserMode = false
    private var currentColor = Color.BLACK
    private var currentStrokeWidth = 10f

    private var latestColor = Color.BLACK
    private var eraserCheck = 0

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        canvas?.drawColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        for (coloredPath in coloredPaths) {
            paint.color = coloredPath.color
            paint.strokeWidth = coloredPath.strokeWidth
            canvas.drawPath(coloredPath.path, paint)
        }
    }

    fun setColor(color: Int) {
        currentColor = color
    }

    fun getColor() : Int {
        return currentColor
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val newPath = Path()
                newPath.moveTo(x, y)
                coloredPaths.add(ColoredPath(newPath, currentColor, currentStrokeWidth))
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                coloredPaths.lastOrNull()?.path?.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
            }
        }

        invalidate()
        return true
    }
    fun setStrokeWidth(strokeWidth: Float) {
        currentStrokeWidth = strokeWidth
    }

    fun clearCanvas() {
        paths.clear()
        canvas?.drawColor(Color.WHITE)
        invalidate()
    }

    fun setEraserMode(eraser:Boolean){
        isEraserMode = eraser
        if(isEraserMode==true){
            if(eraserCheck==0) latestColor = this.getColor()
            eraserCheck++
            this.setColor(Color.WHITE)
        }else{
            eraserCheck=0
            this.setColor(latestColor)
        }
    }
    fun undoLastPath() {
        if (coloredPaths.isNotEmpty()) {
            val undonePath = coloredPaths.removeAt(coloredPaths.size - 1)
            undonePaths.add(undonePath)
            invalidate()
        }
    }

    fun redoLastPath() {
        if (undonePaths.isNotEmpty()) {
            val redoPath = undonePaths.removeAt(undonePaths.size - 1)
            coloredPaths.add(redoPath)
            invalidate()
        }
    }

    fun getColoredPaths():MutableList<ColoredPath>{
        return coloredPaths
    }


}

