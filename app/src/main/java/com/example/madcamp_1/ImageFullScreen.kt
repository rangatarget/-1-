package com.example.madcamp_1

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp_1.databinding.ActivityDrawingMemoEditBinding
import com.example.madcamp_1.databinding.ActivityImageFullScreenBinding
import com.example.madcamp_1.databinding.FragmentSecondBinding
import java.io.File

class ImageFullScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityImageFullScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //item index 받기
        Log.v("풀스크린 띄움","인덱스 가져오기 전")
        val intent = intent
        val index = intent.getStringExtra("image_index")

        //fullscreen으로 띄우기
        if (index != null) {
            Log.v("풀스크린 띄움", index)
        }
        val filePath = File(this.filesDir, "gallery_image_" + index.toString() + ".jpg").absolutePath
        val loadedBitmap = BitmapFactory.decodeFile(filePath)
        if (loadedBitmap != null) {
            binding.fullscreen.setImageBitmap(loadedBitmap)
            Log.v("이미지 로드 성공", "$loadedBitmap")
        } else {
            //파일 로드 실패 처리
            Log.v("이미지 로드 실패", "")
        }

        //뒤로가기 눌렀을때 fragment2로 이동
        binding.backButton.setOnClickListener {
            Log.v("뒤로가기 누름", "")
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("deleted", "")
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_SECOND)
            startActivity(intent)
            finish()
        }

        //삭제버튼 눌렀을 때
        binding.delete.setOnClickListener {
            Log.v("삭제버튼 누름", "")
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("deleted", index.toString())
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_SECOND)
            startActivity(intent)
            finish()
        }



    }
}