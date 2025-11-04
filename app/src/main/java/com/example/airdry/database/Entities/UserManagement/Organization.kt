package com.example.airdry.database.Entities.UserManagement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "organization")
data class Organization(
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "displayname")
    val displayName:String,
    @ColumnInfo(name = "logo")
    val logo : ByteArray?

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Organization

        if (id != other.id) return false
        if (displayName != other.displayName) return false
        if (!logo.contentEquals(other.logo)) return false

        return true
    }
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + (logo?.contentHashCode() ?: 0)
        return result
    }
}