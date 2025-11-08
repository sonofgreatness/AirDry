package com.example.airdry.database.Entities.Work

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.airdry.database.Entities.Stack.Stack
import java.util.UUID

@Entity(
    tableName = "work_done_stack",
    foreignKeys = [
        ForeignKey(
            entity = WorkDone::class,
            parentColumns = ["id"],
            childColumns = ["work_done_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Stack::class,
            parentColumns = ["id"],
            childColumns = ["stack_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["work_done_id"]),
        Index(value = ["stack_id"])
    ]
)
data class WorkDoneStack(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "work_done_id")
    val workDoneId: UUID,
    @ColumnInfo(name = "stack_id")
    val stackId: UUID)
