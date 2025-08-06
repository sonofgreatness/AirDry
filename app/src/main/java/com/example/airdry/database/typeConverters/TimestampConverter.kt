package com.example.airdry.database.typeConverters

import androidx.room.TypeConverter
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimestampConverter {

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @TypeConverter
    fun fromTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it) }
    }

    @TypeConverter
    fun toTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.time
    }

    // String <-> Timestamp converters
    @TypeConverter
    fun fromString(value: String?): Timestamp? {
        return try {
            value?.let {
                val date = formatter.parse(it)
                date?.let { Timestamp(it.time) }
            }
        } catch (e: Exception) {
            Timestamp(System.currentTimeMillis())
        }
    }

    @TypeConverter
    fun toString(timestamp: Timestamp?): String? {
        return timestamp?.let { formatter.format(Date(it.time)) }
    }
}
