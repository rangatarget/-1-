package com.example.madcamp_1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(val context: Context, val itemList: ArrayList<ImageModel>) :
    RecyclerView.Adapter<RecyclerAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val element = itemList[position]
        holder.bind(element)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById<ImageView>(R.id.imageView)

        fun bind(item: ImageModel){
            img.setImageBitmap(item.bitmap)
            itemView.setOnClickListener{
                val intent = Intent(context, ImageFullScreen::class.java)
                intent.putExtra("image_index", item.index.toString())
                itemView.context.startActivity(intent)
            }
        }
    }

}