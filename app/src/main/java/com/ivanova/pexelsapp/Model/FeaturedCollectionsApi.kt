package com.ivanova.pexelsapp.Model

import com.ivanova.pexelsapp.Utils.Constants.HEADER
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface FeaturedCollectionsApi {

    @Headers(HEADER)
    @GET("collections/featured")
    suspend fun getFeaturedCollections(@Query("per_page") number: Int): FeaturedCollections
}