package com.example.madcamp_1.memo_tab

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.madcamp_1.MainActivity
import com.example.madcamp_1.R
import com.example.madcamp_1.databinding.ActivityTextMemoShowBinding
import java.io.File

class TextMemoShowActivity : AppCompatActivity() {

    var textMemoContent : String? = null
    lateinit var textMemoTitle : String
    lateinit var textMemoDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityTextMemoShowBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        textMemoContent = intent.getStringExtra("text_memo_content").toString()
        textMemoTitle = intent.getStringExtra("text_memo_title").toString()
        textMemoDate = intent.getStringExtra("text_memo_date").toString()
        binding.titleEdit.text = textMemoTitle

        binding.textMemoShow.text = textMemoContent
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.text_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteMemoModelFromInternalStorage(textMemoTitle, textMemoDate)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
                startActivity(intent)
                finish()
                return true
            }

            R.id.menu_edit -> {
                val intent = Intent(this, TextMemoEditActivity::class.java)
                intent.putExtra("text_memo_title", textMemoTitle )
                intent.putExtra("text_memo_date", textMemoDate)
                startActivity(intent)
                finish()
                return true
            }

            // 필요에 따라 추가 메뉴 아이템 처리
            else -> return super.onOptionsItemSelected(item)
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
        finish()
    }
}