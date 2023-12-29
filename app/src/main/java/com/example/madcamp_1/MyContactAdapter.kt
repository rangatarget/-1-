package com.example.madcamp_1

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
                    contactClick.visibility = View.GONE
                }
                else if (contactClick.visibility == View.GONE){
                    contactClick.visibility = View.VISIBLE
                }
            }
        }

    }
}