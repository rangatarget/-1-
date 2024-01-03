package com.example.madcamp_1

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.madcamp_1.databinding.ActivityDrawingMemoEditBinding
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DrawingMemoEditActivity : AppCompatActivity() {

    private var isBrushMenuOn = false
    private var isEraserMenuOn = false
    lateinit var drawingView: DrawingView

    lateinit var memo_title : String

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityDrawingMemoEditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        memo_title = binding.titleEdit.text.toString()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
            startActivity(intent)
            finish()
        }

        drawingView = binding.drawingView

        val btnBrush = binding.btnBrush
        val btnEraser = binding.btnEraser
        val btnBack = binding.btnBack
        val btnForward = binding.btnForward
        val btnSaveImg = binding.btnSaveImg

        val bgBrush = binding.bgBrush
        val bgEraser = binding.bgEraser
        bgBrush.visibility = View.VISIBLE

        val brushMenu = binding.brushMenuCard
        val brushSeekBar = binding.brushWidthSeekBar
        val brushWidthText = binding.brushSeekBarText

        brushMenu.y += 500f
        brushMenu.scaleX = 0.1f
        brushMenu.scaleY = 0.1f

        val eraserMenu = binding.eraserMenuCard
        val eraserSeekBar = binding.eraserWidthSeekBar
        val eraserWidthText = binding.eraserSeekBarText

        eraserMenu.y += 500f
        eraserMenu.scaleX = 0.1f
        eraserMenu.scaleY = 0.1f

        btnBrush.setOnClickListener {
            drawingView.setStrokeWidth(brushSeekBar.progress.toFloat())

            isEraserMenuOn = false
            eraserMenu.y += 500f
            eraserMenu.scaleX = 0.1f
            eraserMenu.scaleY = 0.1f

            val targetScaleClosed = 0.1f
            val targetScaleOpen = 1f

            if (isBrushMenuOn) {
                ObjectAnimator.ofFloat(brushMenu, "translationY", 500f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    brushMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleClosed),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleClosed)
                ).apply { start() }
                isBrushMenuOn = false
            }
            else if (!isBrushMenuOn && bgBrush.visibility == View.VISIBLE){

                ObjectAnimator.ofFloat(brushMenu, "translationY", 0f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    brushMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleOpen),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleOpen)
                ).apply { start() }
                isBrushMenuOn = true
            }

            bgBrush.visibility = View.VISIBLE
            bgEraser.visibility = View.INVISIBLE
            drawingView.setEraserMode(false)
        }

        btnEraser.setOnClickListener {
            drawingView.setStrokeWidth(eraserSeekBar.progress.toFloat())

            isBrushMenuOn = false
            brushMenu.y += 500f
            brushMenu.scaleX = 0.1f
            brushMenu.scaleY = 0.1f

            val targetScaleClosed = 0.1f
            val targetScaleOpen = 1f

            if (isEraserMenuOn) {
                ObjectAnimator.ofFloat(eraserMenu, "translationY", 500f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    eraserMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleClosed),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleClosed)
                ).apply { start() }
                isEraserMenuOn = false
            }
            else if (!isEraserMenuOn && bgEraser.visibility == View.VISIBLE){

                ObjectAnimator.ofFloat(eraserMenu, "translationY", 0f).apply { start() }
                ObjectAnimator.ofPropertyValuesHolder(
                    eraserMenu,
                    PropertyValuesHolder.ofFloat("scaleX", targetScaleOpen),
                    PropertyValuesHolder.ofFloat("scaleY", targetScaleOpen)
                ).apply { start() }
                isEraserMenuOn = true
            }

            bgEraser.visibility = View.VISIBLE
            bgBrush.visibility = View.INVISIBLE
            drawingView.setEraserMode(true)
        }

        brushSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // SeekBar 값이 변경될 때 호출됨
                // progress: 현재 SeekBar의 값
                // fromUser: 사용자가 조작했을 때 true, 코드에서 변경한 경우 false
                // TODO: 원하는 작업 수행
                brushWidthText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 조작을 시작할 때 호출됨
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 조작을 멈출 때 호출됨
                // TODO: 값을 사용하여 원하는 작업 수행
                val currentValue = seekBar?.progress ?: 0
                drawingView.setStrokeWidth(currentValue.toFloat())
            }
        })

        eraserSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // SeekBar 값이 변경될 때 호출됨
                // progress: 현재 SeekBar의 값
                // fromUser: 사용자가 조작했을 때 true, 코드에서 변경한 경우 false
                // TODO: 원하는 작업 수행
                eraserWidthText.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 조작을 시작할 때 호출됨
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 조작을 멈출 때 호출됨
                // TODO: 값을 사용하여 원하는 작업 수행
                val currentValue = seekBar?.progress ?: 0
                drawingView.setStrokeWidth(currentValue.toFloat())
            }
        })

        btnBack.setOnClickListener {
            drawingView.undoLastPath()
        }

        btnForward.setOnClickListener {
            drawingView.redoLastPath()
        }

        val btnColorPicker = binding.btnColorPicker
        val brushColor = binding.brushColor

        val btnRed = binding.btnRed
        val btnOrange = binding.btnOrange
        val btnYellow = binding.btnYellow
        val btnGreen = binding.btnGreen
        val btnBlue = binding.btnBlue
        val btnBrown = binding.btnBrown
        val btnGray = binding.btnGray
        val btnBlueGray = binding.btnBlueGray
        val btnDarkGray = binding.btnDarkGary

        btnRed.setOnClickListener {
            setBasicColor("#FF2F22",brushColor)
        }

        btnOrange.setOnClickListener {
            setBasicColor("#FF9800",brushColor)
        }

        btnYellow.setOnClickListener {
            setBasicColor("#FFE03B",brushColor)
        }

        btnGreen.setOnClickListener {
            setBasicColor("#4CAF50",brushColor)
        }

        btnBlue.setOnClickListener {
            setBasicColor("#4490EA",brushColor)
        }

        btnBlue.setOnClickListener {
            setBasicColor("#4490EA",brushColor)
        }

        btnBrown.setOnClickListener {
            setBasicColor("#795548",brushColor)
        }

        btnGray.setOnClickListener {
            setBasicColor("#9E9E9E",brushColor)
        }

        btnBlueGray.setOnClickListener {
            setBasicColor("#607D8B",brushColor)
        }

        btnDarkGray.setOnClickListener {
            setBasicColor("#333333",brushColor)
        }

        btnColorPicker.setOnClickListener {
            openColorPicker(drawingView, brushColor, drawingView.getColor())
        }

        btnSaveImg.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //Q 버전 이상일 경우. (안드로이드 10, API 29 이상일 경우)
                //saveImageOnAboveAndroidQ(bitmap)
                saveBitmapToGallery(this, drawingView.getBitmapFromPaths(), "Test", "test")
                Toast.makeText(baseContext, "이미지 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // Q 버전 이하일 경우. 저장소 권한을 얻어온다.
                val writePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                if(writePermission == PackageManager.PERMISSION_GRANTED) {
                    //saveImageOnUnderAndroidQ(bitmap)
                    Toast.makeText(baseContext, "이미지 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    val requestExternalStorageCode = 1

                    val permissionStorage = arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                    ActivityCompat.requestPermissions(this, permissionStorage, requestExternalStorageCode)
                }
            }
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

                return true
            }
            // 필요에 따라 추가 메뉴 아이템 처리
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        saveBitmapToCashe(drawingView.getBitmapFromPaths(), getCurrentDateTime(), this)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
        intent.putExtra("drawing_memo_title", memo_title)
        intent.putExtra("drawing_memo_date", getCurrentDateTime())

        startActivity(intent)
        finish()
    }

    private fun isTouchOutsideView(event: MotionEvent, view: View): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]
        return !(event.x > x && event.x < x + view.width && event.y > y && event.y < y + view.height)
    }

    private fun openColorPicker(dV : DrawingView, brushColor : Button, defaultColor : Int) {
        val awd = AmbilWarnaDialog(
            this,
            defaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog) {
                    // onCancel 내용
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    dV.setColor(color)
                    brushColor.backgroundTintList = ColorStateList.valueOf(color)
                }
            }
        )
        awd.show()
    }

    fun setBasicColor(color : String, brushColor : Button){
        drawingView.setColor(Color.parseColor(color))
        brushColor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
    }

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String, description: String) {
        // Get the directory for the user's public pictures directory.
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(path, "$title.jpg")

        try {
            // Save the bitmap to the file
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.close()

            // Notify the media scanner
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.toString()),
                arrayOf("image/jpeg"),
                null
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun saveBitmapToCashe(bitmap: Bitmap, name : String, context: Context){
        val storage: File = context.cacheDir
        val fileName = "$name.jpg"
        val tempFile = File(storage, fileName)
        try {
            // 자동으로 빈 파일을 생성합니다.
            tempFile.createNewFile()

            // 파일을 쓸 수 있는 스트림을 준비합니다.
            val out = FileOutputStream(tempFile)

            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            Log.e("이미지저장됨", "이미지저장됨 : ${fileName}")

            // 스트림 사용 후 닫아줍니다.
            out.close()

        } catch (e: FileNotFoundException) {
            Log.e("MyTag", "FileNotFoundException : ${e.message}")
        } catch (e: IOException) {
            Log.e("MyTag", "IOException : ${e.message}")
        }
    }


}
