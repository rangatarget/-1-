package com.example.madcamp_1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdFragment : Fragment() {
    lateinit private var mgv: MyGraphicView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        title = "그림판"
        mgv = MyGraphicView(this)
        setContentView(mgv)
        return inflater.inflate(R.layout.fragment_third, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menu?.add(0, 1, 2, "선 그리기")
        menu?.add(0, 2, 1, "원 그리기")
        menu?.add(0, 3, 0, "네모 그리기")
        menu?.add(0, 10, 0, "지우기") //지우기 추가 2019-01-25

        val sMenu = menu?.addSubMenu(0, 9, 4, "색상 변경 ==>")

        sMenu?.add(1, 4, 0, "빨강")
        sMenu?.add(1, 5, 0, "파랑")
        sMenu?.add(1, 6, 0, "초록")
        sMenu?.add(2, 7, 1, "선 굵게")
        sMenu?.add(2, 8, 0, "선 가늘게")

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            1 -> {
                curShape = LINE
                return true
            }
            2 -> {
                curShape = CIRCLE
                return true
            }
            3 -> {
                curShape = SQ
                return true
            }
            4 -> {
                color = 1
                eraseBeforColor = 1
                return true
            }
            5 -> {
                color = 2
                eraseBeforColor = 2
                return true
            }
            6 -> {
                color = 3
                eraseBeforColor = 3
                return true
            }
            7 -> {
                size += 5
                return true
            }
            8 -> {
                size -= 5
                return true
            }
            10 -> { //2019-01-25 추가작성
                myShapes.clear() //ArrayList에 저장된 값들을 지워주는 코드
                eraseBeforColor = color //삭제전 색상 저장
                flagErase = true //지우기 누른걸 알림
                color = 4
                mgv.invalidate() //mgv의 onDraw를 호출.
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object { //동반 객체라고 함.(자바의 static과 비슷한 역할)
        val LINE = 1 //선
        val CIRCLE = 2 //원
        val SQ = 3 //네모
        var curShape = LINE  //모양
        var color = 1 //색상(빨,파,노)
        var size = 5 //크기(굵게,가늘게)

        //internal은 자바의 default의 역할
        //생성된 클래스와 패키지에서 사용 가능. 다른 패키지 불가.
        internal var myShapes: MutableList<MyShape> = ArrayList()
        //도형들의 데이터 누적
        internal var eraseBeforColor = 1

        internal var flagErase = false
    }
    private class MyGraphicView(context: Context) : View(context) {
        var startX = -1
        var startY = -1
        var stopX = -1
        var stopY = -1

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            when (event?.action) {//액션은 어떤 터치 동작을 했는지 체크하는 역할
                MotionEvent.ACTION_DOWN -> { //터치 시작(손가락을 가져다 둔 것이 ACTION_DOWN)
                    startX = event.x.toInt()
                    startY = event.y.toInt()

                    if (flagErase) { //20190125 만약에 지우기를 했다면
                        flagErase = false //지우기 하지 않는다으로 바꾸고
                        color = 4 //펜툴 색상을 White로.
                    } else {
                        color = eraseBeforColor
                        //지우기가 아니라면 지우기 전 색상을 가져와서 사용하라.
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    //화면에서 이동할 때 화면에서 손가락 접촉을 멈추었을 때

                    stopX = event.x.toInt()
                    stopY = event.y.toInt()
                    this.invalidate() //명령이 완료되었으니 그리기를 호출(onDraw호출하는 것)

                }
                MotionEvent.ACTION_UP -> {
                    val shape = MyShape() //도형 데이터 1건을 저장시킬 객체생성.
                    shape.shapeType = curShape
                    shape.startX = startX
                    shape.startY = startY
                    shape.stopX = stopX
                    shape.stopY = stopY
                    shape.color = color
                    shape.size = size
                    myShapes.add(shape) // ArrayList에 저장. 도형 누적.

                    this.invalidate()
                }
            }
            return true
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val paint = Paint()
            paint.style = Paint.Style.STROKE

            canvas.drawColor(Color.WHITE) //캔버스 색상 변경 19 01 25

            ////////////////
            if (flagErase) { //지우기 했다면
                color = 4//펜툴을 흰색으로 바꾸고
                flagErase = false //플래그는 삭제 이전으로 토글.
            }
        }

            for (i in myShapes.indices) { //리스트 요소 처리
                val shape2 = myShapes[i]
                //첫 번째 도형을 가져와서 shape2에 담아
                paint.setStrokeWidth(shape2.size.toFloat())
                //각 도형별(첫 번째부터) 사이즈를 가져와서 펜 설정.

                if (shape2.color === 1) {
                    //===이 3개인 것은(=가 3개) 객체 비교시 사용
                    //자바에서는 객체 비교 시 equlas를 썼는데
                    //코틀린에서는 =를 3개 써서 객체 비교(===)
                    paint.color = Color.RED
                } else if (shape2.color === 2) {
                    paint.color = Color.BLUE
                } else if (shape2.color === 3) {
                    paint.color = Color.GREEN
                } else if (shape2.color === 4) {
                    paint.color = Color.WHITE
                }

                when (shape2.shapeType) {
                    LINE -> canvas.drawLine(
                        shape2.startX.toFloat(),
                        shape2.startY.toFloat(),
                        shape2.stopX.toFloat(),
                        shape2.stopY.toFloat(),
                        paint
                    )

                    CIRCLE -> {
                        val radius = Math.sqrt(
                            Math.pow(
                                (shape2.stopX - shape2.startX).toDouble(),
                                2.0
                            ) + Math.pow(
                                (shape2.stopY - shape2.startY).toDouble(),
                                2.0
                            )
                        ).toInt()
                        canvas.drawCircle(
                            shape2.startX.toFloat(),
                            shape2.startY.toFloat(), radius.toFloat(), paint
                        )
                    }//

                    SQ -> canvas.drawRect(
                        shape2.startX.toFloat(),
                        shape2.startY.toFloat(), shape2.stopX.toFloat(),
                        shape2.stopY.toFloat(), paint
                    )
                }
            }
            paint.strokeWidth = size.toFloat()

            if (color === 1) {
                paint.color = Color.RED
            } else if (color === 2) {
                paint.color = Color.BLUE
            } else if (color === 3) {
                paint.color = Color.GREEN
            } else if (color === 4) {
                paint.color = Color.WHITE
            }

            when (curShape) {
                LINE -> canvas?.drawLine(
                startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), paint
                )

                CIRCLE -> {
                    val radius = Math.sqrt(
                        Math.pow(
                            (stopX - startX).toDouble(), 2.0
                        ) +
                                Math.pow((stopY - startY).toDouble(), 2.0)
                    )

                    canvas?.drawCircle(
                        startX.toFloat(), startY.toFloat(),
                        radius.toFloat(), paint
                    )
                }
                SQ -> canvas?.drawRect(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), paint)
            }
        }
    }
}


