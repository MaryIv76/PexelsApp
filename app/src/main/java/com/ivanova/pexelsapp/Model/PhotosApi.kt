package com.ivanova.pexelsapp.Model

import com.ivanova.pexelsapp.Utils.Constants.HEADER
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotosApi {

    @Headers(HEADER)
    @GET("photos/{id}")
    suspend fun getPhotoById(@Path("id") id: Int): Photo

    @Headers(HEADER)
    @GET("curated")
    suspend fun getCuratedPhotos(@Query("per_page") number: Int): Photos
}