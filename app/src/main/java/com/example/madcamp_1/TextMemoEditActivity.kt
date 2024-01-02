package com.example.madcamp_1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import com.example.madcamp_1.databinding.ActivityTextMemoEditBinding

class TextMemoEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityTextMemoEditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
            startActivity(intent)
        //finish()
        }

        val memoContent = binding.memoContent

        memoContent.setOnKeyListener { _, keyCode, event ->
            // 백버튼을 눌렀을 때
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // EditText의 포커스를 해제
                memoContent.clearFocus()
                return@setOnKeyListener true // 이벤트 소비
            }
            return@setOnKeyListener false // 다른 키 이벤트는 기본 동작으로 처리
        }


    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
        startActivity(intent)
    }
}