package com.example.airdry.database.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.airdry.util.Enums
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Migrations {

 companion object {
     val MIGRATION_4_5 = object : Migration(4, 5) {
         override fun migrate(database: SupportSQLiteDatabase) {
             // Rename old table
             database.execSQL("ALTER TABLE stack RENAME TO stack_old")

             // Correct CREATE TABLE (with correct types and primary key)
             database.execSQL("""
            CREATE TABLE IF NOT EXISTS stack (
                id TEXT NOT NULL PRIMARY KEY,
                type TEXT NOT NULL,
                date_created TEXT NOT NULL
            )
        """)

             // Copy data from old table (length -> type)
             database.execSQL("""
            INSERT INTO stack (id, type, date_created)
            SELECT id, length, date_created FROM stack_old
        """)

             // Drop old table
             database.execSQL("DROP TABLE stack_old")
         }
     }
     val MIGRATION_5_6 = object : Migration(5, 6) {
         override fun migrate(database: SupportSQLiteDatabase) {
             val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

             // 1. Rename old table
             database.execSQL("ALTER TABLE stack RENAME TO stack_old")

             // 2. Create new table
             database.execSQL("""
            CREATE TABLE IF NOT EXISTS stack (
                id TEXT NOT NULL PRIMARY KEY,
                type TEXT NOT NULL,
                breadth REAL NOT NULL,
                width REAL NOT NULL,
                height REAL NOT NULL,
                number_of_pieces INTEGER NOT NULL,
                date_created TEXT NOT NULL
            )
        """.trimIndent())

             // 3. Read old data and insert into new table
             val cursor = database.query("SELECT id, type, date_created FROM stack_old")

             while (cursor.moveToNext()) {
                 val id = cursor.getString(0)
                 val oldTypeDisplayName = cursor.getString(1)
                 val dateCreated = cursor.getLong(2) // old timestamp in millis

                 val stackType = try {
                     Enums.StackType.fromDisplayName(oldTypeDisplayName)
                 } catch (e: IllegalArgumentException) {
                     null
                 }

                 if (stackType != null) {
                     val formattedDate = formatter.format(Date(dateCreated))
                     database.execSQL("""
                    INSERT INTO stack (
                        id, type, breadth, width, height, number_of_pieces, date_created
                    ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """, arrayOf(
                         id,
                         stackType.displayName,
                         stackType.breadth,
                         stackType.width,
                         stackType.height,
                         stackType.numberOfPieces,
                         formattedDate // ✅ proper formatted string
                     ))
                 }
             }
             cursor.close()

             // 4. Drop old table
             database.execSQL("DROP TABLE stack_old")
         }
     }

 }
}