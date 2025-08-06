package com.example.airdry.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.airdry.database.Daos.EmployeeDao
import com.example.airdry.database.Daos.StackDao
import com.example.airdry.database.Daos.WorkDoneDao
import com.example.airdry.database.Entities.Employee
import com.example.airdry.database.Entities.Stack
import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.database.migration.Migrations.Companion.MIGRATION_4_5
import com.example.airdry.database.migration.Migrations.Companion.MIGRATION_5_6
import com.example.airdry.database.typeConverters.EnumConverters
import com.example.airdry.database.typeConverters.TimestampConverter
import com.example.airdry.database.typeConverters.UUIDConverter
import com.example.airdry.util.Constants
import com.example.airdry.util.Enums
import java.util.UUID

@Database
    (
    entities = [Employee::class,
        Stack::class,
        WorkDone::class]
    , version = 1,
    exportSchema = false
)
@TypeConverters(TimestampConverter::class, UUIDConverter::class,
    EnumConverters::class)
abstract class MainDatabase :RoomDatabase() {

    abstract fun getEmployeeDao(): EmployeeDao
    abstract fun getStackDao(): StackDao
    abstract fun getWorkDoneDao(): WorkDoneDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null
        fun getDatabase(context: Context): MainDatabase {
            Log.d("Database", "Database instance created")
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    Constants.LOCAL_DATABASE_NAME

                )

                    .addCallback(
                        callback()
                    )
                    .build()

                INSTANCE = instance
                return instance
            }
        }

    }
}

private fun callback(): RoomDatabase.Callback = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        // Insert initial stack data here
        db.beginTransaction()
        try {
            for (stackType in Enums.StackType.entries) {
                db.execSQL(
                    """
                        INSERT INTO stack (
                            id, type, breadth, width, height, number_of_pieces, date_created
                        ) VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, arrayOf(
                        UUID.randomUUID().toString(),
                        stackType.displayName,
                        stackType.breadth,
                        stackType.width,
                        stackType.height,
                        stackType.numberOfPieces,
                        System.currentTimeMillis().toString()
                    )
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}