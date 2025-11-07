package com.example.airdry.database.Entities.Stack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "stack_code",
    indices = [Index(value = ["display_code"], unique = true)]
)
data class StackCode(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "display_code")
    val displayCode: String,
    @ColumnInfo(name = "display_name")
    val displayName: String
)