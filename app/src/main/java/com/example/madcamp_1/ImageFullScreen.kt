package com.example.madcamp_1

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
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
        var rotated_num = 0
        var rotated = ""

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
        binding.fullscreen.scaleType = ImageView.ScaleType.FIT_XY

        //뒤로가기 눌렀을때 fragment2로 이동
        binding.backButton.setOnClickListener {
            Log.v("뒤로가기 누름", "")
            MyApplication.prefs.setString("deleted", "")
            MyApplication.prefs.setString("rotated", "")
            MyApplication.prefs.setString("rotated", rotated)
            MyApplication.prefs.setString("rotated_num", rotated_num.toString())
            Log.v("backbutton", "rotated:$rotated, $rotated_num")
            finish()
        }

        //삭제버튼 눌렀을 때
        binding.delete.setOnClickListener {
            Log.v("삭제버튼 누름", "")
            if (index != null) {
                MyApplication.prefs.setString("deleted", index)
            }
            finish()
        }

        //회전버튼 눌렀을 때
        binding.rotate.setOnClickListener {
            Log.v("회전버튼 누름", "")
            if (index != null) {
                rotated = index.toString()
            }
            rotated_num = rotated_num + 1
            binding.fullscreen.rotation = binding.fullscreen.rotation + 90f
        }



    }
}