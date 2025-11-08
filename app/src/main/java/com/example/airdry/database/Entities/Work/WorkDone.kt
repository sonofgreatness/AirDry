package com.example.airdry.database.Entities.Work

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.UUID

@Entity(
    tableName = "work_done",
    indices = [Index(value = ["work_date"])]
)
data class WorkDone(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "work_date")
    val workDate: Timestamp = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name = "is_pending_update")
    val isPendingUpdate: Boolean =false,
    @ColumnInfo(name = "is_pending_insert")
    val isPendingInsert : Boolean = false,
    @ColumnInfo(name = "captured_at")
    val  capturedAt : Timestamp?=null,
    @ColumnInfo(name = "updated_at")
    val updatedAt : Timestamp?=null,
    @ColumnInfo(name = "captured_by")
    val  capturedBy: UUID?=null,
    @ColumnInfo(name = "updated_by")
    val updatedBy: UUID?=null)