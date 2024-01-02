package com.example.madcamp_1

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.ref.WeakReference

class MyMemoAdapter (val itemList : ArrayList<MemoModel>, val activity: MainActivity) :
    RecyclerView.Adapter<MyMemoAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyMemoAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return ViewHolder(view, itemList, activity)
    }

    override fun onBindViewHolder(holder: MyMemoAdapter.ViewHolder, position: Int) {
        holder.title.text = itemList[position].title
        holder.date.text = itemList[position].date

        val resizedBitmap = resizeBitmapWithAspectRatio(itemList[position].thumbnail, 400, 400)
        holder.img.setImageBitmap(resizedBitmap)

    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    class ViewHolder(itemView: View, private val itemList: ArrayList<MemoModel>, private val activity: MainActivity) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val img : ImageView = itemView.findViewById(R.id.thumbnail)

        init {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DrawingMemoShowActivity::class.java)
                intent.putExtra("drawing_memo_title", itemList[position].title)
                intent.putExtra("drawing_memo_date", itemList[position].date)
                activity.finish()
                itemView.context.startActivity(intent)
            }
        }
    }

    private fun resizeBitmapWithAspectRatio(originalBitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }

        return Bitmap.createScaledBitmap(originalBitmap, finalWidth, finalHeight, false)
    }


}