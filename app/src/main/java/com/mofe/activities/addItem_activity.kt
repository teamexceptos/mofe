package com.mofe.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.mofe.R
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.amount
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

class addItem_activity : AppCompatActivity() {

    val mActivity: Activity = this@addItem_activity

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var myCalendar: Calendar

    val CUSTOM_PREF_NAME = "amount_data"

    private var inputDate = ""
    private var inputItem = ""
    private var inputItemCost = ""
    private var pickedCategoryItem = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_item)

        setSupportActionBar(toolbarAddTask)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

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

        edtSetDate.setOnClickListener {
            dateAndTime()
            setDate()
        }
    }

    private fun addItems(){

        inputItem = edtItem.text.toString()
        inputItemCost = edtItemCost.text.toString()

        if(inputItem != "" && inputItemCost != "" && inputDate != "" && pickedCategoryItem != ""){

            val date = getCurrentDateTime()
            val datetoString = date.toString("EEE, d MMM yyyy")

            val Prefs = SharedprefManager.customPreference(this, CUSTOM_PREF_NAME);

            if(Prefs.amount > inputItemCost.toInt()){

                Prefs.amount = Prefs.amount - inputItemCost.toInt()

                saveItemMofetoDB(itemname = inputItem,
                        itemcate = pickedCategoryItem,
                        itemcost = inputItemCost.toInt(),
                        itemimg = "",
                        itemduedate = inputDate,
                        itemdateadded = datetoString);

                startActivity(Intent(mActivity, home_activity::class.java))
                finish()

            } else {
                toast("So sad, you have low buget on you right now")
            }

        } else {

            toast("Some of your inputs are empty")
        }
    }

    private fun saveItemMofetoDB(itemname: String, itemcost: Int, itemcate: String, itemimg: String, itemduedate: String, itemdateadded: String) {
        doAsync {
            val item = Items(itemName = itemname, itemCate = itemcate, itemPrice = itemcost, itemImg = itemimg, itemDueDate = itemduedate, itemDateAdded = itemdateadded)
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
        menuInflater.inflate(R.menu.menu_add_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when (id) {
            R.id.action_done -> {
                addItems()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
