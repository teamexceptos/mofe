package com.mofe

import android.app.Application
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Cate
import org.jetbrains.anko.doAsync

class RoomApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        doAsync {

            val database = AppDatabase.getInstance(context = this@RoomApplication)

            if (database.CateDao().all.isEmpty()) {

                val cate: MutableList<Cate> = mutableListOf()

                listOf("Food", "Wears", "Gadgets", "Snacks", "Books", "School", "Fun", "Friends", "Tickets").forEach {

                    val cates = Cate(it)
                    cate.add(cates)
                }

                database.CateDao().insertAll(cates = cate)
            }
        }
    }

}
