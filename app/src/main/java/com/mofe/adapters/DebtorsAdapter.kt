package com.mofe.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mofe.R
import com.mofe.activities.home_activity
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Debtors
import com.mofe.utils.ItemTouchHelperAdapter
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.init_amount
import com.mofe.utils.SharedprefManager.spentamount
import com.mofe.utils.Togetutil
import kotlinx.android.synthetic.main.activity_new_home.*
import kotlinx.android.synthetic.main.debtors_preview.view.*
import java.util.*

/**
 * Created by ${cosmic} on 2/11/19.
 */

class DebtorsAdapter(val mContext: Context,
                     var mArrayList: ArrayList<Debtors>,
                     val mActivity: home_activity) : RecyclerView.Adapter<DebtorsAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    val CUSTOM_PREF_NAME = "amount_data"

    val Debtorsdatabase = AppDatabase.getInstance(context = mContext).DebtorsDao()
    val togetutil = Togetutil()
    val Prefs = SharedprefManager.customPreference(mContext, CUSTOM_PREF_NAME)

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val mView = LayoutInflater.from(mContext).inflate(R.layout.debtors_preview, parent, false)
        return ViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.debtorName.text = mArrayList[position].personName
        holder.debtorOweAmount.text = togetutil.NumberAmountFormat(mArrayList[position].debtAmount!!)
        holder.debtorContact.text = mArrayList[position].personContact
        holder.debtorPaymentDue.text = mArrayList[position].duePayDate

    }

    /**
     * Clear list data
     * */
    fun clearAdapter() {
        this.mArrayList.clear()
        notifyDataSetChanged()
    }

    fun deleteDebtor(position: Int) {

        if (mArrayList[position].debtPayStatus == "no"){
            Prefs!!.amount = Prefs.amount + mArrayList[position].debtAmount!!
            mActivity.amt_reduction.text = togetutil.NumberAmountFormat(Prefs.amount)
            Debtorsdatabase.delete(mArrayList[position])

        } else {
            Debtorsdatabase.delete(mArrayList[position])
        }

        mArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mArrayList.size)
    }

    fun gottenPayment(position: Int) {

        Prefs!!.spentamount = Prefs.spentamount + mArrayList[position].debtAmount!!
        val debtors: Debtors = Debtorsdatabase.findDebtorById(mArrayList[position].personUid)[0]
        debtors.debtPayStatus = "yes"

        Debtorsdatabase.update(debtors)

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

        var debtorName = view.debtor_name_text_view
        var debtorContact = view.debtor_contact_text_view
        var debtorOweAmount = view.debt_amount_text_view
        var debtorPaymentDue = view.due_date_to_pay_textvw
    }
}