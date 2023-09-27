package com.ivanova.pexelsapp.Model.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Query("SELECT * FROM PhotoEntity")
    fun getAllPhotos(): List<PhotoEntity>

    @Query("SELECT * FROM PhotoEntity WHERE id = (:photoId)")
    fun getPhotoById(photoId: Int): PhotoEntity

    @Insert
    fun insertPhoto(photo: PhotoEntity)

    @Delete
    fun deletePhoto(photo: PhotoEntity)

}