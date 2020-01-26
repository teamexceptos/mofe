package com.mofe.database.daos

import android.arch.persistence.room.*
import com.mofe.database.entities.Items


/**
 * Created by ${cosmic} on 2/10/19.
 */

@Dao
interface ItemsDao {

    @get:Query("SELECT * FROM items")
    val all: List<Items>

    @Query("SELECT * FROM items WHERE item_is_gotten = :itemisGotten")
    fun loadAllByGotten(itemisGotten: String): List<Items>

    @Query("SELECT * FROM items WHERE item_uid = :itemID")
    fun findItemById(itemID: Int): List<Items>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(items: Items)

    @Insert
    fun insertAll(items: List<Items>)

    @Insert
    fun insert(itmes: Items)

    @Delete
    fun delete(items: Items)

}