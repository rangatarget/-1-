package com.example.madcamp_1

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyContactAdapter(val itemList : ArrayList<ContactModel>) :
    RecyclerView.Adapter<MyContactAdapter.BoardViewHolder>(){
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

        init {
            itemView.setOnClickListener{
                if (contactClick.visibility == View.VISIBLE){
                    collapseView(contactClick)
                }
                else if (contactClick.visibility == View.GONE){
                    expandView(contactClick)
                }
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