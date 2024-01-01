package com.example.madcamp_1

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import com.example.madcamp_1.databinding.ActivityDrawingMemoEditBinding

class DrawingMemoEditActivity : AppCompatActivity() {
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

        val drawingView = binding.drawingView

        val btnBrush = binding.btnBrush
        val btnEraser = binding.btnEraser
        val btnBack = binding.btnBack

        val bgBrush = binding.bgBrush
        val bgEraser = binding.bgEraser
        bgBrush.visibility = View.VISIBLE

        val brushMenu = binding.brushMenuCard
        val brushSeekBar = binding.brushWidthSeekBar
        val brushWidthText = binding.seekBarText

        brushMenu.y += 500f
        brushMenu.scaleX = 0.1f
        brushMenu.scaleY = 0.1f
        var isBrushMenuOn = false


        btnBrush.setOnClickListener {
            val targetScaleClosed = 0.1f
            val targetScaleOpen = 1f

            bgBrush.visibility = View.VISIBLE
            bgEraser.visibility = View.INVISIBLE

            if (isBrushMenuOn) {
                ObjectAnimator.ofFloat(brushMenu, "translationY", 500f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    brushMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleClosed),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleClosed)
                ).apply { start() }
                isBrushMenuOn = !isBrushMenuOn
            }
            else {

                ObjectAnimator.ofFloat(brushMenu, "translationY", 0f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    brushMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleOpen),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleOpen)
                ).apply { start() }
                isBrushMenuOn = !isBrushMenuOn
            }
            drawingView.setEraserMode(false)
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
                //Toast.makeText(this@DrawingMemoEditActivity, "SeekBar 값: $currentValue", Toast.LENGTH_SHORT).show()
                drawingView.setStrokeWidth(currentValue.toFloat())
            }
        })


        btnEraser.setOnClickListener {
            bgEraser.visibility = View.VISIBLE
            bgBrush.visibility = View.INVISIBLE
            drawingView.setEraserMode(true)
        }

        btnBack.setOnClickListener {
            drawingView.undoLastPath()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
        startActivity(intent)
    }
}