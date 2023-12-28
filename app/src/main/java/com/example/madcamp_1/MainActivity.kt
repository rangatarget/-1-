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

        val status = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS")
        if (status==PackageManager.PERMISSION_GRANTED){
            Log.d("test", "permission granted")
        } else {
            ActivityCompat.requestPermissions(this, arrayOf<String>("android.permission.READ_CONTACTS"),100)
            Log.d("test", "permission denied")
        }


        val adapter = MyFragmentPagerAdapter(this)
        binding.mainViewpager2.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.mainViewpager2){tab, pos ->
            tab.text="TAB${(pos+1)}"
        }.attach()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Log.d("test", "permission granted")
        } else {
            Log.d("test", "permission denied")
        }

    }

}

