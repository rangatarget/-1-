package com.example.madcamp_1

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

data class Phone(val id:String?, val name:String?, val phone:String?)

class FirstFragment : Fragment() {
    val permissions = arrayOf(Manifest.permission.READ_CONTACTS)
    //lateinit var adapter:PhoneAdapter
    //var list = mutableListOf()
    //var searchText = “”
    //var sortText = “asc”

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }


}