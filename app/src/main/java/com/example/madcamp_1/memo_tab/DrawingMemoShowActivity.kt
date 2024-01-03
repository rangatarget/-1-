package com.example.madcamp_1.memo_tab

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.madcamp_1.MainActivity
import com.example.madcamp_1.R
import com.example.madcamp_1.databinding.ActivityDrawingMemoShowBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class DrawingMemoShowActivity : AppCompatActivity() {
    lateinit var drawingMemoTitle : String
    lateinit var drawingMemoDate: String
    lateinit var drawingMemoShow : ImageView

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

        binding.titleEdit.text = drawingMemoTitle

        val setBitmap = loadMemoModelFromInternalStorage(drawingMemoTitle, drawingMemoDate)

        drawingMemoShow = binding.drawingMemoShow

        drawingMemoShow.setImageBitmap(setBitmap)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
            startActivity(intent)
            finish()
        }

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
            R.id.menu_save -> {
                getBitmapFromImageView(drawingMemoShow)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //Q 버전 이상일 경우. (안드로이드 10, API 29 이상일 경우)
                    getBitmapFromImageView(drawingMemoShow)?.let { saveImageOnAboveAndroidQ(it) }
                    Toast.makeText(baseContext, "이미지 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // Q 버전 이하일 경우. 저장소 권한을 얻어온다.
                    val writePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    if(writePermission == PackageManager.PERMISSION_GRANTED) {
                        getBitmapFromImageView(drawingMemoShow)?.let { saveImageOnUnderAndroidQ(it) }
                        Toast.makeText(baseContext, "이미지 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        val requestExternalStorageCode = 1

                        val permissionStorage = arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        ActivityCompat.requestPermissions(this, permissionStorage, requestExternalStorageCode)
                    }
                }
                return true
            }
            R.id.menu_edit -> {
                val intent = Intent(this, DrawingMemoEditActivity::class.java)
                intent.putExtra("drawing_memo_title", drawingMemoTitle )
                intent.putExtra("drawing_memo_date", drawingMemoDate)
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
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageOnAboveAndroidQ(bitmap: Bitmap) {
        val fileName = System.currentTimeMillis().toString() + ".png" // 파일이름 현재시간.png

        /*
        * ContentValues() 객체 생성.
        * ContentValues는 ContentResolver가 처리할 수 있는 값을 저장해둘 목적으로 사용된다.
        * */
        val contentValues = ContentValues()
        contentValues.apply {
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/ImageSave") // 경로 설정
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName) // 파일이름을 put해준다.
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1) // 현재 is_pending 상태임을 만들어준다.
            // 다른 곳에서 이 데이터를 요구하면 무시하라는 의미로, 해당 저장소를 독점할 수 있다.
        }

        // 이미지를 저장할 uri를 미리 설정해놓는다.
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            if(uri != null) {
                val image = contentResolver.openFileDescriptor(uri, "w", null)
                // write 모드로 file을 open한다.

                if(image != null) {
                    val fos = FileOutputStream(image.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    //비트맵을 FileOutputStream를 통해 compress한다.
                    fos.close()

                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0) // 저장소 독점을 해제한다.
                    contentResolver.update(uri, contentValues, null, null)
                }
            }
        } catch(e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveImageOnUnderAndroidQ(bitmap: Bitmap) {
        val fileName = System.currentTimeMillis().toString() + ".png"
        val externalStorage = Environment.getExternalStorageDirectory().absolutePath
        val path = "$externalStorage/DCIM/imageSave"
        val dir = File(path)

        if(dir.exists().not()) {
            dir.mkdirs() // 폴더 없을경우 폴더 생성
        }

        try {
            val fileItem = File("$dir/$fileName")
            fileItem.createNewFile()
            //0KB 파일 생성.

            val fos = FileOutputStream(fileItem) // 파일 아웃풋 스트림

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            //파일 아웃풋 스트림 객체를 통해서 Bitmap 압축.

            fos.close() // 파일 아웃풋 스트림 객체 close

            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileItem)))
            // 브로드캐스트 수신자에게 파일 미디어 스캔 액션 요청. 그리고 데이터로 추가된 파일에 Uri를 넘겨준다.
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getBitmapFromImageView(imageView: ImageView): Bitmap? {
        val drawable: Drawable? = imageView.drawable

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        if (drawable != null) {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        return null
    }
}