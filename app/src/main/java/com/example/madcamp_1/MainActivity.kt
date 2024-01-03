package com.example.madcamp_1

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.madcamp_1.databinding.ActivityMainBinding
import com.example.madcamp_1.memo_tab.ThirdFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var drawingMemoTitle : String
    lateinit var drawingMemoDate: String

    private var firstFragment : FirstFragment? = null
    private var secondFragment : SecondFragment? = null
    private var thirdFragment : ThirdFragment? = null

    companion object {
        const val FRAGMENT_TO_SHOW = "fragment_to_show"
        const val FRAGMENT_FIRST = "fragment_first"
        const val FRAGMENT_SECOND = "fragment_second"
        const val FRAGMENT_THIRD = "fragment_third"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingMemoTitle = intent.getStringExtra("drawing_memo_title").toString()
        drawingMemoDate = intent.getStringExtra("drawing_memo_date").toString()

        Log.d("test", "${drawingMemoTitle}")
        Log.d("test", "${drawingMemoDate}")




        val fragmentToShow = intent.getStringExtra(FRAGMENT_TO_SHOW)
        //Log.d("test", "${fragmentToShow}")
        //3번째 탭 이동
        if (fragmentToShow == "fragment_third") {
            setFragment(ThirdFragment())
            binding.bottomNavigationview.selectedItemId = R.id.memo
        }
        else {
            // 특정 프래그먼트 요청이 없으면 기본 프래그먼트 설정
            setFragment(FirstFragment())
        }

        binding.bottomNavigationview.setOnItemSelectedListener{item ->
            when(item.itemId){
                R.id.contact -> {
                    if(firstFragment==null){
                        firstFragment = FirstFragment()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.containers, firstFragment!!)
                            .commit()
                    }
                    if(firstFragment!=null) supportFragmentManager.beginTransaction().show(firstFragment!!).commit()
                    if(secondFragment!=null) supportFragmentManager.beginTransaction().hide(secondFragment!!).commit()
                    if(thirdFragment!=null) supportFragmentManager.beginTransaction().hide(thirdFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.gallery -> {
                    Log.v("프래그먼트 변경", "갤러리 탭 누름")
                    if(secondFragment==null){
                        secondFragment = SecondFragment()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.containers, secondFragment!!)
                            .commit()
                    }
                    if(secondFragment!=null) supportFragmentManager.beginTransaction().show(secondFragment!!).commit()
                    if(firstFragment!=null) supportFragmentManager.beginTransaction().hide(firstFragment!!).commit()
                    if(thirdFragment!=null) supportFragmentManager.beginTransaction().hide(thirdFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.memo -> {
                    if(thirdFragment==null){
                        thirdFragment = ThirdFragment()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.containers, thirdFragment!!)
                            .commit()
                    }
                    if(thirdFragment!=null) supportFragmentManager.beginTransaction().show(thirdFragment!!).commit()
                    if(secondFragment!=null) supportFragmentManager.beginTransaction().hide(secondFragment!!).commit()
                    if(firstFragment!=null) supportFragmentManager.beginTransaction().hide(firstFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
            }
            true
        }

    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containers, fragment)
            .commit()
    }


}

