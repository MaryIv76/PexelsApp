package com.ivanova.pexelsapp.Model.Network

import com.google.gson.annotations.SerializedName

data class Photos(
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val photos: List<Photo>,
    @SerializedName("next_page")
    val nextPage: String
)
