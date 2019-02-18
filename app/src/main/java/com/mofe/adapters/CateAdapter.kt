package com.mofe.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mofe.R
import com.mofe.database.entities.Cate
import kotlinx.android.synthetic.main.row_cate.view.*

/**
 * Created by ${cosmic} on 2/10/19.
 */

class CateAdapter(private val mContext: Context, private val mData: MutableList<Cate>) : BaseAdapter() {

    fun addAll(customers: List<Cate>) {
        mData.clear()
        mData.addAll(customers)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(position: Int): Any {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val viewHolder: ViewHolder
        if (view != null) {

            viewHolder = view.tag as ViewHolder

        } else {

            view = LayoutInflater.from(mContext).inflate(R.layout.row_cate, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }

        val cate = mData[position]
        viewHolder.title.text = cate.cate

        return view!!
    }

    internal class ViewHolder(view: View) {

        var title: TextView

        init {
            title = view.cate_title
        }
    }

}
