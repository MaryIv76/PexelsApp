package com.ivanova.pexelsapp.View.RecyclerViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ivanova.pexelsapp.R

class PhotosRecyclerViewAdapter(private val context: Context, private val photos: List<String>) :
    RecyclerView.Adapter<PhotosRecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item_home_screen, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context)
            .load(photos[position])
            .into(holder.imViewPhoto)
        //holder.imViewPhoto.setImageResource(photos[position])
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imViewPhoto: ImageView = itemView.findViewById(R.id.imView_photoHome)
    }
}