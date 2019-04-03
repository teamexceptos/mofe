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
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.RelativeLayout
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
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
import kotlinx.android.synthetic.main.item_bottomsheet_details.*
import kotlinx.android.synthetic.main.item_bottomsheet_details.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * Created by ${cosmic} on 2/10/19.
 */

open class home_activity : AppCompatActivity() {

    open val CUSTOM_PREF_NAME = "amount_data"
    open val mActivity: Activity = this@home_activity

    private var mAdapter: ItemsAdapter? = null
    private var mAdapter2: ItemsAdapter? = null
    val context: Context = this
    var perc: Int = 0
    var totalAmt:Int = 0

    lateinit var rvMofe: RecyclerView
    lateinit var rvMofe_2: RecyclerView
    lateinit var rView: RelativeLayout
    var mArrayList: ArrayList<Items> = ArrayList()
    var mArrayList2: ArrayList<Items> = ArrayList()
    lateinit var database : AppDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noStatusBar()

        setContentView(R.layout.activity_home)

        val Prefs = customPreference(this, CUSTOM_PREF_NAME);

        database = AppDatabase.getInstance(context = this@home_activity)

        doAsync {

            val items = database.ItemsDao().loadAllByGotten("no")
            val items2 = database.ItemsDao().loadAllByGotten("yes")

            sortingforItmes(items)
            sortingforItmes(items2)

            mArrayList = items as ArrayList<Items>
            mArrayList2 = items2 as ArrayList<Items>

            rView = no_items_added
            rvMofe = to_get_rv
            rvMofe_2 = gotten_rv

            uiThread {

                if(mArrayList.size == 0 && mArrayList2.size == 0) {

                    rView.visibility = View.VISIBLE

                } else {

                    mAdapter2 = ItemsAdapter(this@home_activity, mArrayList2,this@home_activity, true)
                    mAdapter = ItemsAdapter(this@home_activity, mArrayList,this@home_activity, false)

                    initializeMoferv()
                    initializeMofeGottenrv()
                }
            }

        }

        to_get_heading.setOnClickListener {

            if(to_get_rv.visibility == View.VISIBLE) {
                to_get_rv.visibility = View.GONE
                expand1.setImageDrawable(mActivity.getDrawable(R.drawable.ic_expand_more_black_24dp))

            } else {
                to_get_rv.visibility = View.VISIBLE
                expand1.setImageDrawable(mActivity.getDrawable(R.drawable.ic_expand_less_black_24dp))
            }
        }

        gotten_heading.setOnClickListener {

            if(gotten_rv.visibility == View.VISIBLE) {
                gotten_rv.visibility = View.GONE
                expand2.setImageDrawable(mActivity.getDrawable(R.drawable.ic_expand_more_black_24dp))

            } else {
                gotten_rv.visibility = View.VISIBLE
                expand2.setImageDrawable(mActivity.getDrawable(R.drawable.ic_expand_less_black_24dp))
            }
        }

        val date = getCurrentDateTime()
        val datetoString = date.toString("EEE, d MMM yyyy")
        Prefs.lastdate = datetoString
        last_date_update.setText(datetoString)

        amt_reduction.setText(NumberAmountFormat(Prefs.amount))
        init_amt_input.setText(NumberAmountFormat(Prefs.init_amount))
        totalAmt = Prefs.init_amount

        edit_amt.setOnClickListener {
            dialogUpdate(this);
        }

        add_item_toget_fab.setOnClickListener {
            startActivity(Intent(mActivity, addItem_activity::class.java))
            finish()
        }

        circularProgressBar(Prefs.init_amount, Prefs.spentamount)

    }

    fun itemPriceChange(): OnItemPriceChange {
        return object : OnItemPriceChange {
            override fun ItemPrice(price: Int) {

            }
        }
    }

    fun circularProgressBar(amount: Int, spentamount: Int){

        cp_bar.run {
            setRounded(true)
            setMaxProgress(amount.toFloat())
            setProgressWidth(21.0F)
            setProgress(spentamount.toFloat())
            perc = getProgressPercentage()
            actual_money_spent.text = "$perc%"

            if(perc >= 80){
                setProgressColor(context.getColor(R.color.red))
            } else {
                setProgressColor(context.getColor(R.color.lime_progress_100))
            }

            setProgressBackgroundColor(context.getColor(R.color.colorWhite))
        }
    }

    private fun initializeMoferv() {

        rvMofe = to_get_rv
        rvMofe.visibility = View.VISIBLE
        rvMofe.setHasFixedSize(true)
        rvMofe.layoutManager = LinearLayoutManager(this)
        rvMofe.adapter = mAdapter

        rvMofe.addOnItemTouchListener(RecyclerItemClickListener(baseContext, rvMofe, object : RecyclerItemClickListener.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {

                        val sheet = itemSheetFragment()
                        var itemperc : Int

                        itemperc = (mArrayList[position].itemPrice!!.toFloat().div(totalAmt.toFloat()) * 100).toInt()

                        sheet.itemClicked().ItemClicked(mArrayList[position], itemperc, position, mAdapter!!)
                        sheet.show(supportFragmentManager, "BottomSheetFragment")

                    }

                    override fun onLongItemClick(view: View, position: Int) {

                    }
                })
        )
    }

    private fun initializeMofeGottenrv() {

        rvMofe_2 = gotten_rv
        rvMofe_2.visibility = View.VISIBLE
        rvMofe_2.setHasFixedSize(true)
        rvMofe_2.layoutManager = LinearLayoutManager(this)
        rvMofe_2.adapter = mAdapter2

        rvMofe_2.addOnItemTouchListener(RecyclerItemClickListener(baseContext, rvMofe_2, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {

                val sheet = itemSheetFragment()
                var itemperc: Int

                itemperc = (mArrayList2[position].itemPrice!!.toFloat() / totalAmt.toFloat() * 100).toInt()

                sheet.itemClicked().ItemClicked(mArrayList2[position], itemperc, position, mAdapter2!!)
                sheet.show(supportFragmentManager, "BottomSheetFragment")

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

            button_positive.setTextColor(context.getColor(R.color.colorPrimary))
            button_positive.setOnClickListener {

                val amt_update: String = input.text.toString().trim()

                if(amt_update != "") {

                    val new_update = Prefs.init_amount + amt_update.toInt()
                    init_amt_input.text = NumberAmountFormat(new_update)

                    Prefs.init_amount = new_update
                    Prefs.amount = Prefs.amount + amt_update.toInt()
                    amt_reduction.text = NumberAmountFormat(Prefs.amount)

                    circularProgressBar(Prefs.init_amount, Prefs.spentamount)

                    val sheet = itemSheetFragment()
                    sheet.activity!!.finish()

                    alertDialog.dismiss()
                }

                else { toast("Enter Amount to update or add") }

                alertDialog.dismiss()
            }

            button_negative.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }

    fun noStatusBar() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}

class itemSheetFragment: SuperBottomSheetFragment() {

    lateinit var items : Items
    private var mAdapter: ItemsAdapter? = null
    var itemperc : Int = 0
    var position : Int = 0
    var mItemSheetFragment : itemSheetFragment = this@itemSheetFragment

    fun itemClicked(): OnItemGotten<Items> {
        return object : OnItemGotten<Items> {
            override fun ItemClicked(obj: Items, perc: Int, index: Int, adapter: ItemsAdapter) {
                items = obj
                this@itemSheetFragment.itemperc = perc
                position = index
                mAdapter = adapter
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.item_bottomsheet_details, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.bottemsheet_item_name.text = items.itemName
        view.bottemsheet_item_cate.text = items.itemCate
        view.bottomsheet_item_date.text = items.itemDueDate
        view.bottomsheet_item_price.text = NumberAmountFormat(items.itemPrice!!)
        view.bottomsheet_item_perc.text = "$itemperc%"
        view.bottomsheet_perc_textdetail.text = " will be spent"

        if(items.itemGotten == "yes"){
            view.bottomsheet_item_check_gotten.visibility = View.GONE
            view.bottomsheet_item_price_edit.visibility = View.GONE
            view.bottomsheet_perc_textdetail.text = "was spent"
        }

        view.bottomsheet_item_check_gotten.setOnClickListener {
            mItemSheetFragment.dismiss()
            mItemSheetFragment.isCancelable = true
            mAdapter!!.gottenItems(position) }

        view.bottomsheet_item_price_edit.setOnClickListener {
            dialogUpdate(position = position)
            mAdapter!!.notifyDataSetChanged()}

        view.bottomsheet_item_delete.setOnClickListener {
            mItemSheetFragment.dismiss()
            mItemSheetFragment.isCancelable = true
            mAdapter!!.deleteItem(position) }

        view.bottomsheet_item_to_share.setOnClickListener {
            mItemSheetFragment.dismiss()
            mItemSheetFragment.isCancelable = true
            shareItemtoget(items)
            }
    }

    fun shareItemtoget(items: Items) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND

        if(items.itemGotten == "no"){
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing this item, " + items.itemName +
                    ". I want to get on or before " + items.itemDueDate +
                    "\nI manage my spendings through Mofe app, try it out")

        } else {
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing this item, " + items.itemName +
                    " I have gotten already on " + items.itemDueDate +
                    "\nI manage my spendings through Mofe app, try it out")
        }

        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Send to..."))
    }

    @SuppressLint("ResourceAsColor")
    fun dialogUpdate(position: Int?) {

        val lytInf = LayoutInflater.from(mItemSheetFragment.requireContext())
        val promptsView = lytInf.inflate(R.layout.alert_dialog_update_amount, null)
        val Prefs = customPreference(mItemSheetFragment.requireContext(), "amount_data");

        val alert = AlertDialog.Builder(mItemSheetFragment.requireContext())
        alert.setView(promptsView)

        val input: EditText = promptsView.findViewById(R.id.edtAmt) as EditText
        alert.setPositiveButton("Update", { _, _ -> })
        alert.setNegativeButton("Cancle", { _, _ -> })

        val alertDialog = alert.create()
        alertDialog.setOnShowListener {

            val button_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val button_negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            button_positive.setOnClickListener {

                val amt_update: String = input.text.toString().trim()

                if (position != null) {

                    val database : AppDatabase = AppDatabase.getInstance(context = mItemSheetFragment.requireContext())

                    val items: Items = database.ItemsDao().findItemById(items.itemUid)[0]
                    Prefs.amount = Prefs.amount + items.itemPrice!!

                    items.itemPrice = amt_update.toInt()
                    Prefs.amount = Prefs.amount - items.itemPrice!!
                    bottomsheet_item_price.text = NumberAmountFormat(items.itemPrice!!)
//                    home_activity().itemPriceChange().ItemPrice(Prefs.amount)
                    database.ItemsDao().update(items)
                }

                else {

                    toast("Enter amount to change")
                }

                alertDialog.dismiss()
            }

            button_negative.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }

}

interface OnItemGotten<T> {

    fun ItemClicked(obj: Items, perc: Int, index: Int, adapter: ItemsAdapter)
}

interface OnItemPriceChange {

    fun ItemPrice(price: Int)
}
