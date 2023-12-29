package com.example.madcamp_1

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp_1.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var firstFragment : FirstFragment? = null
    private var secondFragment : SecondFragment? = null
    private var thirdFragment : ThirdFragment? = null

    class MyFragmentPagerAdapter(activity: FragmentActivity):FragmentStateAdapter(activity){
        val fragments : List<Fragment>
        init {
            fragments = listOf(FirstFragment(), SecondFragment(), ThirdFragment())
        }

        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(FirstFragment())
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
                R.id.drawing -> {
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

