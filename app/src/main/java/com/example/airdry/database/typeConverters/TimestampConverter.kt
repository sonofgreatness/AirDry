package com.example.airdry.database.typeConverters

import androidx.room.TypeConverter
import java.sql.Timestamp

class TimestampConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it) }
    }

    @TypeConverter
    fun toTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.time
    }
}
