package com.example.airdry.database.Entities

import androidx.room.*
import com.example.airdry.util.Enums
import java.sql.Timestamp
import java.util.*

@Entity(tableName = "stack")
data class Stack(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "length")
    val type: Enums.StackType,
    @ColumnInfo(name = "date_created")
    val dateCreated: Timestamp = Timestamp(System.currentTimeMillis())
)