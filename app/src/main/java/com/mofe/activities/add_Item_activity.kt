package com.mofe.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.mofe.R
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.init_amount
import com.mofe.utils.Togetutil
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

class add_Item_activity : AppCompatActivity() {

    val mActivity: Activity = this@add_Item_activity
    val homeActivity = home_activity()
    val togetutil = Togetutil()

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var myCalendar: Calendar

    lateinit var database : AppDatabase

    val CUSTOM_PREF_NAME = "amount_data"

    private var inputDate = ""
    private var inputItem = ""
    private var inputItemCost = ""
    private var pickedCategoryItem = ""
    lateinit var all_items: List<Items>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noStatusBar()

        setContentView(R.layout.activity_add_item)

        homeActivity.checkLowCashtoTrack(this)

        setSupportActionBar(toolbarAddTask)

        add_to_cate.setOnClickListener {

            doAsync {

                database = AppDatabase.getInstance(context = this@add_Item_activity)

                val Catedatabase = AppDatabase.getInstance(context = this@add_Item_activity).CateDao().all
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

        item_add_action_img.setOnClickListener {
            addItems()
        }
    }

    private fun addItems() {

        val view = this.currentFocus
        view?.let { v ->

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }

        inputItem = edtItem.text.toString()
        inputItemCost = edtItemCost.text.toString()

        if(inputItem != "" && inputItemCost != "" && inputDate != "" && pickedCategoryItem != ""){

            val date = togetutil.getCurrentDateTime()
            val datetoString = date.toString("EEE, d MMM yyyy")
            val Prefs = SharedprefManager.customPreference(this, CUSTOM_PREF_NAME)

            if(Prefs!!.init_amount > inputItemCost.toInt()){

                if(Prefs.amount < inputItemCost.toInt() && Prefs.amount != 0) {

                    toast("Running on low buget")

                } else {

                    if(Prefs.amount == 0) {

                        Prefs.amount = Prefs.init_amount - inputItemCost.toInt()

                    } else {

                        Prefs.amount = Prefs.amount - inputItemCost.toInt()
                    }

                    saveItemtoDB(itemuid = all_items.size + 1,
                            itemname = inputItem,
                            itemcate = pickedCategoryItem,
                            itemcost = inputItemCost.toInt(),
                            itemimg = "",
                            itemduedate = inputDate,
                            itemdateadded = datetoString,
                            itemLongDateAdded = date.time,
                            itemisgotten = "no")
                }

                startActivity(Intent(mActivity, home_activity::class.java))
                finish()

            } else {

                snackbarUpdatedIncome()
            }

        } else {

            toast("Make all inputs")
        }
    }

    private fun saveItemtoDB(itemuid: Int, itemname: String, itemcost: Int, itemcate: String, itemimg: String, itemduedate: String, itemdateadded: String,
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

            AppDatabase.getInstance(this@add_Item_activity).ItemsDao().insert(item)
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

            alertDialog.setNegativeButton("Cancel") { _, _ ->

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

    private fun snackbarUpdatedIncome() {
        togetutil.showSnackbar(findViewById<View>(android.R.id.content),
                R.string.update_income,
                R.string.update_action,
                View.OnClickListener {
                    val intent = Intent(this, home_activity::class.java)
                    startActivity(intent)
                })
    }

    override fun onBackPressed() {
        checkItemInputs()
    }

    fun noStatusBar() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
