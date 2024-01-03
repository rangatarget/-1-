package com.example.madcamp_1.memo_tab

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.madcamp_1.MainActivity
import com.example.madcamp_1.MyApplication
import com.example.madcamp_1.R
import com.example.madcamp_1.databinding.ActivityTextMemoEditBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TextMemoEditActivity : AppCompatActivity() {

    lateinit var textContentContainer : LinearLayout

    lateinit var memo_title : String
    var textMemoTitle : String? = null
    var textMemoDate: String? = null

    lateinit var bitmap: Bitmap

    lateinit var saveMemo : String

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityTextMemoEditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        memo_title = "(제목 없음)"

        textContentContainer = binding.textMemoContainer

        binding.titleEdit.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                memo_title = binding.titleEdit.text.toString()
            }
        }
        textMemoTitle = intent.getStringExtra("text_memo_title")
        textMemoDate = intent.getStringExtra("text_memo_date")

        Log.d("textMemoTitle", "$textMemoTitle")
        Log.d("textMemoDate", "$textMemoDate")

        if (textMemoTitle!=null&&textMemoDate!=null){
            Log.d("??", "왜 여기로 오는거야")
            val content = MyApplication.prefs.getString("${textMemoTitle}_${textMemoDate}", "default")
            binding.memoContent.text = Editable.Factory.getInstance().newEditable(content)
            memo_title = textMemoTitle as String
            binding.titleEdit.hint = textMemoTitle
        } else {
            memo_title = "(제목 없음)"
        }

        val memoContent = binding.memoContent

        memoContent.setOnKeyListener { _, keyCode, event ->
            // 백버튼을 눌렀을 때
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // EditText의 포커스를 해제
                memoContent.clearFocus()
                saveMemo = memoContent.text.toString()
                return@setOnKeyListener true // 이벤트 소비
            }

            return@setOnKeyListener false // 다른 키 이벤트는 기본 동작으로 처리
        }

        binding.backButton.setOnClickListener {
            if (binding.titleEdit.hasFocus()){
                memoContent.clearFocus()
                memo_title = binding.titleEdit.text.toString()
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(memoContent.windowToken, 0)
                saveMemo = memoContent.text.toString()
            }
            if (memoContent.hasFocus()){
                memoContent.clearFocus()
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(memoContent.windowToken, 0)
                saveMemo = memoContent.text.toString()
            }else{
                if (textMemoTitle!=null&&textMemoDate!=null){
                    deleteMemoModelFromInternalStorage(textMemoTitle!!, textMemoDate!!)
                }
                saveMemo = memoContent.text.toString()

                val date = getCurrentDateTime()
                MyApplication.prefs.putString("${memo_title}_$date", saveMemo)

                Log.d("텍스트메모에딧에서 저장하는 키", "${memo_title}_$date")

                val bitmap = createBitmapFromLinearLayout(textContentContainer)
                saveMemoToInternalStorage(bitmap)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
                intent.putExtra("drawing_memo_title", memo_title)
                intent.putExtra("drawing_memo_date", date)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        val titleEditText = findViewById<EditText>(R.id.title_edit)
        val memoContent = findViewById<EditText>(R.id.memo_content)
        // EditText가 포커스 상태인지 확인하고 포커스가 있을 때만 clearFocus() 호출
        if (titleEditText.hasFocus()) {
            titleEditText.clearFocus()
        } else {
            super.onBackPressed()
            saveMemo = memoContent.text.toString()

            if (textMemoTitle!=null&&textMemoDate!=null){
                deleteMemoModelFromInternalStorage(textMemoTitle!!, textMemoDate!!)
            }

            val date = getCurrentDateTime()
            MyApplication.prefs.putString("${memo_title}_$date", saveMemo)

            Log.d("텍스트메모에딧에서 저장하는 키", "${memo_title}_$date")

            val bitmap = createBitmapFromLinearLayout(textContentContainer)
            saveMemoToInternalStorage(bitmap)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
            intent.putExtra("drawing_memo_title", memo_title)
            intent.putExtra("drawing_memo_date", date)
            startActivity(intent)
            finish()
        }
    }

    private fun createBitmapFromLinearLayout(linearLayout: LinearLayout): Bitmap {
        // LinearLayout의 크기를 얻어옴
        val width = linearLayout.width
        val height = linearLayout.height

        // 새로운 비트맵을 생성
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // 새로운 비트맵에 LinearLayout의 내용을 그림
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)  // 배경을 흰색으로 설정하거나 필요에 따라 변경
        linearLayout.draw(canvas)

        return bitmap
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
    private fun saveMemoToInternalStorage(bitmap: Bitmap) {
        val memoModel = MemoModel(memo_title, getCurrentDateTime(), bitmap)

        // MemoModel을 내부 저장소에 저장합니다.
        saveMemoModelToInternalStorage(memoModel)
    }

    private fun saveMemoModelToInternalStorage(memoModel: MemoModel) {
        // Bitmap을 바이트 배열로 변환합니다.
        val stream = ByteArrayOutputStream()
        memoModel.thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        try {
            // MemoModel을 내부 저장소에 저장합니다.
            val fileName = "${memoModel.title}_${memoModel.date}.png"
            val outputStream: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
            outputStream.write(byteArray)
            outputStream.close()

            Log.e("MemoModel", "MemoModel이 저장되었습니다: $fileName")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MemoModel", "MemoModel 저장 중 오류 발생: ${e.message}")
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
}