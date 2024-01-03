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
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date

class SecondFragment : Fragment() {
    private val REQUEST_PERMISSIONS_READ_MEDIA_IMAGES = 1000
    private val REQUEST_PERMISSIONS_CAMERA = 1001
    private var isFabOpen = false
    private var imagelist = ArrayList<ImageModel>()
    private var image_len = 0
    private var photoURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("onCreate 실행", image_len.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v("onCreateView 실행", image_len.toString())

        val binding = FragmentSecondBinding.inflate(inflater, container, false)

        //톱니바퀴버튼
        binding.button2.setOnClickListener {
            if (isFabOpen) {
                ObjectAnimator.ofFloat(binding.button21, "translationY", 0f).apply { start() }
                ObjectAnimator.ofFloat(binding.button22, "translationY", 0f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.button21,
                    PropertyValuesHolder.ofFloat("scaleX", 0.3f),
                    PropertyValuesHolder.ofFloat("scaleY", 0.3f)
                ).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.button22,
                    PropertyValuesHolder.ofFloat("scaleX", 0.3f),
                    PropertyValuesHolder.ofFloat("scaleY", 0.3f)
                ).apply { start() }
            } else {
                ObjectAnimator.ofFloat(binding.button21, "translationY", -200f).apply { start() }
                ObjectAnimator.ofFloat(binding.button22, "translationY", -400f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.button21,
                    PropertyValuesHolder.ofFloat("scaleX", 1f),
                    PropertyValuesHolder.ofFloat("scaleY", 1f)
                ).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.button22,
                    PropertyValuesHolder.ofFloat("scaleX", 1f),
                    PropertyValuesHolder.ofFloat("scaleY", 1f)
                ).apply { start() }
            }

            isFabOpen = !isFabOpen
        }

        //갤러리버튼
        binding.button21.setOnClickListener {
            requestPermission_Gallery()
        }

        //카메라버튼
        binding.button22.setOnClickListener {
            Toast.makeText(context, "카메라 버튼 클릭", Toast.LENGTH_SHORT).show()
            requestPermission_Camera()
        }

        val adapter = context?.let { RecyclerAdapter(it, imagelist) }
        binding.rcvGallery.adapter = adapter
        binding.rcvGallery.layoutManager = GridLayoutManager(requireContext(), 3)
        if (adapter != null) {
            adapter.notifyDataSetChanged()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart(){
        super.onStart()
        Log.v("onStart 실행", image_len.toString())

    }

    @SuppressLint("WrongThread")
    override fun onResume(){
        super.onResume()
        Log.v("onResume 실행", image_len.toString())
        //image_len 불러오기
        image_len = Integer.parseInt(MyApplication.prefs.getString("image_len", "0"))

        //deleted 여부 확인
        val deleted = MyApplication.prefs.getString("deleted", "")
        isDeleted(deleted)
        //삭제하고 온 경우
        if(deleted != "") {
            val deleted_index = Integer.parseInt(deleted)
            for (i: Int in deleted_index ..image_len) {
                imagelist.removeLast()
            }
            for (i: Int in deleted_index..image_len - 1) {
                loadBitmap(i)
            }
        }
        //추가 또는 뒤로가기 또는 다른 fragment에서 온 경우
        else {
            for (i: Int in imagelist.size ..image_len - 1) {
                loadBitmap(i)
            }
        }
        //회전시킨 경우
        val rotated = MyApplication.prefs.getString("rotated", "")
        if(rotated != "") {
            MyApplication.prefs.setString("rotated", "")
            val rotated_index = Integer.parseInt(rotated)
            val bitmap_old = imagelist[rotated_index].bitmap
            val rotated_num = MyApplication.prefs.getString("rotated_num", "0")
            val angle = (90 * (Integer.parseInt(rotated_num))%360)
            Log.v("rotated", angle.toString())
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            val bitmap_new = Bitmap.createBitmap(bitmap_old, 0, 0, bitmap_old.width, bitmap_old.height, matrix, true)
            imagelist[rotated_index].bitmap = bitmap_new
            val outputStream: FileOutputStream = requireContext().openFileOutput("gallery_image_" + rotated_index.toString() + ".jpg", Context.MODE_PRIVATE)
            bitmap_new.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        }

        //rcvgallery 업데이트
        val rcvgallery = activity?.findViewById<RecyclerView>(R.id.rcvGallery)
        val adapter = context?.let { RecyclerAdapter(it, imagelist) }
        adapter?.notifyDataSetChanged()
        rcvgallery?.adapter = adapter
        rcvgallery?.layoutManager = GridLayoutManager(requireContext(), 3)
        MyApplication.prefs.setString("image_len", image_len.toString())
        //글씨 업데이트
        val blankmsg = activity?.findViewById<TextView>(R.id.initialmsg)
        if(image_len == 0) {
            Log.v("initial msg 지우기", image_len.toString())
            if (blankmsg != null) {
                blankmsg.visibility = View.VISIBLE
            }
        }
        else {
            Log.v("initial msg 띄우기", image_len.toString())
            if (blankmsg != null) {
                blankmsg.visibility = View.GONE
            }
        }
    }


    //함수들

    private fun isDeleted(deleted: String){
        Log.v("isDeleted", "삭제될 인덱스: " + deleted + "    image_len: " + image_len.toString())
        if(deleted == "") return
        val index = Integer.parseInt(deleted)
        val file = File(context?.filesDir, "gallery_image_" + index.toString() + ".jpg")
        file.delete()
        for(i:Int in index + 1..image_len - 1){
            val oldFile = File(context?.filesDir, "gallery_image_" + i.toString() + ".jpg")
            val newFile = File(context?.filesDir, "gallery_image_" + (i-1).toString() + ".jpg")
            oldFile.renameTo(newFile)
        }
        image_len = image_len - 1
    }

    //갤러리, 카메라 접근
    private fun performActionWithPermissions_Gallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }

    private fun performActionWithPermissions_Camera() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri : Uri? = createImageUri("JPEG_${timeStamp}_", "image/jpeg")
        photoURI = uri
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, 2001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("onActivityResult", "image_len: " + image_len.toString())
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                2000 -> { //gallery
                    val uri = data?.data
                    val destinationFileName = "gallery_image_" + image_len.toString() + ".jpg"
                    context?.let { saveGalleryToFile(it, uri.toString(), destinationFileName) }
                    image_len = image_len + 1
                    MyApplication.prefs.setString("image_len", image_len.toString())
                    MyApplication.prefs.setString("deleted", "")
                }
            }
            when (requestCode) {
                2001 -> { //camera
                    if( photoURI != null)
                    {
                        context?.let { saveCameraToFile(it, photoURI!!, "gallery_image_" + image_len.toString() + ".jpg") }
                        photoURI = null
                    }
                    image_len = image_len + 1
                    MyApplication.prefs.setString("image_len", image_len.toString())
                    MyApplication.prefs.setString("deleted", "")
                }
            }
        }
    }
    //비트맵 사용
    @SuppressLint("NotifyDataSetChanged")
    private fun loadBitmap(index: Int){
        Log.v("loadBitmap 실행", "imagelist.size: " + (imagelist.size).toString())
        val filePath = File(context?.filesDir, "gallery_image_" + index.toString() + ".jpg").absolutePath
        val loadedBitmap = BitmapFactory.decodeFile(filePath)
        imagelist.add(ImageModel(index, loadedBitmap))
        val rcvgallery = activity?.findViewById<RecyclerView>(R.id.rcvGallery)
        val adapter = context?.let { RecyclerAdapter(it, imagelist) }
        adapter?.notifyDataSetChanged()
        rcvgallery?.adapter = adapter
        rcvgallery?.layoutManager = GridLayoutManager(requireContext(), 3)
    }
    private fun saveGalleryToFile(context: Context, uri: String, filename: String) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(Uri.parse(uri))
        if (inputStream != null) {
            // 비트맵으로 변환
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // 비트맵을 파일로 저장
            val outputStream: FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private fun saveCameraToFile(context: Context, uri: Uri, filename: String) {
        var image: Bitmap? = null
        image = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))

        // 비트맵을 파일로 저장
        val outputStream: FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    //권한 요청
    private fun requestPermission_Gallery() {
        // 권한이 부여되어 있는지 확인
        val readStoragePermission = context?.let { it1 -> ContextCompat.checkSelfPermission(it1, "android.permission.READ_MEDIA_IMAGES") }
        // 권한이 부여되어 있지 않다면 권한 요청
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            Log.v("권한 요청 조건성립", "갤러리 읽기")
            ActivityCompat.requestPermissions(context as Activity, arrayOf<String>("android.permission.READ_MEDIA_IMAGES"), REQUEST_PERMISSIONS_READ_MEDIA_IMAGES)
        } else {
            // 이미 권한이 부여된 경우 작업 수행
            Log.v("권한 이미 있음", "갤러리 읽기")
            performActionWithPermissions_Gallery()
        }
    }

    private fun requestPermission_Camera() {
        // 권한이 부여되어 있는지 확인
        val cameraPermission = context?.let { it1 -> ContextCompat.checkSelfPermission(it1, "android.permission.CAMERA") }
        // 권한이 부여되어 있지 않다면 권한 요청
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            Log.v("권한 요청 조건성립", "카메라")
            ActivityCompat.requestPermissions(context as Activity, arrayOf<String>("android.permission.CAMERA"), REQUEST_PERMISSIONS_CAMERA)
        } else {
            // 이미 권한이 부여된 경우 작업 수행
            Log.v("권한 이미 있음", "카메라")
            performActionWithPermissions_Camera()
        }
    }
    //새로운 시도
    private fun createImageUri(filename:String, mimeType:String): Uri?{
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME,filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        return context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    fun rotateBitmap(photoPath: String): Bitmap? {
        try {
            val exif = ExifInterface(photoPath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

            val rotationAngle = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }

            val options = BitmapFactory.Options()
            options.inSampleSize = 2  // 이미지 크기를 조절할 수 있음

            val originalBitmap = BitmapFactory.decodeFile(photoPath, options)

            return if (rotationAngle != 0) {
                val matrix = android.graphics.Matrix()
                matrix.postRotate(rotationAngle.toFloat())
                Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
            } else {
                originalBitmap
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

}