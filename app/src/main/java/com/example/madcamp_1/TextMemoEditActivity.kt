package com.example.madcamp_1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.madcamp_1.databinding.ActivityTextMemoEditBinding

class TextMemoEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityTextMemoEditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val firstFragment : FirstFragment = FirstFragment()
        val secondFragment : SecondFragment = SecondFragment()
        val thirdFragment : ThirdFragment = ThirdFragment()

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
            startActivity(intent)
        //finish()
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
        startActivity(intent)
    }
}