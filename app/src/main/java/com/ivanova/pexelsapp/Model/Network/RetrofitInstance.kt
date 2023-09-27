package com.ivanova.pexelsapp.Model.Network

import android.content.Context
import com.ivanova.pexelsapp.Utils.Constants.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private lateinit var context: Context

        fun setContext(context: Context) {
            Companion.context = context
        }

        private val okHttpClient by lazy {
            OkHttpClient.Builder().addInterceptor(NetworkConnectionInterceptor(context))
        }

        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
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