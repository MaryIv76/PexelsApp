package com.ivanova.pexelsapp.Model.Network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PhotosRepository {
    companion object {
        suspend fun getPhotoById(id: Int): Flow<Photo> {
            return flow {
                emit(RetrofitInstance.photosApi.getPhotoById(id))
            }.flowOn(Dispatchers.IO)
        }

        suspend fun getCuratedPhotos(number: Int): Flow<Photos> {
            return flow {
                emit(RetrofitInstance.photosApi.getCuratedPhotos(number))
            }.flowOn(Dispatchers.IO)
        }

        suspend fun getPhotosBySearch(request: String, number: Int): Flow<PhotosSearch> {
            return flow {
                emit(RetrofitInstance.photosApi.getPhotosBySearch(request, number))
            }.flowOn(Dispatchers.IO)
        }
    }
}