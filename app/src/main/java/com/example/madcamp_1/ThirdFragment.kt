package com.example.madcamp_1

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.madcamp_1.databinding.FragmentThirdBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ThirdFragment : Fragment() {

    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentThirdBinding.inflate(inflater, container, false)

        val fabMain = binding.fabMain
        val fabDrawing = binding.fabOptionDrawing
        val fabText = binding.fabOptionText

        fabDrawing.scaleX = 0.3f
        fabDrawing.scaleY = 0.3f

        fabText.scaleX = 0.3f
        fabText.scaleY = 0.3f

        fabMain.setOnClickListener {
            toggleFab(fabMain, fabDrawing, fabText)
        }
        fabDrawing.setOnClickListener{
            Toast.makeText(context, "드로잉 버튼 클릭됨", Toast.LENGTH_SHORT).show()
        }

        fabText.setOnClickListener {
            //Toast.makeText(context, "텍스트 버튼 클릭됨", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, TextMemoEditActivity::class.java))

        }

        return binding.root
    }

    private fun toggleFab(fabMain:FloatingActionButton, fabDrawing : FloatingActionButton, fabText : FloatingActionButton){
        val targetScaleClosed = 0.3f
        val targetScaleOpen = 1f

        if (isFabOpen) {
            ObjectAnimator.ofFloat(fabDrawing, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(fabText, "translationY", 0f).apply { start() }
            Log.d("test", "닫기")
            ObjectAnimator.ofPropertyValuesHolder(
                fabDrawing,
                PropertyValuesHolder.ofFloat("scaleX", targetScaleClosed),
                PropertyValuesHolder.ofFloat("scaleY", targetScaleClosed)
            ).apply { start() }

            ObjectAnimator.ofPropertyValuesHolder(
                fabText,
                PropertyValuesHolder.ofFloat("scaleX", targetScaleClosed),
                PropertyValuesHolder.ofFloat("scaleY", targetScaleClosed)
            ).apply { start() }

        //fabMain.setImageResource(R.drawable.icon_contact)

        } else {
            ObjectAnimator.ofFloat(fabDrawing, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(fabText, "translationY", -400f).apply { start() }
            Log.d("test", "열기")
            ObjectAnimator.ofPropertyValuesHolder(
                fabDrawing,
                PropertyValuesHolder.ofFloat("scaleX", targetScaleOpen),
                PropertyValuesHolder.ofFloat("scaleY", targetScaleOpen)
            ).apply { start() }

            ObjectAnimator.ofPropertyValuesHolder(
                fabText,
                PropertyValuesHolder.ofFloat("scaleX", targetScaleOpen),
                PropertyValuesHolder.ofFloat("scaleY", targetScaleOpen)
            ).apply { start() }

        //fabMain.setImageResource(R.drawable.icon_pencil)

        }

        isFabOpen = !isFabOpen
    }




}