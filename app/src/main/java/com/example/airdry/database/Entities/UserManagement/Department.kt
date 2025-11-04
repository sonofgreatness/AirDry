package com.example.airdry.database.Entities.UserManagement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.airdry.database.Entities.UserManagement.Organization
import java.sql.Timestamp
import java.util.UUID
import androidx.room.Index
@Entity(tableName = "department",
    foreignKeys = [
        ForeignKey(
            entity = Organization::class,
            parentColumns = ["id"],
            childColumns = ["organization_id"],
            onDelete = ForeignKey.Companion.RESTRICT
        )],
    indices = [Index("organization_id")])
data class Department(
    @PrimaryKey
    val id : UUID,
    @ColumnInfo("displayname")
    val displayName: String,
    @ColumnInfo(name = "organization_id")
    val organizationId : UUID,
    @ColumnInfo(name = "created_at")
    val createdAt : Timestamp = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name = "updated_at")
    val updatedAt : Timestamp = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name ="created_by")
    val createdBy : UUID,
    @ColumnInfo("updated_by")
    val updatedBy: UUID,
)