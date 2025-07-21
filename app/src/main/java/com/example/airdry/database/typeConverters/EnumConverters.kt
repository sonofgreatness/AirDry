package com.example.airdry.database.typeConverters

import androidx.room.TypeConverter
import com.example.airdry.util.Enums

class EnumConverters {
    @TypeConverter
    fun fromStackLength(length: Enums.StackLength): String = length.name

    @TypeConverter
    fun toStackLength(name: String): Enums.StackLength = Enums.StackLength.valueOf(name)

    @TypeConverter
    fun fromStackWidth(width: Enums.StackWidth): String = width.name

    @TypeConverter
    fun toStackWidth(name: String): Enums.StackWidth = Enums.StackWidth.valueOf(name)
}
