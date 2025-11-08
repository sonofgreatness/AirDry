package com.example.airdry.database.Entities.Work

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.airdry.database.Entities.UserManagement.AppUser
import java.util.UUID
@Entity(
    tableName = "work_done_user",
    foreignKeys = [
        ForeignKey(
            entity = WorkDone::class,
            parentColumns = ["id"],
            childColumns = ["work_done_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AppUser::class,
            parentColumns = ["id"],
            childColumns = ["app_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["work_done_id"]),
        Index(value = ["app_user_id"]),
        Index(value = ["work_done_id", "app_user_id"], unique = true)
    ]
)
data class WorkDoneUser(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "work_done_id")
    val workDoneId: UUID,
    @ColumnInfo(name = "app_user_id")
    val appUserId: UUID)

