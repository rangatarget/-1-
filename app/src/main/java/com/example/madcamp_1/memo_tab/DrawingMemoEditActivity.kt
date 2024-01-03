package com.example.madcamp_1.memo_tab

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.madcamp_1.MainActivity
import com.example.madcamp_1.R
import com.example.madcamp_1.databinding.ActivityDrawingMemoEditBinding
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DrawingMemoEditActivity : AppCompatActivity() {

    private var isBrushMenuOn = false
    private var isEraserMenuOn = false
    lateinit var drawingView: DrawingView

    lateinit var memo_title : String
    var drawingMemoTitle : String? = null
    var drawingMemoDate: String? = null

    lateinit var container : FrameLayout

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityDrawingMemoEditBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        drawingView = binding.drawingView

        container = binding.containers

        memo_title = "(제목 없음)"

        binding.titleEdit.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                memo_title = binding.titleEdit.text.toString()
            }
        }

        drawingMemoTitle = intent.getStringExtra("drawing_memo_title")
        drawingMemoDate = intent.getStringExtra("drawing_memo_date")

        val test = intent.getStringExtra("text").toString()

        Log.d("textMemoTitle", "$drawingMemoTitle")
        Log.d("textMemoDate", "$drawingMemoDate")
        if (drawingMemoTitle!=null&&drawingMemoDate!=null){
            binding.drawingBg.setImageBitmap(loadMemoModelFromInternalStorage(drawingMemoTitle!!,
                drawingMemoDate!!
            ))
            memo_title = drawingMemoTitle as String
            binding.titleEdit.text = Editable.Factory.getInstance().newEditable(drawingMemoTitle)

        } else {
            memo_title = "(제목 없음)"
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        binding.backButton.setOnClickListener {
            if (drawingMemoTitle!=null&&drawingMemoDate!=null) {
                deleteMemoModelFromInternalStorage(drawingMemoTitle!!, drawingMemoDate!!)
            }
            val bitmap = combineViewsToBitmap(container)
            saveMemoToInternalStorage(bitmap)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
            intent.putExtra("drawing_memo_title", memo_title)
            intent.putExtra("drawing_memo_date", getCurrentDateTime())
            startActivity(intent)
            finish()
        }

        val btnBrush = binding.btnBrush
        val btnEraser = binding.btnEraser
        val btnBack = binding.btnBack
        val btnForward = binding.btnForward
        val btnSaveImg = binding.btnSaveImg
        val brushColor = binding.brushColor

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
            drawingView.setColor(brushColor.backgroundTintList?.defaultColor ?: 0)

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
                drawingView.setLastestColor(drawingView.getColor())
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
            val bitmap = combineViewsToBitmap(container)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //Q 버전 이상일 경우. (안드로이드 10, API 29 이상일 경우)
                saveImageOnAboveAndroidQ(bitmap)
                Toast.makeText(baseContext, "이미지 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // Q 버전 이하일 경우. 저장소 권한을 얻어온다.
                val writePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                if(writePermission == PackageManager.PERMISSION_GRANTED) {
                    saveImageOnUnderAndroidQ(bitmap)
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
        }

    }

    override fun onBackPressed() {
        val titleEditText = findViewById<EditText>(R.id.title_edit)
        // EditText가 포커스 상태인지 확인하고 포커스가 있을 때만 clearFocus() 호출
        if (titleEditText.hasFocus()) {
            titleEditText.clearFocus()
        } else {
            super.onBackPressed()

            if (drawingMemoTitle!=null&&drawingMemoDate!=null) {
                deleteMemoModelFromInternalStorage(drawingMemoTitle!!, drawingMemoDate!!)
            }

            val bitmap = combineViewsToBitmap(container)
            saveMemoToInternalStorage(bitmap)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(MainActivity.FRAGMENT_TO_SHOW, MainActivity.FRAGMENT_THIRD)
            intent.putExtra("drawing_memo_title", memo_title)
            intent.putExtra("drawing_memo_date", getCurrentDateTime())
            startActivity(intent)
            finish()
        }
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


    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
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

    fun combineViewsToBitmap(frameLayout: FrameLayout): Bitmap {
        // Get the dimensions of the FrameLayout
        val width = frameLayout.width
        val height = frameLayout.height

        // Create a Bitmap with the dimensions of the FrameLayout
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a Canvas using the Bitmap
        val canvas = Canvas(bitmap)

        // Draw the FrameLayout onto the Canvas
        frameLayout.draw(canvas)

        return bitmap
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
