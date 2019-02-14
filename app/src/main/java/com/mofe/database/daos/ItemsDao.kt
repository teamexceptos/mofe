package com.mofe.database.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.mofe.database.entities.Items

/**
 * Created by ${cosmic} on 2/10/19.
 */

@Dao
interface ItemsDao {

    @get:Query("SELECT * FROM items")
    val all: List<Items>

    @Query("SELECT * FROM items WHERE uid IN (:itemsIds)")
    fun loadAllByIds(itemsIds: Array<Int>): List<Items>

    @Query("SELECT * FROM items WHERE item_name = :item_name")
    fun findByItemId(item_name: String): List<Items>

    @Query("UPDATE items SET item_price = :item_amount WHERE uid =:itemsId")
    fun update(item_amount: Int?, itemsId: Int)

    @Insert
    fun insertAll(items: List<Items>)

    @Insert
    fun insert(itmes: Items)

    @Delete
    fun delete(items: Items)

}