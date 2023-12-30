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
import android.content.Context
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var isFabOpen = false
    private var imagelist = ArrayList<ListItemModel>()
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
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
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

        val adapter = RecyclerAdapter(imagelist)
        adapter.notifyDataSetChanged()
        binding.rcvGallery.adapter = adapter
        binding.rcvGallery.layoutManager = GridLayoutManager(requireContext(), 3)

        // Inflate the layout for this fragment
        return binding.root
    }

    fun checkExtStoPermission(activity: FragmentActivity): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        return granted
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                2000 -> {
                    val uri = data?.data
                    imagelist.add(ListItemModel("image", uri, "description"))

                    val rcvgallery = activity?.findViewById<RecyclerView>(R.id.rcvGallery)
                    val adapter = RecyclerAdapter(imagelist)
                    adapter.notifyDataSetChanged()
                    rcvgallery?.adapter = adapter
                    rcvgallery?.layoutManager = GridLayoutManager(requireContext(), 3)
                }
            }
        }
    }
}