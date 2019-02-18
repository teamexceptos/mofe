package com.mofe.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Items constructor(@PrimaryKey @ColumnInfo(name = "item_uid") var itemUid: Int = 0,
                        itemName: String = "",
                        itemPrice: Int = 0,
                        itemCate: String = "",
                        itemImg: String = "",
                        itemDateAdded: String = "",
                        itemLongDateAdded: Long = 0,
                        itemDueDate: String = "",
                        itemisGotten: String = "") {

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

    @ColumnInfo(name = "item_long_date_added")
    var itemLongDateAdded: Long? = itemLongDateAdded

    @ColumnInfo(name = "item_due_date")
    var itemDueDate: String? = itemDueDate

    @ColumnInfo(name = "item_is_gotten")
    var itemGotten: String? = itemisGotten


}