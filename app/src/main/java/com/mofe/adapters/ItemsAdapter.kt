package com.mofe.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mofe.R
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.ItemTouchHelperAdapter
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.spentamount
import com.mofe.utils.Togetutil
import kotlinx.android.synthetic.main.item_preview.view.*
import java.util.*

/**
 * Created by ${cosmic} on 2/11/19.
 */

class ItemsAdapter(val mContext: Context,
                   var mArrayList: ArrayList<Items>) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    val CUSTOM_PREF_NAME = "amount_data"

    val Catedatabase = AppDatabase.getInstance(context = mContext).ItemsDao()
    val Prefs = SharedprefManager.customPreference(mContext, CUSTOM_PREF_NAME)
    val togetutil = Togetutil()

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val mView = LayoutInflater.from(mContext).inflate(R.layout.item_preview, parent, false)
        return ViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemname.text = mArrayList[position].itemName
        holder.itemprice.text = togetutil.NumberAmountFormat(mArrayList[position].itemPrice!!)
        holder.itemcate.text = mArrayList[position].itemCate
        holder.itemduedate.text = mArrayList[position].itemDueDate
    }

    /**
     * Clear list data
     * */
    fun clearAdapter() {
        this.mArrayList.clear()
        notifyDataSetChanged()
    }

    fun itemChanged(position: Int) {

        notifyItemChanged(position)
    }

    fun deleteItem(position: Int) {

        if (mArrayList[position].itemGotten == "no") {

            Prefs!!.amount = Prefs.amount + mArrayList[position].itemPrice!!
            Catedatabase.delete(mArrayList[position])

        } else {

            Catedatabase.delete(mArrayList[position])
        }

        mArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mArrayList.size)
    }

    fun gottenItems(position: Int) {

        Prefs!!.spentamount = Prefs.spentamount + mArrayList[position].itemPrice!!
        val items: Items = Catedatabase.findItemById(mArrayList[position].itemUid)[0]
        items.itemGotten = "yes"

        Catedatabase.update(items)

        mArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mArrayList.size)
    }

    override fun onItemDismiss(position: Int) {
        mArrayList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(mArrayList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var itemname = view.preview_text_view
        var itemprice = view.preview_amount_text_view
        var itemcate = view.cate_text_vw
        var itemduedate = view.due_date_to_pay_textvw
    }
}