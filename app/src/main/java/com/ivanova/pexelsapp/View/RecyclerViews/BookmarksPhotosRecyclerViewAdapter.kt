package com.ivanova.pexelsapp.View.RecyclerViews

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ivanova.pexelsapp.Model.Database.PhotoEntity
import com.ivanova.pexelsapp.Model.Network.Photo
import com.ivanova.pexelsapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class BookmarksPhotosRecyclerViewAdapter(private val context: Context) :
    RecyclerView.Adapter<BookmarksPhotosRecyclerViewAdapter.MyViewHolder>() {

    private var photos: ArrayList<PhotoEntity> = arrayListOf()
    private var counter: Int = 0

    private val isAllItemsVisibleLiveMutable = MutableLiveData<Boolean>(false)
    val isAllItemsVisibleLive: LiveData<Boolean> = isAllItemsVisibleLiveMutable

    var onItemClick: ((Int) -> Unit)? = null

    fun setPhotos(photos: ArrayList<PhotoEntity>) {
        this.photos = photos
        this.counter = 0
        isAllItemsVisibleLiveMutable.postValue(this.photos.size == this.counter)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_item_bookmarks_screen, parent, false)
        return BookmarksPhotosRecyclerViewAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(photos[position].id)
        }

        holder.tvPhotographer.text = photos[position].photographer
        Glide.with(context)
            .load(File(photos[position].photoPath))
            .placeholder(R.drawable.placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e(ContentValues.TAG, "onLoadFailed")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    counter++
                    if (counter == photos.size) {
                        isAllItemsVisibleLiveMutable.postValue(true)
                    }
                    return false
                }
            })
            .into(holder.imViewPhoto)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imViewPhoto: ImageView = itemView.findViewById(R.id.imView_photoBookmarks)
        val tvPhotographer: TextView = itemView.findViewById(R.id.tv_photographerBookmarks)
    }
}