package com.ivanova.pexelsapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanova.pexelsapp.Model.FeaturedCollectionsRepository
import com.ivanova.pexelsapp.Model.PhotosRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val FEATURED_COLLECTIONS_NUMBER = 7
    private val CURATED_PHOTOS_NUMBER = 5
    private val SEARCH_PHOTOS_NUMBER = 5

    private val currentSearchRequestLiveMutable = MutableLiveData<String>()
    val currentSearchRequestLive: LiveData<String> = currentSearchRequestLiveMutable

    private val titlesLiveMutable = MutableLiveData<ArrayList<String>>()
    val titlesLive: LiveData<ArrayList<String>> = titlesLiveMutable

    private val photosLiveMutable = MutableLiveData<ArrayList<String>>()
    val photosLive: LiveData<ArrayList<String>> = photosLiveMutable

    fun loadTitles() {
        viewModelScope.launch {
            FeaturedCollectionsRepository.getFeaturedCollections(FEATURED_COLLECTIONS_NUMBER)
                .collect { featuredCollections ->
                    val titles: ArrayList<String> = arrayListOf()
                    for (featuredCollection in featuredCollections.collections) {
                        titles.add(featuredCollection.title)
                    }
                    titlesLiveMutable.postValue(titles)
                }
        }
    }

    fun loadCuratedPhotos() {
        viewModelScope.launch {
            PhotosRepository.getCuratedPhotos(CURATED_PHOTOS_NUMBER)
                .collect { photos ->
                    val curatedPhotos: ArrayList<String> = arrayListOf()
                    for (photo in photos.photos) {
                        curatedPhotos.add(photo.src.original)
                    }
                    photosLiveMutable.postValue(curatedPhotos)
                }
        }
    }

    fun findPhotos(request: String) {
        currentSearchRequestLiveMutable.postValue(request)

        viewModelScope.launch {
            PhotosRepository.getPhotosBySearch(request, SEARCH_PHOTOS_NUMBER)
                .collect { photos ->
                    val foundPhotos: ArrayList<String> = arrayListOf()
                    for (photo in photos.photos) {
                        foundPhotos.add(photo.src.original)
                    }
                    photosLiveMutable.postValue(foundPhotos)
                }
        }
    }
}