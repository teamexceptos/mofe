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
import com.mofe.R
import com.mofe.adapters.ItemsAdapter
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.NumberAmountFormat
import com.mofe.utils.RecyclerItemClickListener
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.customPreference
import com.mofe.utils.SharedprefManager.lastdate
import com.mofe.utils.getCurrentDateTime
import com.mofe.utils.toString
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * Created by ${cosmic} on 2/10/19.
 */

class home_activity : AppCompatActivity() {

    val CUSTOM_PREF_NAME = "amount_data"
    val mActivity: Activity = this@home_activity
    private var mAdapter: ItemsAdapter? = null

    lateinit var rvMofe: RecyclerView
    var mArrayList: ArrayList<Items> = ArrayList()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val Prefs = customPreference(this, CUSTOM_PREF_NAME);

        doAsync {

            val database = AppDatabase.getInstance(context = this@home_activity)
            val bills = database.ItemsDao().all
            mArrayList = bills as ArrayList<Items>

            uiThread {
                if(mArrayList.size == 0) {

                    mofe_rv.visibility = View.GONE
                    no_items_added.visibility = View.VISIBLE

                } else {
                    mofe_rv.visibility = View.VISIBLE
                    no_items_added.visibility = View.GONE
                    mAdapter = ItemsAdapter(this@home_activity, mArrayList)

                    initializeMoferv()

                }
            }
        }

        val date = getCurrentDateTime()
        val datetoString = date.toString("EEE, d MMM yyyy")
        Prefs.lastdate = datetoString

        if(amt_input.text.toString().equals("update amount") && Prefs.amount == 0 ){

            last_date_update.setText(datetoString)

        } else {

            last_date_update.setText(datetoString)
            amt_input.setText(NumberAmountFormat(Prefs.amount))
        }

        edit_amt.setOnClickListener {
            dialogUpdate(this);
        }

        add_item_toget_fab.setOnClickListener {
            startActivity(Intent(mActivity, addItem_activity::class.java))
            finish()
        }
    }

    private fun initializeMoferv() {

        rvMofe = mofe_rv
        rvMofe.setHasFixedSize(true)
        rvMofe.layoutManager = LinearLayoutManager(view.context)
        rvMofe.adapter = mAdapter

        rvMofe.addOnItemTouchListener(RecyclerItemClickListener(baseContext, rvMofe, object : RecyclerItemClickListener.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {

                    }

                    override fun onLongItemClick(view: View, position: Int) {

                    }
                })
        )
    }

    /**
     * dialog to add category
     * */
    @SuppressLint("ResourceAsColor")
    fun dialogUpdate(context: Context) {

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

                if(amt_update != ""){

                    if(amt_input.text.toString() != "update amount"){
                        val new_update = Prefs.amount + amt_update.toInt()
                        amt_input.setText(NumberAmountFormat(new_update))
                        Prefs.amount = new_update
                    }

                    else {

                        amt_input.setText(NumberAmountFormat(amt_update.toInt()))
                        Prefs.amount = amt_update.toInt()
                    }

                    alertDialog.dismiss()
                }
                else {

                    toast("Enter Amount to update or add")
                }
            }

            button_negative.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }
}
