package com.example.airdry.database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.airdry.database.Entities.ProductKey

@Dao
interface  ProducKeyDao {

@Insert
suspend fun  createProductKey(entity : ProductKey)
@Query("SELECT * FROM product_key")
suspend fun getProductKeys() : List<ProductKey>

}