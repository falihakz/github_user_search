package com.example.cermatitestapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cermatitestapp.BuildConfig
import com.example.cermatitestapp.database.dao.UserSearchResultDao
import com.example.cermatitestapp.database.entities.UserSearchResult
import java.io.File

@Database(entities = [UserSearchResult::class], version = 1)
abstract class AppDatabase :RoomDatabase() {

    abstract fun userSearchResultDao(): UserSearchResultDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DB_NAME = BuildConfig.APPLICATION_ID + "_database"
        private var DB_PATH = ""

        internal fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {

                        if(BuildConfig.DEBUG){
                            // Debug app should use external file
                            var file: File? = null
                            try {
                                file = context.getExternalFilesDir(null)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            if (file != null) {
                                DB_PATH = file.absolutePath + "/databases/"
                            }
                        }

                        INSTANCE = Room.databaseBuilder<AppDatabase>(
                            context.applicationContext,
                            AppDatabase::class.java, DB_PATH + DB_NAME
                        )
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}