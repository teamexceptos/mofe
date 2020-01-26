package com.mofe.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mofe.R
import com.mofe.adapters.ItemsAdapter
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.RecyclerItemClickListener
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.init_amount
import com.mofe.utils.Togetutil
import kotlinx.android.synthetic.main.bottomsheet_item_details.*
import kotlinx.android.synthetic.main.bottomsheet_update_amount.*
import kotlinx.android.synthetic.main.fragment_items_toget.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast

class history_toget_fragment : Fragment() {

    var togetItemArray: ArrayList<Items> = ArrayList()
    lateinit var database : AppDatabase
    private var mAdapter: ItemsAdapter? = null
    lateinit var Clickeditem : Items

    var itemperc : Int = 0
    var itemposition: Int = 0
    var Prefs : SharedPreferences? = null
    open val CUSTOM_PREF_NAME = "amount_data"

    var historytabsfragment = history_tabs_fragment()
    val homeActivity = home_activity()
    val togetutil = Togetutil()
    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_items_toget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = AppDatabase.getInstance(activity!!.baseContext)

        Prefs = SharedprefManager.customPreference(activity!!, CUSTOM_PREF_NAME)
        val items = database.ItemsDao().loadAllByGotten("no")

        bottomSheet = view.findViewById(R.id.item_toget_bottom_details_view)
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)

        togetItemArray = items as ArrayList<Items>

        if(togetItemArray.size != 0) {

            initialize_itmes_rv()
            item_empty_gotten.visibility = View.GONE
        }
    }

    private fun initialize_itmes_rv() {

        item_toget_rv.visibility = View.VISIBLE
        item_toget_rv.setHasFixedSize(true)
        item_toget_rv.layoutManager = LinearLayoutManager(activity)
        mAdapter = ItemsAdapter(activity!!.baseContext, togetItemArray)

        item_toget_rv.addOnItemTouchListener(RecyclerItemClickListener(activity!!.baseContext, item_toget_rv, object : RecyclerItemClickListener.OnItemClickListener {

                override fun onItemClick(view: View, position: Int) {

                    itemposition = position
                    Clickeditem = togetItemArray[position]
                    initBottomViews(Clickeditem, position)

                }

                override fun onLongItemClick(view: View, position: Int) {

                }
            }))

        item_toget_rv.adapter = mAdapter
    }

    private fun initBottomViews(items: Items, position: Int) {

        bottomSheet.visibility = View.VISIBLE

        historytabsfragment.bottomSheetBehaviourStateInit(bottomSheetBehaviour)
        historytabsfragment.bottomSheetBehaviourInit(bottomSheetBehaviour)

        itemperc = (items.itemPrice!!.toFloat().div(Prefs!!.init_amount.toFloat()) * 100).toInt()

        bottemsheet_item_name.text = items.itemName
        bottemsheet_item_cate.text = items.itemCate
        bottomsheet_item_date.text = items.itemDueDate
        bottomsheet_item_price.text = togetutil.NumberAmountFormat(items.itemPrice!!)
        bottomsheet_item_perc.text = "$itemperc%"
        bottomsheet_perc_textdetail.text = " will be spent"
        bottomsheet_date_details.text = " Date to get"

        if(items.itemGotten == "no") {

            bottomsheet_item_check_gotten.visibility = View.VISIBLE
            bottomsheet_item_price_edit.visibility = View.VISIBLE
        }

        bottomsheet_item_check_gotten.setOnClickListener {

            mAdapter!!.gottenItems(position)
            bottomSheet.visibility = View.VISIBLE

        }

        bottomsheet_item_price_edit.setOnClickListener {

            dialogUpdate(position = position, items = items)
            mAdapter!!.itemChanged(position)

        }

        bottomsheet_item_delete.setOnClickListener {

            mAdapter!!.deleteItem(position)
            bottomSheet.visibility = View.GONE
        }

        bottomsheet_item_to_share.setOnClickListener {

            shareItemtoget(items)
        }
    }

    @SuppressLint("ResourceAsColor")
    fun dialogUpdate(position: Int?, items: Items) {

        val lytInf = LayoutInflater.from(context)
        val promptsView = lytInf.inflate(R.layout.bottomsheet_update_amount, null)
        val Prefs = SharedprefManager.customPreference(requireContext(), CUSTOM_PREF_NAME)

        val topnotch = promptsView.findViewById<ImageView>(R.id.imageView12)
        topnotch.visibility = View.GONE

        val itemAmount = promptsView.findViewById<TextView>(R.id.bottomsheet_amount_to_update)
        itemAmount.text = togetutil.NumberAmountFormat(items.itemPrice!!)

        val bottomsheetupdate = promptsView.findViewById<TextView>(R.id.update_amount_action)
        bottomsheetupdate.visibility = View.GONE

        val alert = AlertDialog.Builder(requireContext())
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

                    val database : AppDatabase = AppDatabase.getInstance(requireContext())

                    val items: Items = database.ItemsDao().findItemById(items.itemUid)[0]
                    Prefs!!.amount = Prefs.amount + items.itemPrice!!

                    items.itemPrice = amt_update.toInt()
                    Prefs.amount = Prefs.amount - items.itemPrice!!

                    database.ItemsDao().update(items)

                    bottomsheet_item_price.text = togetutil.NumberAmountFormat(items.itemPrice!!)
                    itemperc = (items.itemPrice!!.toFloat().div(Prefs.init_amount.toFloat()) * 100).toInt()
                    bottomsheet_item_perc.text = "$itemperc%"

                    val newItems = database.ItemsDao().loadAllByGotten("no")
                    togetItemArray = newItems as ArrayList<Items>

                    initialize_itmes_rv()


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

    fun shareItemtoget(items: Items) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND

        sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing this item, " + items.itemName +
                ". I want to get it on or before " + items.itemDueDate +
                "\nI manage my spendings through Mofe app, try it out")

        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Send to..."))
    }
}