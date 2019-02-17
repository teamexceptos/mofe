package com.mofe.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import com.mofe.R
import com.mofe.adapters.ItemsAdapter
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.*
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.customPreference
import com.mofe.utils.SharedprefManager.init_amount
import com.mofe.utils.SharedprefManager.lastdate
import com.mofe.utils.SharedprefManager.spentamount
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * Created by ${cosmic} on 2/10/19.
 */

open class home_activity : AppCompatActivity() {

    open val CUSTOM_PREF_NAME = "amount_data"
    open val mActivity: Activity = this@home_activity

    private var mAdapter: ItemsAdapter? = null
    val context: Context = this

    lateinit var rvMofe: RecyclerView
    lateinit var rView: RelativeLayout
    var mArrayList: ArrayList<Items> = ArrayList()
    lateinit var database : AppDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val Prefs = customPreference(this, CUSTOM_PREF_NAME);
        database = AppDatabase.getInstance(context = this@home_activity)

        doAsync {

            val items = database.ItemsDao().loadAllByGotten("no")
            sortingforItmes(items)
            mArrayList = items as ArrayList<Items>
            rvMofe = mofe_rv
            rView = no_items_added

            uiThread {

                if(mArrayList.size == 0) {

                    rvMofe.visibility = View.GONE
                    rView.visibility = View.VISIBLE

                } else {

                    rView.visibility = View.GONE
                    mAdapter = ItemsAdapter(this@home_activity, mArrayList,this@home_activity, false)

                    initializeMoferv()

                }
            }
        }

        val date = getCurrentDateTime()
        val datetoString = date.toString("EEE, d MMM yyyy")
        Prefs.lastdate = datetoString
        last_date_update.setText(datetoString)

        amt_reduction.setText(NumberAmountFormat(Prefs.amount))
        init_amt_input.setText(NumberAmountFormat(Prefs.init_amount))

        edit_amt.setOnClickListener {
            dialogUpdate(this, false, null);
        }

        add_item_toget_fab.setOnClickListener {
            startActivity(Intent(mActivity, addItem_activity::class.java))
            finish()
        }

        cp_bar.run {
            setRounded(true)
            setMaxProgress(Prefs.init_amount.toFloat())
            setProgressColor(context.getColor(R.color.lime_progress_100))
            setProgressBackgroundColor(context.getColor(R.color.colorWhite))
            setProgressWidth(16.0F)
            setProgress(Prefs.spentamount)
        }
    }

    private fun initializeMoferv() {

        rvMofe = mofe_rv
        rvMofe.visibility = View.VISIBLE
        rvMofe.setHasFixedSize(true)
        rvMofe.layoutManager = LinearLayoutManager(this)
        rvMofe.adapter = mAdapter

        rvMofe.addOnItemTouchListener(RecyclerItemClickListener(baseContext, rvMofe, object : RecyclerItemClickListener.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) { }

                    override fun onLongItemClick(view: View, position: Int) {

                        val holder: ItemsAdapter.ViewHolder = ItemsAdapter.ViewHolder(view)

                        if (holder.optionsItemClick.visibility == View.GONE){
                            holder.optionsItemClick.visibility = View.VISIBLE
                        } else {
                            holder.optionsItemClick.visibility = View.GONE
                        }
                    }
                })
        )
    }

    /**
     * dialog to add category
     * */
    @SuppressLint("ResourceAsColor")
    fun dialogUpdate(context: Context, forItem: Boolean, position: Int?) {

        val lytInf = LayoutInflater.from(context)
        val promptsView = lytInf.inflate(R.layout.alert_dialog_update_amount, null)
        val Prefs = customPreference(this, CUSTOM_PREF_NAME);

        val alert = AlertDialog.Builder(context)
        alert.setView(promptsView)

        val input: EditText = promptsView.findViewById(R.id.edtAmt) as EditText
        alert.setPositiveButton("Update", { _, _ -> })
        alert.setNegativeButton("Cancle", { _, _ -> })

        val alertDialog = alert.create()
        alertDialog.setOnShowListener {

            val button_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val button_negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            button_positive.setTextColor(context.resources.getColor(R.color.colorPrimary))
            button_positive.setOnClickListener {

                val amt_update: String = input.text.toString().trim()

                if(!forItem){

                    if(amt_update != ""){

                        if(init_amt_input.text.toString() != "update amount"){

                            val new_update = Prefs.init_amount + amt_update.toInt()
                            init_amt_input.setText(NumberAmountFormat(new_update))
                            Prefs.init_amount = new_update
                        }

                        else {

                            init_amt_input.setText(NumberAmountFormat(amt_update.toInt()))
                            Prefs.init_amount = amt_update.toInt()
                        }

                        alertDialog.dismiss()
                    }

                    else { toast("Enter Amount to update or add") }

                } else {

                    if(amt_update != ""){

                        if (position != null) {

                            val items: Items = database.ItemsDao().findItemById(position)!!
                            Prefs.amount = Prefs.amount + items.itemPrice!!

                            items.itemPrice = amt_update.toInt()
                            Prefs.amount = Prefs.amount - items.itemPrice!!
                            amt_reduction.setText(Prefs.amount.toString())
                            database.ItemsDao().update(items)
                        }
                    }
                    else {
                        toast("Enter amount to change")
                    }

                    alertDialog.dismiss()
                }
            }

            button_negative.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }
}
