package com.ivanova.pexelsapp.Model

import com.google.gson.annotations.SerializedName

data class FeaturedCollections(
    val collections: List<FeaturedCollection>,
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("next_page")
    val nextPage: String,
    @SerializedName("prev_page")
    val prevPage: String,
)
