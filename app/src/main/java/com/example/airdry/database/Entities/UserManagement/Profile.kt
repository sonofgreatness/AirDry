package com.example.airdry.database.Entities.UserManagement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName =  "user_profile")
data class Profile(
    @PrimaryKey
    val id : UUID,
    val name : String,
    val surname: String,
    val section: String,
    val department: String,
    val  organization: String,
    @ColumnInfo(name = "user_type")
    val userType: String,
    val logo : ByteArray?,
    val  email : String)