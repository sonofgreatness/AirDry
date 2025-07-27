package com.example.airdry.util
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId



    fun Timestamp.toLocalDateTime(): LocalDateTime {
        return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }