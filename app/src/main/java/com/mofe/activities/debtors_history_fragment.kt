package com.mofe.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.mofe.R
import com.mofe.adapters.DebtorsAdapter
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Debtors
import com.mofe.utils.RecyclerItemClickListener
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.init_amount
import com.mofe.utils.Togetutil
import kotlinx.android.synthetic.main.bottomsheet_debtor_details.*
import kotlinx.android.synthetic.main.fragment_debtors.*

class debtors_history_fragment : Fragment() {

    var debtorsArray: ArrayList<Debtors> = ArrayList()
    lateinit var database : AppDatabase
    private var mAdapter: DebtorsAdapter? = null

    var homeActivity = home_activity()
    var debtperc : Int = 0

    var Prefs : SharedPreferences? = null
    open val CUSTOM_PREF_NAME = "amount_data"
    val historytabsfragment = history_tabs_fragment()
    val togetutil = Togetutil()

    protected var hasCallPermissions: Boolean = false

    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_debtors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = AppDatabase.getInstance(activity!!.baseContext)

        Prefs = SharedprefManager.customPreference(requireContext(), CUSTOM_PREF_NAME)

        bottomSheet = view.findViewById(R.id.debtor_bottom_details_view)
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)

        val debtors = database.DebtorsDao().all

        debtorsArray = debtors as ArrayList<Debtors>

        if(debtorsArray.size != 0) {

            initialize_debtor_rv()
            debtor_empty.visibility = View.GONE

        }
    }

    private fun initialize_debtor_rv() {

        debtors_rv.visibility = View.VISIBLE
        debtors_rv.setHasFixedSize(true)
        debtors_rv.layoutManager = LinearLayoutManager(activity)
        mAdapter = DebtorsAdapter(activity!!.baseContext, debtorsArray, homeActivity)

        debtors_rv.addOnItemTouchListener(RecyclerItemClickListener(activity!!.baseContext, debtors_rv, object : RecyclerItemClickListener.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                initBottomViews(debtorsArray[position], position)
            }

            override fun onLongItemClick(view: View, position: Int) {

            }
        }))

        debtors_rv.adapter = mAdapter
    }

    private fun initBottomViews(debtors: Debtors, position: Int) {

        bottomSheet.visibility = View.VISIBLE

        historytabsfragment.bottomSheetBehaviourStateInit(bottomSheetBehaviour)
        historytabsfragment.bottomSheetBehaviourInit(bottomSheetBehaviour)

        debtperc = (debtors.debtAmount!!.toFloat().div(Prefs!!.init_amount.toFloat()) * 100).toInt()

        bottemsheet_debtor_contact.text = debtors.personContact
        bottemsheet_debtor_name.text = debtors.personName
        bottemsheet_debtor_name.text = debtors.personName
        bottomsheet_due_pay_date.text = debtors.duePayDate
        bottomsheet_debt_amount.text = togetutil.NumberAmountFormat(debtors.debtAmount!!)
        bottomsheet_debt_perc_textdetail.text = " is taken"
        bottomsheet_debt_perc.text = "$debtperc%"

        if(debtors.debtPayStatus == "yes") {

            bottomsheet_debtor_check.visibility = View.GONE
            bottomsheet_debt_perc_textdetail.text = " was returned"
            bottomsheet_debt_date_details.text = "Paid"
        }

        bottomsheet_debtor_check.setOnClickListener {

            mAdapter!!.gottenPayment(position)
            bottomSheet.visibility = View.VISIBLE

        }

        bottom_debtor_call.setOnClickListener {

            callDebtor(debtors.personContact!!)
            bottomSheet.visibility = View.VISIBLE
        }

        bottomsheet_debtor_delete.setOnClickListener {

            mAdapter!!.deleteDebtor(position)
            bottomSheet.visibility = View.VISIBLE

        }

    }

    fun callDebtor(phonenumber: String) {

        performCall(phonenumber)
    }

    @SuppressLint("MissingPermission")
    fun performCall(phone: String) {

        val url = "tel:$phone"
        val intent = Intent(Intent.ACTION_CALL, Uri.parse(url))
        startActivity(intent)
    }

}