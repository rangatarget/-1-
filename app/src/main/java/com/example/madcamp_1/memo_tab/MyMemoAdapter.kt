package com.example.madcamp_1.memo_tab

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_1.MainActivity
import com.example.madcamp_1.MyApplication
import com.example.madcamp_1.R

class MyMemoAdapter (val itemList : ArrayList<MemoModel>, val activity: MainActivity) :
    RecyclerView.Adapter<MyMemoAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return ViewHolder(view, itemList, activity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

        var isText : Boolean = false
        lateinit var content : String

        init {
            itemView.setOnClickListener {

                isText = MyApplication.prefs.findText("${itemList[position].title}_${itemList[position].date}")
                content = MyApplication.prefs.getString("${itemList[position].title}_${itemList[position].date}", "default")
                Log.d("test 어댑터에서 찾는 키", "${itemList[position].title}_${itemList[position].date}")
                Log.d("isText 확인", "$isText")
                Log.d("meme content 확인", "$content")

                if (isText){
                    val intent = Intent(itemView.context, TextMemoShowActivity::class.java)
                    intent.putExtra("text_memo_title", itemList[position].title)
                    intent.putExtra("text_memo_date", itemList[position].date)
                    intent.putExtra("text_memo_content", content)
                    activity.finish()
                    itemView.context.startActivity(intent)
                } else {
                    val intent = Intent(itemView.context, DrawingMemoShowActivity::class.java)
                    intent.putExtra("drawing_memo_title", itemList[position].title)
                    intent.putExtra("drawing_memo_date", itemList[position].date)
                    activity.finish()
                    itemView.context.startActivity(intent)
                }
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