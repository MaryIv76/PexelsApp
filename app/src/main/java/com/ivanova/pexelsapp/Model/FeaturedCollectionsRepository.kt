package com.ivanova.pexelsapp.Model

import kotlinx.coroutines.flow.flow
import com.ivanova.pexelsapp.Model.FeaturedCollections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

class FeaturedCollectionsRepository {
    companion object {
        suspend fun getFeaturedCollections(number: Int): Flow<FeaturedCollections> {
            return flow {
                emit(RetrofitInstance.featuredCollectionsApi.getFeaturedCollections(number))
            }.flowOn(Dispatchers.IO)
                .catch { exception -> println("!!!Exception: " + exception.message) }
        }
    }
}