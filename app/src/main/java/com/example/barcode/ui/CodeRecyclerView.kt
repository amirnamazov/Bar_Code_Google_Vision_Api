package com.example.barcode.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.barcode.R
import com.example.barcode.data.model.Post
import kotlinx.android.synthetic.main.item_layout.view.*

class CodeRecyclerView(private val array:ArrayList<Post>)
    : RecyclerView.Adapter<CodeRecyclerView.CodeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return CodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CodeViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class CodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int){
            itemView.tv_code.text = array[position].body
        }
    }
}