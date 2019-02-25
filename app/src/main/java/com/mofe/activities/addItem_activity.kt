package com.mofe.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mofe.R
import com.mofe.adapters.ItemsAdapter
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.*
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.init_amount
import kotlinx.android.synthetic.main.activity_add_item.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ${cosmic} on 2/10/19.
 */

class addItem_activity : home_activity() {

    override val mActivity: Activity = this@addItem_activity

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var myCalendar: Calendar

    override val CUSTOM_PREF_NAME = "amount_data"
    private var mAdapter: ItemsAdapter? = null

    private var inputDate = ""
    private var inputItem = ""
    private var inputItemCost = ""
    private var pickedCategoryItem = ""
    lateinit var all_items: List<Items>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noStatusBar()

        setContentView(R.layout.activity_add_item)

        setSupportActionBar(toolbarAddTask)

        addtoCate.setOnClickListener {

            doAsync {

                val Catedatabase = AppDatabase.getInstance(context = this@addItem_activity).CateDao().all

                val cnames: MutableList<CharSequence> = arrayListOf()
                Catedatabase.asSequence().mapTo(cnames){
                    String.format("%s", it.cate)
                }
                uiThread {
                    selector("Categories", cnames) { _, i ->
                        showCateSelect.setText(cnames[i])
                        pickedCategoryItem = cnames[i].toString()
                    }
                }
            }
        }

        expandings()

        doAsync {

            val database = AppDatabase.getInstance(context = this@addItem_activity)
            val itemGotten = database.ItemsDao().loadAllByGotten("yes")

            all_items = database.ItemsDao().all

            sortingforItmes(itemGotten)

            mArrayList = itemGotten as ArrayList<Items>

            uiThread {
                if(mArrayList.size == 0) {

                    gotten_mofe_items_rv.visibility = View.GONE

                } else {

                    gotten_mofe_items_rv.visibility = View.VISIBLE
                    mAdapter = ItemsAdapter(this@addItem_activity, mArrayList,this@addItem_activity, true)
                    initializeMofeGottenrv()

                }
            }
        }

        edtSetDate.setOnClickListener {
            dateAndTime()
            setDate()
        }

        item_mofe_to_add.setOnClickListener {
            addItems()
        }
    }

    private fun initializeMofeGottenrv() {

        rvMofe = gotten_mofe_items_rv
        rvMofe.visibility = View.VISIBLE
        rvMofe.setHasFixedSize(true)
        rvMofe.layoutManager = LinearLayoutManager(this)
        rvMofe.adapter = mAdapter

        rvMofe.addOnItemTouchListener(RecyclerItemClickListener(baseContext, rvMofe, object : RecyclerItemClickListener.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {

                        val holder: ItemsAdapter.ViewHolder = ItemsAdapter.ViewHolder(view)

                        if (holder.optionsItemClick.visibility == View.GONE) {

                            holder.optionsItemClick.visibility = View.VISIBLE

                        } else {

                            holder.optionsItemClick.visibility = View.GONE
                        }
                    }

                    override fun onLongItemClick(view: View, position: Int) {

                    }
                })
        )

    }

    private fun expandings(){

        expand_input_layout.setOnClickListener {

            if(mofe_input_layout.visibility == View.VISIBLE){
                mofe_input_layout.visibility = View.GONE
                expand_input_layout.setImageDrawable(mActivity.getDrawable(R.drawable.ic_expand_more_black_24dp))

            } else {
                mofe_input_layout.visibility = View.VISIBLE
                expand_input_layout.setImageDrawable(mActivity.getDrawable(R.drawable.ic_expand_less_black_24dp))
            }
        }
    }

    private fun addItems(){

        inputItem = edtItem.text.toString()
        inputItemCost = edtItemCost.text.toString()

        if(inputItem != "" && inputItemCost != "" && inputDate != "" && pickedCategoryItem != ""){

            val date = getCurrentDateTime()
            val datetoString = date.toString("EEE, d MMM yyyy")
            val Prefs = SharedprefManager.customPreference(this, CUSTOM_PREF_NAME);

            if(Prefs.init_amount > inputItemCost.toInt()){

                if(Prefs.amount < inputItemCost.toInt() && Prefs.amount != 0) {

                    toast("Running on low buget")

                } else {

                    if(Prefs.amount == 0) {

                        Prefs.amount = Prefs.init_amount - inputItemCost.toInt()

                    } else {

                        Prefs.amount = Prefs.amount - inputItemCost.toInt()
                    }

                    saveItemMofetoDB(itemuid = all_items.size + 1,
                            itemname = inputItem,
                            itemcate = pickedCategoryItem,
                            itemcost = inputItemCost.toInt(),
                            itemimg = "",
                            itemduedate = inputDate,
                            itemdateadded = datetoString,
                            itemLongDateAdded = date.time,
                            itemisgotten = "no");

                }

                startActivity(Intent(mActivity, home_activity::class.java))
                finish()

            } else {

                toast("Set initial amount to spend or Try to delete some wants to spend")
            }

        } else {

            toast("Make all inputs")
        }
    }

    private fun saveItemMofetoDB(itemuid: Int, itemname: String, itemcost: Int, itemcate: String, itemimg: String, itemduedate: String, itemdateadded: String,
                                 itemLongDateAdded: Long,
                                 itemisgotten: String) {

        doAsync {
            val item = Items(itemUid = itemuid,
                    itemName = itemname,
                    itemCate = itemcate,
                    itemPrice = itemcost,
                    itemImg = itemimg,
                    itemDueDate = itemduedate,
                    itemDateAdded = itemdateadded,
                    itemLongDateAdded = itemLongDateAdded,
                    itemisGotten = itemisgotten)

            AppDatabase.getInstance(this@addItem_activity).ItemsDao().insert(item)
        }
    }

    private fun checkItemInputs() {

        inputItem = edtItem.text.toString().trim()
        inputItemCost = edtItemCost.text.toString().trim()

        if (inputItem != "" && inputItemCost != "") {

            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(mActivity)

            alertDialog.setTitle("Save item")
            alertDialog.setMessage("Want to save this Item?")

            alertDialog.setPositiveButton("Save") { _, _ ->
                addItems()
            }

            alertDialog.setNegativeButton("Cancle") { _, _ ->

                startActivity(Intent(mActivity, home_activity::class.java))
                finish()
            }

            val alert: AlertDialog = alertDialog.create()
            alert.show()

        } else {

            startActivity(Intent(mActivity, home_activity::class.java))
            finish()
        }

    }

    private fun dateAndTime() {

        myCalendar = Calendar.getInstance()

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabelDate()
        }
    }

    private fun setDate() {

        val datePickerDialog = DatePickerDialog(this,
                dateSetListener, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()

    }

    private fun updateLabelDate() {

        val myFormat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        edtSetDate.setText(sdf.format(myCalendar.time))

        inputDate = sdf.format(myCalendar.time)
    }

    override fun onBackPressed() {
        checkItemInputs()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_additem_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        when (id) {

            R.id.action_spanding_stats -> {
                startActivity(Intent(mActivity, stats_activity::class.java))
            }

            R.id.action_about -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

}
