package com.example.airdry.database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.UUID


@Entity(
    tableName = "work_done",
    foreignKeys = [
        ForeignKey(
            entity = Employee::class,
            parentColumns = ["id"],
            childColumns = ["employee_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Stack::class,
            parentColumns = ["id"],
            childColumns = ["stack_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("employee_id"), Index("stack_id")]
)
data class WorkDone(
    @PrimaryKey val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "employee_id")
    val employeeId: UUID,
    @ColumnInfo(name = "stack_id")
    val stackId: UUID,
    @ColumnInfo(name = "ownership_fraction")
    val ownershipFraction: Float, // e.g. 1.0 = full, 0.5 = half
    @ColumnInfo(name = "date_worked")
    val dateWorked: Timestamp = Timestamp(System.currentTimeMillis()),
    val amount: Double
){

}