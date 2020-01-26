package com.mofe.database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.mofe.database.entities.Cate

@Dao
interface CateDao {

    @get:Query("SELECT * FROM cate")
    val all: List<Cate>

    @Query("SELECT * FROM cate WHERE uid IN (:CateIds)")
    fun loadAllByIds(CateIds: IntArray): List<Cate>

    @Query("SELECT * FROM cate WHERE cate LIKE :cate LIMIT 1")
    fun findByName(cate: String): Cate

    @Insert
    fun insertAll(cates: List<Cate>)

    @Delete
    fun delete(cate: Cate)

}
