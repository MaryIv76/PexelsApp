package com.ivanova.pexelsapp.Model.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotoEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "photographer") val photographer: String,
    @ColumnInfo(name = "photoPath") val photoPath: String
)
