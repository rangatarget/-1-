package com.example.madcamp_1

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.madcamp_1.databinding.FragmentSecondBinding
import android.animation.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream

class SecondFragment : Fragment() {
    private val REQUEST_PERMISSIONS = 1000
    private var isFabOpen = false
    private var imagelist = ArrayList<ImageModel>()
    private var image_len = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume(){
        super.onResume()
        val temp = MyApplication.prefs.getString("image_len", "")
        if(!temp.isNullOrBlank()) {
            image_len = Integer.parseInt(temp)
            for(i:Int in imagelist.size..image_len - 1){
                Log.v("loadBitmap", i.toString())
                loadBitmap(i)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentSecondBinding.inflate(inflater, container, false)

        //톱니바퀴버튼
        binding.button2.setOnClickListener {
            if (isFabOpen) {
                ObjectAnimator.ofFloat(binding.button21, "translationY", 0f).apply { start() }
                ObjectAnimator.ofFloat(binding.button22, "translationY", 0f).apply { start() }
            } else {
                ObjectAnimator.ofFloat(binding.button21, "translationY", -200f).apply { start() }
                ObjectAnimator.ofFloat(binding.button22, "translationY", -400f).apply { start() }
            }

            isFabOpen = !isFabOpen
        }

        //갤러리버튼
        binding.button21.setOnClickListener {
            requestPermission()
        }

        //삭제버튼
        binding.button22.setOnClickListener {
            Toast.makeText(context, "삭제 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun requestPermission() {
        // 권한이 부여되어 있는지 확인
        val readStoragePermission = context?.let { it1 -> ContextCompat.checkSelfPermission(it1, "android.permission.READ_MEDIA_IMAGES") }
        // 권한이 부여되어 있지 않다면 권한 요청
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            Log.v("권한 요청 조건성립", "1")
            ActivityCompat.requestPermissions(context as Activity, arrayOf<String>("android.permission.READ_MEDIA_IMAGES"), REQUEST_PERMISSIONS)
        } else {
            // 이미 권한이 부여된 경우 작업 수행
            Log.v("권한 이미 있음", "1")
            performActionWithPermissions()
        }
    }

    private fun performActionWithPermissions() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                2000 -> {
                    val uri = data?.data
                    val destinationFileName = "gallery_image_" + image_len.toString() + ".jpg"
                    context?.let { saveBitmapToFile(it, uri.toString(), destinationFileName) }
                    image_len = image_len + 1
                    MyApplication.prefs.setString("image_len", image_len.toString())
                }
            }
        }
    }
    private fun loadBitmap(index: Int){
        val filePath = File(context?.filesDir, "gallery_image_" + index.toString() + ".jpg").absolutePath
        val loadedBitmap = BitmapFactory.decodeFile(filePath)
        imagelist.add(ImageModel(index, loadedBitmap))
        val rcvgallery = activity?.findViewById<RecyclerView>(R.id.rcvGallery)
        val adapter = RecyclerAdapter(imagelist)
        adapter.notifyDataSetChanged()
        rcvgallery?.adapter = adapter
        rcvgallery?.layoutManager = GridLayoutManager(requireContext(), 3)
    }
    private fun saveBitmapToFile(context: Context, uri: String, filename: String) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(android.net.Uri.parse(uri))

        if (inputStream != null) {
            // 비트맵으로 변환
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // 비트맵을 파일로 저장
            val outputStream: FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

        }
    }
}