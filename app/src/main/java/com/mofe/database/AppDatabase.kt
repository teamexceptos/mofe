package com.mofe.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.mofe.database.daos.CateDao
import com.mofe.database.daos.ItemsDao
import com.mofe.database.entities.Cate
import com.mofe.database.entities.Items

@Database(entities = [(Cate::class), (Items::class)], version = 5, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun CateDao(): CateDao

    abstract fun ItemsDao(): ItemsDao

    companion object {

        /**
         * The only instance
         */

        private var sInstance: AppDatabase? = null

        /**
         * Gets the singleton instance of SampleDatabase.
         *
         * @param context The context.
         * @return The singleton instance of SampleDatabase.
         */

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                sInstance = Room
                        .databaseBuilder(context.applicationContext, AppDatabase::class.java, "toget")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
            }

            return sInstance!!
        }
    }

}
