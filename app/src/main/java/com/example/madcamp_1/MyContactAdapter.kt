package com.example.madcamp_1

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager

class MyContactAdapter(val itemList : ArrayList<ContactModel>) :
    RecyclerView.Adapter<MyContactAdapter.BoardViewHolder>(){

    companion object {
        const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 123
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.number.text = itemList[position].number
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.contact_name)
        val number = itemView.findViewById<TextView>(R.id.contact_number)
        val contactClick = itemView.findViewById<LinearLayout>(R.id.contact_clicked)

        val btnCall = itemView.findViewById<Button>(R.id.btn_call)
        val btnMessage = itemView.findViewById<Button>(R.id.btn_message)

        init {
            itemView.setOnClickListener{
                if (contactClick.visibility == View.VISIBLE){
                    collapseView(contactClick)
                }
                else if (contactClick.visibility == View.GONE){
                    expandView(contactClick)
                }
            }
            
            btnCall.setOnClickListener {
                val phoneNumber = number.text.toString()
                val dialIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))

                if (ActivityCompat.checkSelfPermission(
                        itemView.context,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    itemView.context.startActivity(dialIntent)
                } else {
                    // 필요한 권한 요청
                    ActivityCompat.requestPermissions(
                        itemView.context as Activity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        MY_PERMISSIONS_REQUEST_CALL_PHONE
                    )
                }
                //Toast.makeText(itemView.context, "전화하기 버튼 클릭됨", Toast.LENGTH_SHORT).show()
                Log.d("test", "전화하기 버튼 클릭됨")
            }
            
            btnMessage.setOnClickListener {
                val phoneNumber = number.text.toString()

                val smsUri = Uri.parse("smsto:$phoneNumber")
                val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)

                itemView.context.startActivity(smsIntent)

                //Toast.makeText(itemView.context, "문자하기 버튼 클릭됨", Toast.LENGTH_SHORT).show()
                Log.d("test", "문자하기 버튼 클릭됨")
            }
            
        }

        private fun expandView(view: View) {
            view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val targetHeight = view.measuredHeight

            val initialHeight = view.height
            val animator = ValueAnimator.ofInt(initialHeight, targetHeight)

            animator.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                view.layoutParams.height = value
                view.requestLayout()
            }

            animator.duration = 300
            animator.start()

            view.visibility = View.VISIBLE
        }


        private fun collapseView(view: View) {
            val initialHeight = view.height
            val animator = ValueAnimator.ofInt(initialHeight, 0)

            animator.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                view.layoutParams.height = value
                view.requestLayout()
            }

            animator.duration = 300
            animator.start()

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
        }

    }
}