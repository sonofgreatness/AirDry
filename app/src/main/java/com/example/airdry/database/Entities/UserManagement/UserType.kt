package com.example.airdry.database.Entities.UserManagement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.UUID

@Entity(tableName = "user_type")
data class UserType (
    @PrimaryKey
    val id : UUID,
    @ColumnInfo("displayname")
    val displayName: String,
    @ColumnInfo(name = "created_at")
    val createdAt : Timestamp? = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name = "updated_at")
    val updatedAt : Timestamp? = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name ="created_by")
    val createdBy : UUID?,
    @ColumnInfo("updated_by")
    val updatedBy: UUID?,
)