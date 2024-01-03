package com.example.madcamp_1.memo_tab

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.madcamp_1.MainActivity
import com.example.madcamp_1.databinding.FragmentThirdBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Calendar


class ThirdFragment : Fragment() {

    private var isFabOpen = false
    private lateinit var MainActivity : MainActivity
    private val itemList : ArrayList<MemoModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 액티비티의 참조를 얻기
        if (context is MainActivity) {
            MainActivity = context
        }
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
            val intent = Intent(context, DrawingMemoEditActivity::class.java)
            startActivity(intent)
            activity?.finish()
            Log.d("test", "fabDrawing 클릭됨")        }

        fabText.setOnClickListener {
            val intent = Intent(context, TextMemoEditActivity::class.java)
            startActivity(intent)
            activity?.finish()
            Log.d("test", "fabText 클릭됨")
        }

        loadAllMemoModels()

        val adapter = MyMemoAdapter(sortMemoModelsByDateDescending(itemList), activity as MainActivity)
        adapter.notifyDataSetChanged()
        binding.rcvMemo.adapter = adapter
        binding.rcvMemo.layoutManager = GridLayoutManager(requireContext(), 3)

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

    fun generateRandomDate(): String {
        val calendar = Calendar.getInstance()
        val year = (Math.random() * 10).toInt() + 2020 // 임의의 년도 (2020 ~ 2029)
        val month = (Math.random() * 12).toInt() + 1 // 임의의 월 (1 ~ 12)
        val day = (Math.random() * 28).toInt() + 1 // 임의의 일 (1 ~ 28)

        calendar.set(year, month, day)
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }

    fun loadBitmapFromInternalStorage(context: Context, fileName: String): Bitmap? {
        val file = File(context.cacheDir, fileName)

        return if (file.exists()) {
            try {
                val fileInputStream = FileInputStream(file)
                val bitmap = BitmapFactory.decodeStream(fileInputStream)
                fileInputStream.close()
                bitmap
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }

    private fun loadAllMemoModels() {
        try {
            // 내부 저장소에 저장된 파일 목록을 가져옵니다.
            val fileList = requireContext().fileList()

            // 파일 목록을 순회하며 MemoModel을 읽어옵니다.
            for (fileName in fileList) {
                if (fileName.endsWith(".png")) { // 이미지 파일인지 확인 (확장자가 .png인 경우)
                    val inputStream: FileInputStream = requireContext().openFileInput(fileName)
                    val byteArray = inputStream.readBytes()
                    inputStream.close()

                    // 파일 이름에서 MemoModel의 제목과 날짜를 추출합니다.
                    val titleDateArray = fileName.split("_")
                    val title = titleDateArray[0]
                    val date = titleDateArray[1].removeSuffix(".png")

                    // MemoModel을 생성하여 itemList에 추가합니다.
                    val memoModel = MemoModel(title, date, BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
                    itemList.add(memoModel)
                }
            }

            Log.e("MemoModel", "모든 MemoModels를 내부 저장소에서 불러왔습니다.")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MemoModel", "MemoModels 불러오기 중 오류 발생: ${e.message}")
        }
    }
    fun sortMemoModelsByDateDescending(memoModels: ArrayList<MemoModel>): ArrayList<MemoModel> {
        return ArrayList(memoModels.sortedByDescending { it.date })
    }
}