package com.ivanova.pexelsapp.ViewModel

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanova.pexelsapp.Model.Database.Database
import com.ivanova.pexelsapp.Model.Database.PhotoEntity
import com.ivanova.pexelsapp.Model.Exceptions.NoConnectivityException
import com.ivanova.pexelsapp.Model.Network.FeaturedCollectionsRepository
import com.ivanova.pexelsapp.Model.Network.Photo
import com.ivanova.pexelsapp.Model.Network.PhotosRepository
import com.ivanova.pexelsapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL

class MainViewModel : ViewModel() {

    private val FEATURED_COLLECTIONS_NUMBER = 7
    private val CURATED_PHOTOS_NUMBER = 30
    private val SEARCH_PHOTOS_NUMBER = 30

    private val noInternetConnectionLiveMutable = MutableLiveData(false)
    val noInternetConnectionLive: LiveData<Boolean> = noInternetConnectionLiveMutable

    private val errorRequestLiveMutable = MutableLiveData(false)
    val errorRequestLive: LiveData<Boolean> = errorRequestLiveMutable

    private val someNetworkErrorLiveMutable = MutableLiveData(false)
    val someNetworkErrorLive: LiveData<Boolean> = someNetworkErrorLiveMutable

    private val currentSearchRequestLiveMutable = MutableLiveData<String>()
    val currentSearchRequestLive: LiveData<String> = currentSearchRequestLiveMutable

    private val titlesLiveMutable = MutableLiveData<ArrayList<String>>()
    val titlesLive: LiveData<ArrayList<String>> = titlesLiveMutable

    private val photosLiveMutable = MutableLiveData<ArrayList<Photo>>()
    val photosLive: LiveData<ArrayList<Photo>> = photosLiveMutable

    private val photoDetailsLiveMutable = MutableLiveData<Photo>()
    val photoDetailsLive: LiveData<Photo> = photoDetailsLiveMutable

    private val isInBookmarksLiveMutable = MutableLiveData<Boolean>()
    val isInBookmarksLive: LiveData<Boolean> = isInBookmarksLiveMutable

    private val allPhotosFromBookmarksLiveMutable = MutableLiveData<ArrayList<PhotoEntity>>()
    val allPhotosFromBookmarksLive: LiveData<ArrayList<PhotoEntity>> =
        allPhotosFromBookmarksLiveMutable

    private val photoBookmarksLiveMutable = MutableLiveData<PhotoEntity>()
    val photoBookmarksLive: LiveData<PhotoEntity> = photoBookmarksLiveMutable

    private val photoUrlLiveMutable = MutableLiveData<String>()
    val photoUrlLive: LiveData<String> = photoUrlLiveMutable

    fun loadTitles() {
        viewModelScope.launch {
            FeaturedCollectionsRepository.getFeaturedCollections(FEATURED_COLLECTIONS_NUMBER)
                .catch { exception ->
                    handleExceptions(exception as Exception)
                }
                .collect { featuredCollections ->
                    val titles: ArrayList<String> = arrayListOf()
                    for (featuredCollection in featuredCollections.collections) {
                        titles.add(featuredCollection.title)
                    }
                    titlesLiveMutable.postValue(titles)

                    noInternetConnectionLiveMutable.postValue(false)
                    errorRequestLiveMutable.postValue(false)
                    someNetworkErrorLiveMutable.postValue(false)
                }
        }
    }

    fun loadCuratedPhotos() {
        viewModelScope.launch {
            PhotosRepository.getCuratedPhotos(CURATED_PHOTOS_NUMBER)
                .catch { exception ->
                    handleExceptions(exception as Exception)
                }
                .collect { photos ->
                    photosLiveMutable.postValue(photos.photos as ArrayList<Photo>?)

                    noInternetConnectionLiveMutable.postValue(false)
                    errorRequestLiveMutable.postValue(false)
                    someNetworkErrorLiveMutable.postValue(false)
                }
        }
    }

    fun findPhotos(request: String) {
        currentSearchRequestLiveMutable.postValue(request)

        viewModelScope.launch {
            PhotosRepository.getPhotosBySearch(request, SEARCH_PHOTOS_NUMBER)
                .catch { exception ->
                    handleExceptions(exception as Exception)
                }
                .collect { photos ->
                    photosLiveMutable.postValue(photos.photos as ArrayList<Photo>?)

                    noInternetConnectionLiveMutable.postValue(false)
                    errorRequestLiveMutable.postValue(false)
                    someNetworkErrorLiveMutable.postValue(false)
                }
        }
    }

    fun loadPhotoById(photoId: Int) {
        viewModelScope.launch {
            PhotosRepository.getPhotoById(photoId)
                .catch { exception ->
                    handleExceptions(exception as Exception)
                }
                .collect { photo ->
                    photoDetailsLiveMutable.postValue(photo)
                }
        }
    }

    fun loadPhotoUrlById(photoId: Int) {
        viewModelScope.launch {
            PhotosRepository.getPhotoById(photoId)
                .catch { exception ->
                    handleExceptions(exception as Exception)
                }
                .collect { photo ->
                    photoUrlLiveMutable.postValue(photo.src.original)
                }
        }
    }

    private fun handleExceptions(exception: Exception) {
        when (exception) {
            is NoConnectivityException -> {
                noInternetConnectionLiveMutable.postValue(true)
                Log.d(TAG, "NoConnectivityException: " + exception.message)
            }
            is HttpException -> {
                errorRequestLiveMutable.postValue(true)
                Log.d(TAG, "HttpException: " + exception.message)
            }
            else -> {
                someNetworkErrorLiveMutable.postValue(true)
                Log.d(TAG, "Exception: " + exception.message)
            }
        }
    }

    fun savePhotoToBookmarks(context: Context, photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = URL(photo.src.original)
            val imageData = url.readBytes()
            val bitmap: Bitmap =
                BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

            val photoPath =
                savePhotoToAppDir(context, photo.id.toString() + ".jpeg", bitmap)
            Database.photoDao.insertPhoto(PhotoEntity(photo.id, photo.photographer, photoPath))

            isInBookmarksLiveMutable.postValue(true)
        }
    }

    fun deletePhotoFromBookmarks(context: Context, photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) {
            var photoPath = context.filesDir.absolutePath + "/images" + photo.photographer + ".jpeg"
            Database.photoDao.deletePhoto(PhotoEntity(photo.id, photo.photographer, photoPath))

            isInBookmarksLiveMutable.postValue(false)
        }
    }

    fun isPhotoInBookmarks(photo: Photo) {
        viewModelScope.launch(Dispatchers.IO) {
            val photo = Database.photoDao.getPhotoById(photo.id)
            if (photo == null) {
                isInBookmarksLiveMutable.postValue(false)
            } else {
                isInBookmarksLiveMutable.postValue(true)
            }
        }
    }

    fun loadAllPhotosFromBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            val photos = Database.photoDao.getAllPhotos()
            allPhotosFromBookmarksLiveMutable.postValue(photos as ArrayList<PhotoEntity>?)
        }
    }

    fun loadPhotoByIdFromBookmarks(photoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val photo = Database.photoDao.getPhotoById(photoId)
            photoBookmarksLiveMutable.postValue(photo)
        }
    }

    private fun savePhotoToAppDir(context: Context, imageFile: String, bitmap: Bitmap): String {
        try {
            var appImagesDirPath = context.filesDir.absolutePath + "/images"

            val appImagesDir = File(appImagesDirPath)
            if (!appImagesDir.exists()) {
                appImagesDir.mkdirs()
            }

            val filePath = File(
                appImagesDirPath,
                imageFile
            )
            val outputStream: OutputStream = FileOutputStream(filePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return appImagesDirPath + "/" + imageFile

        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}