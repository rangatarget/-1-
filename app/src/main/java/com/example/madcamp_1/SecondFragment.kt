package com.example.madcamp_1

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.madcamp_1.databinding.FragmentFirstBinding
import com.example.madcamp_1.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSecondBinding.inflate(inflater, container, false)

        binding.btnddd.setOnClickListener {

            if (activity?.let { checkGalleryPermission(it) } == true) {
            }
            else{
                Toast.makeText(context,"dddd", Toast.LENGTH_SHORT).show()

                val status = context?.let { it1 -> ContextCompat.checkSelfPermission(it1, "android.permission.READ_MEDIA_IMAGES") }
                if (status==PackageManager.PERMISSION_GRANTED){
                    Log.d("test", "permission granted")
                } else {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf<String>("android.permission.READ_MEDIA_IMAGES"),1000)
                    Log.d("test", "permission denied")
                }
            }

        }

        // Inflate the layout for this fragment
        return binding.root
    }
    fun checkGalleryPermission(activity: FragmentActivity): Boolean {
        val permission = Manifest.permission.READ_MEDIA_IMAGES
        val granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        return granted
    }

}