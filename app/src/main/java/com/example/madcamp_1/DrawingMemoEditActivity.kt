package com.example.madcamp_1

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.example.madcamp_1.databinding.ActivityDrawingMemoEditBinding
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener


class DrawingMemoEditActivity : AppCompatActivity() {

    private var isBrushMenuOn = false
    private var isEraserMenuOn = false
    lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityDrawingMemoEditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
            startActivity(intent)
            //finish()
        }

        drawingView = binding.drawingView

        val btnBrush = binding.btnBrush
        val btnEraser = binding.btnEraser
        val btnBack = binding.btnBack

        val bgBrush = binding.bgBrush
        val bgEraser = binding.bgEraser
        bgBrush.visibility = View.VISIBLE

        val brushMenu = binding.brushMenuCard
        val brushSeekBar = binding.brushWidthSeekBar
        val brushWidthText = binding.brushSeekBarText

        brushMenu.y += 500f
        brushMenu.scaleX = 0.1f
        brushMenu.scaleY = 0.1f

        val eraserMenu = binding.eraserMenuCard
        val eraserSeekBar = binding.eraserWidthSeekBar
        val eraserWidthText = binding.eraserSeekBarText

        eraserMenu.y += 500f
        eraserMenu.scaleX = 0.1f
        eraserMenu.scaleY = 0.1f

        btnBrush.setOnClickListener {
            drawingView.setStrokeWidth(brushSeekBar.progress.toFloat())

            val targetScaleClosed = 0.1f
            val targetScaleOpen = 1f

            if (isBrushMenuOn) {
                ObjectAnimator.ofFloat(brushMenu, "translationY", 500f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    brushMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleClosed),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleClosed)
                ).apply { start() }
                isBrushMenuOn = false
            }
            else if (!isBrushMenuOn && bgBrush.visibility == View.VISIBLE){

                ObjectAnimator.ofFloat(brushMenu, "translationY", 0f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    brushMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleOpen),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleOpen)
                ).apply { start() }
                isBrushMenuOn = true
            }

            bgBrush.visibility = View.VISIBLE
            bgEraser.visibility = View.INVISIBLE
            drawingView.setEraserMode(false)
        }

        btnEraser.setOnClickListener {
            drawingView.setStrokeWidth(eraserSeekBar.progress.toFloat())

            val targetScaleClosed = 0.1f
            val targetScaleOpen = 1f

            if (isEraserMenuOn) {
                ObjectAnimator.ofFloat(eraserMenu, "translationY", 500f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    eraserMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleClosed),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleClosed)
                ).apply { start() }
                isEraserMenuOn = false
            }
            else if (!isEraserMenuOn && bgEraser.visibility == View.VISIBLE){

                ObjectAnimator.ofFloat(eraserMenu, "translationY", 0f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    eraserMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleOpen),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleOpen)
                ).apply { start() }
                isEraserMenuOn = true
            }

            bgEraser.visibility = View.VISIBLE
            bgBrush.visibility = View.INVISIBLE
            drawingView.setEraserMode(true)
        }

        brushSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // SeekBar 값이 변경될 때 호출됨
                // progress: 현재 SeekBar의 값
                // fromUser: 사용자가 조작했을 때 true, 코드에서 변경한 경우 false
                // TODO: 원하는 작업 수행
                brushWidthText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 조작을 시작할 때 호출됨
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 조작을 멈출 때 호출됨
                // TODO: 값을 사용하여 원하는 작업 수행
                val currentValue = seekBar?.progress ?: 0
                drawingView.setStrokeWidth(currentValue.toFloat())
            }
        })

        eraserSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // SeekBar 값이 변경될 때 호출됨
                // progress: 현재 SeekBar의 값
                // fromUser: 사용자가 조작했을 때 true, 코드에서 변경한 경우 false
                // TODO: 원하는 작업 수행
                eraserWidthText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 조작을 시작할 때 호출됨
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 조작을 멈출 때 호출됨
                // TODO: 값을 사용하여 원하는 작업 수행
                val currentValue = seekBar?.progress ?: 0
                drawingView.setStrokeWidth(currentValue.toFloat())
            }
        })

        btnBack.setOnClickListener {
            drawingView.undoLastPath()
        }

        val btnColorPicker = binding.btnColorPicker
        val brushColor = binding.brushColor

        btnColorPicker.setOnClickListener {
            openColorPicker(drawingView, brushColor)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
        startActivity(intent)
    }

    private fun isTouchOutsideView(event: MotionEvent, view: View): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]
        return !(event.x > x && event.x < x + view.width && event.y > y && event.y < y + view.height)
    }

    fun openColorPicker(dV : DrawingView, brushColor : Button) {
        val awd = AmbilWarnaDialog(
            this,
            R.color.colorAccent,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog) {
                    // onCancel 내용
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    dV.setColor(color)
                    brushColor.backgroundTintList = ColorStateList.valueOf(color)
                }
            }
        )
        awd.show()
    }

}