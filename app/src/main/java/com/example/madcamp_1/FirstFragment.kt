package com.example.madcamp_1

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcamp_1.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFirstBinding.inflate(inflater, container, false)

        binding.btnGetContact.setOnClickListener {
            if (activity?.let { checkContactsPermission(it) } == true) {
                binding.btnGetContact.visibility = View.GONE
                binding.rcvContact.visibility = View.VISIBLE
                val contactCount = context?.let { getTotalContactCount (it) }
                val cnt = contactCount?.minus(1)
                Log.d("display name", context?.let { getContactDisplayNameByIndex(it, 0) } ?: "Not Found")
                val itemList = ArrayList<String>()
                for(i:Int in 0..cnt!!){
                    itemList.add(context?.let { getContactDisplayNameByIndex(it, i) } ?: "Not Found")
                }
                val adapter = MyContactAdapter(itemList)
                adapter.notifyDataSetChanged()
                binding.rcvContact.adapter = adapter
                binding.rcvContact.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            } else {
                val status = context?.let { it1 -> ContextCompat.checkSelfPermission(it1, "android.permission.READ_CONTACTS") }
                if (status==PackageManager.PERMISSION_GRANTED){
                    Log.d("test", "permission granted")
                } else {
                    ActivityCompat.requestPermissions(context as Activity, arrayOf<String>("android.permission.READ_CONTACTS"),100)
                    Log.d("test", "permission denied")
                }

            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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


    private fun getContactDisplayName(context: Context, phoneNumber: String): String? =
        context.contentResolver.query(
            Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber)),
            arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
            null,
            null,
            null
        )?.run {
            var contactName: String? = null
            moveToFirst()
            while (!isAfterLast) {
                getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME).takeIf { it >= 0 }?.let {
                    contactName = getString(it)
                }
                moveToNext()
            }
            close()
            contactName
        }

    private fun getContactDisplayNameByIndex(context: Context, contactIndex: Int): String? {
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        var contactName: String? = null

        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"

        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            if (cursor.moveToPosition(contactIndex)) {
                val displayNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                if (displayNameIndex >= 0) {
                    contactName = cursor.getString(displayNameIndex)
                }
            }
        }

        return contactName
    }

    private fun getTotalContactCount(context: Context): Int {
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        var totalCount = 0

        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            totalCount = cursor.count
        }

        return totalCount
    }

    fun checkContactsPermission(activity: FragmentActivity): Boolean {
        val permission = Manifest.permission.READ_CONTACTS
        val granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        return granted
    }


}