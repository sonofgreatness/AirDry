package com.example.airdry.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.airdry.database.Daos.UserManagement.UserManagementDao
import com.example.airdry.database.Daos.ProducKeyDao
import com.example.airdry.database.Daos.StackDao
import com.example.airdry.database.Daos.WorkDoneDao
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.ProductKey
import com.example.airdry.database.Entities.Stack
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.database.Entities.UserManagement.Organization
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.database.Entities.UserManagement.UserType
import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.database.typeConverters.EnumConverters
import com.example.airdry.database.typeConverters.TimestampConverter
import com.example.airdry.database.typeConverters.UUIDConverter
import com.example.airdry.util.Constants
import com.example.airdry.util.Enums
import java.util.UUID

@Database
    (
    entities = [AppUser::class,
        Stack::class,
        WorkDone::class,ProductKey::class,
        Organization::class,
        Department::class,Section::class,
        UserType::class, Profile::class
               ]
    , version = 1,
    exportSchema = false
)
@TypeConverters(TimestampConverter::class, UUIDConverter::class,
    EnumConverters::class)
abstract class MainDatabase :RoomDatabase() {

    abstract fun getEmployeeDao(): UserManagementDao
    abstract fun getStackDao(): StackDao
    abstract fun getWorkDoneDao(): WorkDoneDao
    abstract  fun getProducKeyDao(): ProducKeyDao

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