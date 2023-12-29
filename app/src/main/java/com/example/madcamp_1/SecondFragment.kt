package com.example.madcamp_1

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.madcamp_1.databinding.FragmentSecondBinding
import android.Manifest
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcamp_1.databinding.FragmentFirstBinding
import android.animation.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val REQUEST_READ_EXTERMAL_STORAGE = 1000
    private var isFabOpen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.button21.setOnClickListener{
            if (activity?.let { checkExtStoPermission(it) } == true) {
                binding.button21.visibility = View.GONE
                Toast.makeText(context, "권한 있음", Toast.LENGTH_SHORT).show()
            } else {
                val status = context?.let { it1 -> ContextCompat.checkSelfPermission(it1, "android.permission.READ_MEDIA_IMAGES") }
                if (status==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context, "권한 생김", Toast.LENGTH_SHORT).show()
                    val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.setType("image/*")
                    startActivityForResult(intent, 2000)
                } else {
                    ActivityCompat.requestPermissions(context as Activity,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        1000)
                    Toast.makeText(context, "권한 안생김", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //삭제버튼
        binding.button22.setOnClickListener {
            Toast.makeText(context, "삭제 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    fun checkExtStoPermission(activity: FragmentActivity): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        return granted
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}