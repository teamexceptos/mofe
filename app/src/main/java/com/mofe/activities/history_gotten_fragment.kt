package com.mofe.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.mofe.R
import com.mofe.adapters.ItemsAdapter
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.RecyclerItemClickListener
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.init_amount
import com.mofe.utils.Togetutil
import kotlinx.android.synthetic.main.bottomsheet_item_details.*
import kotlinx.android.synthetic.main.fragment_items_gotten.*

class history_gotten_fragment : Fragment() {

    var gottenItemArray: ArrayList<Items> = ArrayList()
    lateinit var database : AppDatabase
    private var mAdapter: ItemsAdapter? = null
    lateinit var Clickeditem : Items

    var itemperc : Int = 0
    var Prefs : SharedPreferences? = null
    open val CUSTOM_PREF_NAME = "amount_data"

    val historytabsfragment = history_tabs_fragment()
    val togetutil = Togetutil()

    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_items_gotten, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = AppDatabase.getInstance(activity!!.baseContext)

        val items = database.ItemsDao().loadAllByGotten("yes")

        Prefs = SharedprefManager.customPreference(activity!!, CUSTOM_PREF_NAME)

        bottomSheet = view.findViewById(R.id.item_gotten_bottom_details_view)
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)

        gottenItemArray = items as ArrayList<Items>

        if(gottenItemArray.size != 0) {

            initialize_itmes_rv()
            item_empty_gotten.visibility = View.GONE
        }

    }

    private fun initialize_itmes_rv() {

        item_gotten_rv.visibility = View.VISIBLE
        item_gotten_rv.setHasFixedSize(true)
        item_gotten_rv.layoutManager = LinearLayoutManager(activity)
        mAdapter = ItemsAdapter(activity!!.baseContext, gottenItemArray)

        item_gotten_rv.addOnItemTouchListener(RecyclerItemClickListener(activity!!.baseContext, item_gotten_rv, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {

                Clickeditem = gottenItemArray[position]
                initBottomViews(Clickeditem, position)

            }

            override fun onLongItemClick(view: View, position: Int) {

            }
        }))

        item_gotten_rv.adapter = mAdapter
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

        if(items.itemGotten == "yes") {

            bottomsheet_item_check_gotten.visibility = View.GONE
            bottomsheet_item_price_edit.visibility = View.GONE
            bottomsheet_perc_textdetail.text = "was spent"
            bottomsheet_date_details.text = " Gotten"
        }

        bottomsheet_item_delete.setOnClickListener {

            mAdapter!!.deleteItem(position)
            bottomSheet.visibility = View.GONE
        }

        bottomsheet_item_to_share.setOnClickListener {

            shareItemtoget(items)
        }
    }

    fun shareItemtoget(items: Items) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND

        sendIntent.putExtra(Intent.EXTRA_TEXT, "Sharing this item, " + items.itemName +
                " I have gotten already on " + items.itemDueDate +
                "\nI manage my spending through Mofe app, try it out")

        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Send to..."))
    }
}