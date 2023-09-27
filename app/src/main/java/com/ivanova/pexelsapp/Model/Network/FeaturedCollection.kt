package com.ivanova.pexelsapp.Model.Network

import com.google.gson.annotations.SerializedName

data class FeaturedCollection(
    val id: String,
    val title: String,
    val description: String,
    val private: Boolean,
    @SerializedName("media_count")
    val mediaCount: Int,
    @SerializedName("photos_count")
    val photosCount: Int,
    @SerializedName("videos_count")
    val videosCount: Int
)
