package com.mofe.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.mofe.R
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.init_amount
import com.mofe.utils.getCurrentDateTime
import com.mofe.utils.toString
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
                all_items = database.ItemsDao().all

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

        edtSetDate.setOnClickListener {
            dateAndTime()
            setDate()
        }

        item_mofe_to_add.setOnClickListener {
            addItems()
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
                startActivity(Intent(mActivity, about_activity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
