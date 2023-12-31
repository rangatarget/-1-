package com.example.madcamp_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

        val bgBrush = binding.bgBrush
        val bgEraser = binding.bgEraser

        bgBrush.visibility = View.VISIBLE

        btnBrush.setOnClickListener {
            bgBrush.visibility = View.VISIBLE
            bgEraser.visibility = View.INVISIBLE
            drawingView.setEraserMode(false)
        }

        btnEraser.setOnClickListener {
            bgEraser.visibility = View.VISIBLE
            bgBrush.visibility = View.INVISIBLE
            drawingView.setEraserMode(true)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
        startActivity(intent)
    }
}