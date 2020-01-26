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
import com.mofe.database.entities.Debtors
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.init_amount
import com.mofe.utils.Togetutil
import com.mofe.utils.toString
import kotlinx.android.synthetic.main.activity_add_debtor.*
import kotlinx.android.synthetic.main.activity_add_item.toolbarAddTask
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ${cosmic} on 2/10/19.
 */

class add_debtor_activity : AppCompatActivity() {

    val mActivity: Activity = this@add_debtor_activity

    val homeActivity = home_activity()
    val togetutil = Togetutil()

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var myCalendar: Calendar

    val CUSTOM_PREF_NAME = "amount_data"

    lateinit var database : AppDatabase

    private var inputDate = ""
    private var inputDebtorName = ""
    private var inputDebtCost = ""
    private var pickedCategoryItem = ""
    private var inputDebtorContact = ""
    lateinit var all_debtors: List<Debtors>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noStatusBar()

        setContentView(R.layout.activity_add_debtor)

        setSupportActionBar(toolbarAddTask)

        homeActivity.checkLowCashtoTrack(this)

        database = AppDatabase.getInstance(context = this@add_debtor_activity)
        all_debtors = database.DebtorsDao().all

        edtduedatepay.setOnClickListener {
            dateAndTime()
            setDate()
        }

        debtor_add_action_img.setOnClickListener {
            addDebtor()
        }
    }

    private fun addDebtor() {

        val view = this.currentFocus
        view?.let { v ->

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }

        inputDebtorName = edtdebtorname.text.toString()
        inputDebtCost = edtamountowed.text.toString()
        inputDebtorContact = edtdebtorcontact.text.toString()

        if(inputDebtorName != "" && inputDebtCost != "" && inputDate != "" && inputDebtorContact != "") {

            val date = togetutil.getCurrentDateTime()
            val datetoString = date.toString("EEE, d MMM yyyy")
            val Prefs = SharedprefManager.customPreference(this, CUSTOM_PREF_NAME)

            if(Prefs!!.init_amount > inputDebtCost.toInt()) {

                if(Prefs.amount < inputDebtCost.toInt() && Prefs.amount != 0) {

                    toast("Running on low buget")

                } else {

                    if(Prefs.amount == 0) {

                        Prefs.amount = Prefs.init_amount - inputDebtCost.toInt()

                    } else {

                        Prefs.amount = Prefs.amount - inputDebtCost.toInt()
                    }

                    saveDebtortoDB(personuid = all_debtors.size + 1,
                            personname = inputDebtorName,
                            personcontact = inputDebtorContact,
                            debt = inputDebtCost.toInt(),
                            debtpaydate = inputDate,
                            adddatestring = datetoString,
                            debtoradddate = date.time,
                            debtstatus = "to_pay")
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

    private fun saveDebtortoDB(personuid: Int, personcontact: String, personname: String, debt: Int, debtstatus: String, debtpaydate: String, debtoradddate: Long, adddatestring: String) {

        doAsync {

            val debtor = Debtors(personUid = personuid,
                    personContact = personcontact,
                    personName = personname,
                    debtAmount = debt,
                    debtPayStatus = debtstatus,
                    debtPayDate = debtpaydate,
                    debtAddDate = debtoradddate,
                    debtAddDateString = adddatestring)

            AppDatabase.getInstance(this@add_debtor_activity).DebtorsDao().insert(debtor)
        }
    }

    private fun checkItemInputs() {

        inputDebtorContact = edtdebtorcontact.text.toString().trim()
        inputDebtCost = edtamountowed.text.toString().trim()

        if (inputDebtorContact != "" && inputDebtCost != "") {

            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(mActivity)

            alertDialog.setTitle("Save item")
            alertDialog.setMessage("Want to save this Debtor details?")

            alertDialog.setPositiveButton("Save") { _, _ ->
                addDebtor()
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

    private fun snackbarUpdatedIncome() {
        togetutil.showSnackbar(findViewById<View>(android.R.id.content),
                R.string.update_income,
                R.string.update_action,
                View.OnClickListener {
                    val intent = Intent(this, home_activity::class.java)
                    startActivity(intent)
                })
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
        edtduedatepay.setText(sdf.format(myCalendar.time))

        inputDate = sdf.format(myCalendar.time)
    }

    override fun onBackPressed() {
        checkItemInputs()
    }

    fun noStatusBar() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
