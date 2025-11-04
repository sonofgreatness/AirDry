package com.example.airdry.domain.repository.implementations

import com.example.airdry.database.Daos.ProducKeyDao
import com.example.airdry.database.Entities.ProductKey
import com.example.airdry.domain.repository.interfaces.ProductKeyRepository
import javax.inject.Inject

class ProductKeyRepositoryImpl @Inject constructor(
    val productKeyDao: ProducKeyDao
): ProductKeyRepository {
    override suspend fun createProductKey(entity: ProductKey) =
        productKeyDao.createProductKey(entity)

    override suspend fun getProductKeys() =
        productKeyDao.getProductKeys()

}