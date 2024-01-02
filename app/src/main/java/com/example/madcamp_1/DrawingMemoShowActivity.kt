package com.example.madcamp_1

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.madcamp_1.databinding.ActivityDrawingMemoShowBinding
import java.io.File
import java.io.FileInputStream

class DrawingMemoShowActivity : AppCompatActivity() {
    lateinit var drawingMemoTitle : String
    lateinit var drawingMemoDate: String
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityDrawingMemoShowBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        drawingMemoTitle = intent.getStringExtra("drawing_memo_title").toString()
        drawingMemoDate = intent.getStringExtra("drawing_memo_date").toString()

        Log.d("test", "${drawingMemoTitle}")
        Log.d("test", "${drawingMemoDate}")

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        val setBitmap = loadMemoModelFromInternalStorage(drawingMemoTitle, drawingMemoDate)

        val drawingMemoShow = binding.drawingMemoShow

        drawingMemoShow.setImageBitmap(setBitmap)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_drawing, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteMemoModelFromInternalStorage(drawingMemoTitle, drawingMemoDate)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
                startActivity(intent)
                finish()
                return true
            }
            // 필요에 따라 추가 메뉴 아이템 처리
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun loadMemoModelFromInternalStorage(memoTitle: String, memoDate: String): Bitmap? {
        try {
            // 파일 이름 생성
            val fileName = "${memoTitle}_${memoDate}.png"

            // 내부 저장소에서 파일을 읽어옵니다.
            val inputStream: FileInputStream = openFileInput(fileName)
            val byteArray = inputStream.readBytes()
            inputStream.close()

            // 바이트 배열을 비트맵으로 변환
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            Log.e("MemoModel", "MemoModel이 불러와졌습니다: $fileName")

            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MemoModel", "MemoModel 불러오기 중 오류 발생: ${e.message}")
            return null
        }
    }
    private fun deleteMemoModelFromInternalStorage(title: String, date: String) {
        try {
            // Generate the file name based on title and date
            val fileName = "${title}_${date}.png"

            // Delete the file from internal storage
            val file = File(filesDir, fileName)
            if (file.exists()) {
                file.delete()
                Log.e("MemoModel", "MemoModel이 삭제되었습니다: $fileName")
            } else {
                Log.e("MemoModel", "MemoModel 파일이 존재하지 않습니다: $fileName")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MemoModel", "MemoModel 삭제 중 오류 발생: ${e.message}")
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
        startActivity(intent)
    }

}