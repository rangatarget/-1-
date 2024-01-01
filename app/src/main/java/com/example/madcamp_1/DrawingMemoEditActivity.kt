package com.example.madcamp_1

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.example.madcamp_1.databinding.ActivityDrawingMemoEditBinding
import com.example.madcamp_1.databinding.ActivityTextMemoEditBinding

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

        val brushMenu = binding.brushMenuCard
        val slideInUp = AnimationUtils.loadAnimation(this, R.anim.slide_in_up)
        val slideOutDown = AnimationUtils.loadAnimation(this, R.anim.slide_out_down)
        brushMenu.y += 500f
        brushMenu.scaleX = 0.1f
        brushMenu.scaleY = 0.1f



        val bgBrush = binding.bgBrush
        val bgEraser = binding.bgEraser

        var isBrushMenuOn = false

        bgBrush.visibility = View.VISIBLE

        btnBrush.setOnClickListener {
            val targetScaleClosed = 0.1f
            val targetScaleOpen = 1f

            bgBrush.visibility = View.VISIBLE
            bgEraser.visibility = View.INVISIBLE

            if (!isBrushMenuOn) {
                ObjectAnimator.ofFloat(brushMenu, "translationY", 0f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    brushMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleOpen),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleOpen)
                ).apply { start() }
                isBrushMenuOn = !isBrushMenuOn
            }
            else {
                ObjectAnimator.ofFloat(brushMenu, "translationY", 500f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    brushMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleClosed),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleClosed)
                ).apply { start() }
                isBrushMenuOn = !isBrushMenuOn

            }
            drawingView.setEraserMode(false)
        }

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