package com.example.airdry.database.Entities.UserManagement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import java.sql.Timestamp
import java.util.UUID

@Entity(tableName = "section",
    foreignKeys = [
        ForeignKey(
            entity = Department::class,
            parentColumns = ["id"],
            childColumns = ["department_id"],
            onDelete = ForeignKey.Companion.RESTRICT
        )],
    indices = [Index("department_id")]
)
data class Section (
    @PrimaryKey
    val id : UUID,
    @ColumnInfo("displayname")
    val displayName: String,
    @ColumnInfo(name = "department_id")
    val departmentId : UUID,
    @ColumnInfo(name = "created_at")
    val createdAt : Timestamp = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name = "updated_at")
    val updatedAt : Timestamp = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name ="created_by")
    val createdBy : UUID,
    @ColumnInfo("updated_by")
    val updatedBy: UUID
)