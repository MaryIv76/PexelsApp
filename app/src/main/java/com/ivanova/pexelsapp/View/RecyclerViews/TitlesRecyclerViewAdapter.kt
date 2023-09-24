package com.ivanova.pexelsapp.View.RecyclerViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivanova.pexelsapp.R


class TitlesRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<TitlesRecyclerViewAdapter.MyViewHolder>() {

    private var titles: List<String> = listOf()
    private var currentRequest: String = ""

    var onItemClick: ((String) -> Unit)? = null

    fun setTitles(titles: List<String>) {
        this.titles = titles
        notifyDataSetChanged()
    }

    fun setCurrentRequest(currentRequest: String) {
        this.currentRequest = currentRequest
        notifyItemRangeChanged(0, titles.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.title_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvTitle.text = titles[position]

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(titles[position])
        }

        var holderTitle: String = titles[position].lowercase()
        if (!holder.isActive && holderTitle == currentRequest.lowercase().trim()) {
            holder.tvTitle.setBackgroundResource(R.drawable.active_title_item_bg)
            holder.tvTitle.setTextAppearance(R.style.TitleActiveStyle)
            holder.isActive = true
        }
        if (holder.isActive && holderTitle != currentRequest.lowercase().trim()) {
            holder.tvTitle.setBackgroundResource(R.drawable.inactive_title_item_bg)
            holder.tvTitle.setTextAppearance(R.style.TitleInactiveStyle)
            holder.isActive = false
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        var isActive: Boolean = false
    }
}