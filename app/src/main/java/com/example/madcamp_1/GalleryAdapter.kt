package com.example.madcamp_1

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GalleryAdapter : RecyclerView.Adapter<Holder>() {
    var listData = mutableListOf<ListData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = listData.get(position)
        holder.setListData(data)
    }
}

class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var idTextView = itemView.findViewById<TextView>(R.id.id_number)
    var textTitleTextView = itemView.findViewById<TextView>(R.id.text_title)
    fun setListData(listData: ListData) {
        idTextView.text = "${listData.number}"
        textTitleTextView.text = listData.title
    }
}