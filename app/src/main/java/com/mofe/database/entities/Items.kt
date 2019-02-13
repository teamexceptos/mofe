package com.mofe.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Items constructor(itemName: String,
                        itemPrice: Int,
                        itemCate: String,
                        itemImg: String,
                        itemDateAdded: String,
                        itemDueDate: String) {

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    @ColumnInfo(name = "item_name")
    var itemName: String? = itemName

    @ColumnInfo(name = "item_price")
    var itemPrice: Int? = itemPrice

    @ColumnInfo(name = "item_cate")
    var itemCate: String? = itemCate

    @ColumnInfo(name = "item_img")
    var itemImg: String? = itemImg

    @ColumnInfo(name = "item_date_added")
    var itemDateAdded: String? = itemDateAdded

    @ColumnInfo(name = "item_due_date")
    var itemDueDate: String? = itemDueDate

}