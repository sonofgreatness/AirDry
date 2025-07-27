package com.example.airdry.database.Entities
import java.sql.Timestamp
import java.util.UUID
import androidx.room.*
@Entity(tableName = "employee")
data class Employee (
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "surname") val surname: String?,
    @ColumnInfo(name = "national_id") val nationalId: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "date_created") val dateCreated: Timestamp = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name = "is_active") val isActive: Boolean = false
){
    override fun toString() : String{

        return "$name  $surname $phoneNumber"
    }
    fun printFullname  (): String{
        return "$name  $surname"

    }
}