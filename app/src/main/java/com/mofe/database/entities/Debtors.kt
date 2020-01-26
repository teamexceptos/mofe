package com.mofe.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Debtors constructor(@PrimaryKey @ColumnInfo(name = "debt_uid") var personUid: Int = 0,
                          personName: String = "",
                          personContact: String = "",
                          debtAmount: Int = 0,
                          debtPayDate: String = "",
                          debtPayStatus: String = "",
                          debtAddDate: Long = 0,
                          debtAddDateString: String = "") {

    @ColumnInfo(name = "person_name")
    var personName: String? = personName

    @ColumnInfo(name = "person_contact")
    var personContact: String? = personContact

    @ColumnInfo(name = "debt_amount")
    var debtAmount: Int? = debtAmount

    @ColumnInfo(name = "debt_date")
    var duePayDate: String? = debtPayDate

    @ColumnInfo(name = "debt_status")
    var debtPayStatus: String? = debtPayStatus

    @ColumnInfo(name = "debt_long_date_added")
    var debtAddDate: Long? = debtAddDate

}