package com.example.airdry.database.Entities.Stack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.airdry.database.Entities.Stack.StackCode
import java.util.UUID

@Entity(
    tableName = "stack",
    foreignKeys = [
        ForeignKey(
            entity = StackCode::class,
            parentColumns = ["id"],
            childColumns = ["stack_code_id"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["stack_code_id"], unique = true),
        Index(value = ["volume"])
    ]
)
data class Stack(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "length") val length: Float,
    @ColumnInfo(name = "height") val height: Float,
    @ColumnInfo(name = "thickness") val thickness: Float,
    @ColumnInfo(name = "number_of_pieces") val numberOfPieces: Int = 1,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "stack_code_id") val stackCodeId: UUID,
    @ColumnInfo(name = "volume") val volume: Double? = null // computed in DB
)