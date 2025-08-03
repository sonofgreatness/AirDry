package com.example.airdry.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
 }
}