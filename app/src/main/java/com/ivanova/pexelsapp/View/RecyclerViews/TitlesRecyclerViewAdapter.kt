package com.ivanova.pexelsapp.View.RecyclerViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivanova.pexelsapp.R

class TitlesRecyclerViewAdapter() :
    RecyclerView.Adapter<TitlesRecyclerViewAdapter.MyViewHolder>() {

    private var titles: List<String> = listOf()

    fun setTitles(titles: List<String>) {
        this.titles = titles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.title_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvTitle.text = titles[position]
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    }
}