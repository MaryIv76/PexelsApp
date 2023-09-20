package com.ivanova.pexelsapp.Model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PhotosRepository {
    companion object {
        suspend fun getCuratedPhotos(number: Int): Flow<Photos> {
            return flow {
                emit(RetrofitInstance.photosApi.getCuratedPhotos(number))
            }.flowOn(Dispatchers.IO)
                .catch { exception -> println("!!!Exception: " + exception.message) }
        }

        suspend fun getPhotosBySearch(request: String, number: Int): Flow<PhotosSearch> {
            return flow {
                emit(RetrofitInstance.photosApi.getPhotosBySearch(request, number))
            }.flowOn(Dispatchers.IO)
                .catch { exception -> println("!!!Exception: " + exception.message) }
        }
    }
}