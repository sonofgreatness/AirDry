package com.example.airdry.database.typeConverters

import androidx.room.TypeConverter
import com.example.airdry.util.Enums.StackType

class EnumConverters {
    @TypeConverter
    fun fromStackType(type: StackType): String {
        return type.displayName
    }

    @TypeConverter
    fun toStackType(value: String): StackType {
        return StackType.fromDisplayName(value)
    }
}
