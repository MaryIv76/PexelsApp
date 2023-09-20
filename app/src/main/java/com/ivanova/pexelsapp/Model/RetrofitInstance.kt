package com.ivanova.pexelsapp.Model

import com.ivanova.pexelsapp.Utils.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val photosApi by lazy {
            retrofit.create(PhotosApi::class.java)
        }

        val featuredCollectionsApi by lazy {
            retrofit.create(FeaturedCollectionsApi::class.java)
        }
    }
}