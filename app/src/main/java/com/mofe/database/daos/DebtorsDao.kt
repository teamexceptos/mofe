package com.mofe.database.daos

import android.arch.persistence.room.*
import com.mofe.database.entities.Debtors
import com.mofe.database.entities.Items


/**
 * Created by ${cosmic} on 2/10/19.
 */

@Dao
interface DebtorsDao {

    @get:Query("SELECT * FROM Debtors")
    val all: List<Debtors>

    @Query("SELECT * FROM debtors WHERE debt_status = :debtStatus")
    fun loadAllByDebtStatus(debtStatus: String): List<Debtors>

    @Query("SELECT * FROM debtors WHERE debt_uid = :debtID")
    fun findDebtorById(debtID: Int): List<Debtors>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(debtors: Debtors)

    @Insert
    fun insertAll(items: List<Debtors>)

    @Insert
    fun insert(debtors: Debtors)

    @Delete
    fun delete(debtors: Debtors)

}