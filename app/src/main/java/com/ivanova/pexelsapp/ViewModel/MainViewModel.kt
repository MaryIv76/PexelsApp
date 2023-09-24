package com.ivanova.pexelsapp.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanova.pexelsapp.Model.Exceptions.NoConnectivityException
import com.ivanova.pexelsapp.Model.FeaturedCollectionsRepository
import com.ivanova.pexelsapp.Model.PhotosRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel : ViewModel() {

    private val FEATURED_COLLECTIONS_NUMBER = 7
    private val CURATED_PHOTOS_NUMBER = 5
    private val SEARCH_PHOTOS_NUMBER = 5

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

    private val photosLiveMutable = MutableLiveData<ArrayList<String>>()
    val photosLive: LiveData<ArrayList<String>> = photosLiveMutable

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
                    val curatedPhotos: ArrayList<String> = arrayListOf()
                    for (photo in photos.photos) {
                        curatedPhotos.add(photo.src.original)
                    }
                    photosLiveMutable.postValue(curatedPhotos)

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
                    val foundPhotos: ArrayList<String> = arrayListOf()
                    for (photo in photos.photos) {
                        foundPhotos.add(photo.src.original)
                    }
                    photosLiveMutable.postValue(foundPhotos)

                    noInternetConnectionLiveMutable.postValue(false)
                    errorRequestLiveMutable.postValue(false)
                    someNetworkErrorLiveMutable.postValue(false)
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
}