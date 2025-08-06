package com.example.airdry.database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.UUID


@Entity(tableName = "stack")
data class Stack(
    @PrimaryKey val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "breadth")
    val breadth: Float,

    @ColumnInfo(name = "width")
    val width: Float,

    @ColumnInfo(name = "height")
    val height: Float,

    @ColumnInfo(name = "number_of_pieces")
    val numberOfPieces: Int,

    @ColumnInfo(name = "date_created")
    val dateCreated: Timestamp = Timestamp(System.currentTimeMillis())
)
