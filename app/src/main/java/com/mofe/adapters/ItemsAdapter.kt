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
import com.mofe.utils.NumberAmountFormat
import kotlinx.android.synthetic.main.row_mofe.view.*
import java.util.*

/**
 * Created by ${cosmic} on 2/11/19.
 */

class ItemsAdapter(val mContext: Context, var mArrayList: ArrayList<Items>) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>(), ItemTouchHelperAdapter {


    val TAG: String = ItemsAdapter::class.java.simpleName
    val Catedatabase = AppDatabase.getInstance(context = mContext).ItemsDao().all

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mView = LayoutInflater.from(mContext).inflate(R.layout.row_mofe, parent, false)

        return ViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val androidColors = mContext.resources.getIntArray(R.array.random_color)
        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
        holder.viewColorTag.setBackgroundColor(randomAndroidColor)

        holder.itemname.text = mArrayList[position].itemName
        holder.itemprice.text = NumberAmountFormat(mArrayList[position].itemPrice!!)
        holder.itemcate.text = mArrayList[position].itemCate
        holder.itemduedate.text = mArrayList[position].itemDueDate
        holder.itemdateadded.text = mArrayList[position].itemDateAdded

    }

    /**
     * Clear list data
     * */
    fun clearAdapter() {
        this.mArrayList.clear()
        notifyDataSetChanged()
    }

    /**
     * Set new data list
     * */
    fun setList(mArrayList: ArrayList<Items>) {
        this.mArrayList = mArrayList
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<Items> {
        return this.mArrayList
    }

    fun deleteTask(position: Int) {
//        dbManager.delete(mArrayList[position].uid)
        mArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mArrayList.size)
    }

    fun finishTask(position: Int) {
//        dbManager.finishTask(mArrayList[position].uid)
        mArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mArrayList.size)
    }

    fun unFinishTask(position: Int) {
//        dbManager.unFinishTask(mArrayList[position].uid)
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

        val viewColorTag = view.viewColorTag!!

        var itemname = view.item_mofe
        var itemprice = view.item_price
        var itemcate = view.item_cate
        var itemduedate = view.item_date_toget
        var itemdateadded = view.input_date
    }
}