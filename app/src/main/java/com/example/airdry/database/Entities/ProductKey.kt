package com.example.airdry.database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity("product_key")
data class ProductKey(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "keyValue") val keyValue: String

)