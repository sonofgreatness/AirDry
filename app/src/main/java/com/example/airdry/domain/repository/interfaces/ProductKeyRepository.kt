package com.example.airdry.domain.repository.interfaces

import com.example.airdry.database.Entities.ProductKey

interface ProductKeyRepository {

    suspend fun  createProductKey(entity : ProductKey)
    suspend fun getProductKeys() : List<ProductKey>

}