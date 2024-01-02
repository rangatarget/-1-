package com.example.madcamp_1

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.madcamp_1.databinding.FragmentThirdBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Calendar


class ThirdFragment : Fragment() {

    private var isFabOpen = false
    private lateinit var MainActivity : MainActivity

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
        val itemList : ArrayList<MemoModel> = ArrayList()

        val img = context?.let { loadBitmapFromInternalStorage(it, "${MainActivity.drawingMemoDate}.jpg") }

        if (img==null){
            Log.d("이미지 null 확인", "$img")
        }

        val memoTest = img?.let {
            MemoModel(
                title = MainActivity.drawingMemoTitle,
                date = MainActivity.drawingMemoDate,
                thumbnail = img
            )
        }

        if (memoTest != null) {
            itemList.add(memoTest)
        }

        val adapter = MyMemoAdapter(itemList)
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

}