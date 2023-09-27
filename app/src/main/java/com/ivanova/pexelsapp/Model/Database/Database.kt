package com.ivanova.pexelsapp.Model.Database

import android.content.Context
import androidx.room.Room


class Database {
    companion object {
        private lateinit var applicationContext: Context

        fun setApplicationContext(applicationContext: Context) {
            Database.applicationContext = applicationContext
        }

        private val db by lazy {
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "PexelsDB"
            ).build()
        }

        val photoDao by lazy {
            db.photoDao()
        }
    }
}